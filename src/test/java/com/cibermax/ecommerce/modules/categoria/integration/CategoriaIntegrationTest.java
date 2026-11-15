package com.cibermax.ecommerce.modules.categoria.integration;

import com.cibermax.ecommerce.config.TestContainersConfig;
import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.categoria.repository.CategoriaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@WithMockUser(username = "categoria-integration-user", roles = "USER")
class CategoriaIntegrationTest {

    private static final String BASE_URL = "/api/v1/categorias";
    private static final String LIST_CACHE = "categorias:list:v3";
    private static final String DETAIL_CACHE = "categorias:id:v3";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void cleanBeforeEachTest() {
        clearCaches();
        categoriaRepository.deleteAll();
        categoriaRepository.flush();
    }

    @AfterEach
    void cleanAfterEachTest() {
        clearCaches();
        categoriaRepository.deleteAll();
        categoriaRepository.flush();
    }

    @Test
    @DisplayName("conecta con PostgreSQL de Testcontainers")
    void connectsToPostgres() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("POST crea una categoría y persiste timestamps en PostgreSQL")
    void create_persistsCategoryAndTimestamps() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Computadoras\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.nombre").value("Computadoras"))
                .andExpect(jsonPath("$.data.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.data.updatedAt").isNotEmpty());

        Categoria saved = categoriaRepository.findAll().get(0);
        assertThat(saved.getNombre()).isEqualTo("Computadoras");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("POST rechaza un nombre vacío")
    void create_rejectsBlankName() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"   \"}"))
                .andExpect(status().isBadRequest());

        assertThat(categoriaRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST rechaza una categoría duplicada")
    void create_rejectsDuplicateName() throws Exception {
        saveCategory("Computadoras");

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Computadoras\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        assertThat(categoriaRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("GET lista categorías persistidas")
    void findAll_returnsPersistedCategories() throws Exception {
        saveCategory("Computadoras");
        saveCategory("Periféricos");

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("GET lista devuelve lista vacía cuando no hay categorías")
    void findAll_returnsEmptyList() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("GET lista recupera el resultado desde Redis")
    void findAll_usesRedisCache() throws Exception {
        saveCategory("Computadoras");

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nombre").value("Computadoras"));

        categoriaRepository.deleteAll();
        categoriaRepository.flush();

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nombre").value("Computadoras"));

        assertThat(cache(LIST_CACHE).get("all")).isNotNull();
    }

    @Test
    @DisplayName("POST invalida la caché Redis del listado")
    void create_evictsListCache() throws Exception {
        saveCategory("Computadoras");
        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk());
        assertThat(cache(LIST_CACHE).get("all")).isNotNull();

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Periféricos\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("GET por ID devuelve una categoría existente")
    void findById_returnsExistingCategory() throws Exception {
        Categoria saved = saveCategory("Computadoras");

        mockMvc.perform(get(BASE_URL + "/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.data.nombre").value("Computadoras"));
    }

    @Test
    @DisplayName("GET por ID responde 404 cuando no existe")
    void findById_returnsNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET por ID responde 400 cuando el UUID es inválido")
    void findById_rejectsInvalidUuid() throws Exception {
        mockMvc.perform(get(BASE_URL + "/uuid-invalido"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET por ID recupera el resultado desde Redis")
    void findById_usesRedisCache() throws Exception {
        Categoria saved = saveCategory("Computadoras");

        mockMvc.perform(get(BASE_URL + "/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Computadoras"));

        categoriaRepository.deleteAll();
        categoriaRepository.flush();

        mockMvc.perform(get(BASE_URL + "/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Computadoras"));

        assertThat(cache(DETAIL_CACHE).get(saved.getId())).isNotNull();
    }

    @Test
    @DisplayName("GET buscar devuelve coincidencias sin distinguir mayúsculas")
    void search_returnsCaseInsensitiveMatches() throws Exception {
        saveCategory("Computadoras");
        saveCategory("Componentes");
        saveCategory("Periféricos");

        mockMvc.perform(get(BASE_URL + "/buscar").param("nombre", "com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("GET buscar devuelve lista vacía sin coincidencias")
    void search_returnsEmptyListWithoutMatches() throws Exception {
        saveCategory("Computadoras");

        mockMvc.perform(get(BASE_URL + "/buscar").param("nombre", "inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("PUT actualiza una categoría y persiste el cambio")
    void update_persistsChangedName() throws Exception {
        Categoria saved = saveCategory("Computadoras");

        mockMvc.perform(put(BASE_URL + "/" + saved.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Laptops\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Laptops"))
                .andExpect(jsonPath("$.data.updatedAt").isNotEmpty());

        Categoria updated = categoriaRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getNombre()).isEqualTo("Laptops");
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("PUT rechaza un nombre duplicado")
    void update_rejectsDuplicateName() throws Exception {
        Categoria first = saveCategory("Computadoras");
        saveCategory("Laptops");

        mockMvc.perform(put(BASE_URL + "/" + first.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Laptops\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT responde 404 para una categoría inexistente")
    void update_returnsNotFoundForMissingCategory() throws Exception {
        mockMvc.perform(put(BASE_URL + "/" + UUID.randomUUID())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Laptops\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT responde 400 cuando falta el nombre")
    void update_rejectsBlankName() throws Exception {
        Categoria saved = saveCategory("Computadoras");

        mockMvc.perform(put(BASE_URL + "/" + saved.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT invalida la caché de detalle")
    void update_evictsDetailCache() throws Exception {
        Categoria saved = saveCategory("Computadoras");
        mockMvc.perform(get(BASE_URL + "/" + saved.getId())).andExpect(status().isOk());
        assertThat(cache(DETAIL_CACHE).get(saved.getId())).isNotNull();

        mockMvc.perform(put(BASE_URL + "/" + saved.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Laptops\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_URL + "/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Laptops"));
    }

    private Categoria saveCategory(String nombre) {
        return categoriaRepository.saveAndFlush(new Categoria(null, nombre, null, null));
    }

    private Cache cache(String name) {
        Cache cache = cacheManager.getCache(name);
        assertThat(cache).as("La caché %s debe existir", name).isNotNull();
        return cache;
    }

    private void clearCaches() {
        Cache listCache = cacheManager.getCache(LIST_CACHE);
        if (listCache != null) {
            listCache.clear();
        }

        Cache detailCache = cacheManager.getCache(DETAIL_CACHE);
        if (detailCache != null) {
            detailCache.clear();
        }
    }
}
