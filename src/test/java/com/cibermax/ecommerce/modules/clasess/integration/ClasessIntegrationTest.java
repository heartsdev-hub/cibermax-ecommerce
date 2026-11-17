package com.cibermax.ecommerce.modules.clasess.integration;

import com.cibermax.ecommerce.config.TestContainersConfig;
import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.categoria.repository.CategoriaRepository;
import com.cibermax.ecommerce.modules.clasess.entity.Clasess;
import com.cibermax.ecommerce.modules.clasess.repository.ClasessRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
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
@WithMockUser(username = "clasess-integration-user", roles = "USER")
class ClasessIntegrationTest {

    private static final String BASE_URL = "/api/v1/clasess";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ClasessRepository clasessRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        clasessRepository.deleteAll();
        clasessRepository.flush();
        categoriaRepository.deleteAll();
        categoriaRepository.flush();
    }

    @Test
    @DisplayName("persiste Clasess con código heredado y categoría asociada")
    void persistsClassWithCategoryAndCode() {
        Categoria categoria = categoriaRepository.saveAndFlush(
                new Categoria(null, "Audio", 10, null, null)
        );
        Clasess first = clasessRepository.saveAndFlush(
                new Clasess(null, 1010, "Audífonos", categoria, null, null)
        );
        Clasess second = clasessRepository.saveAndFlush(
                new Clasess(null, 1020, "Parlantes", categoria, null, null)
        );

        List<Clasess> classes = clasessRepository.findByCategoriaIdOrderByCodigoAsc(categoria.getId());

        assertThat(classes).extracting(Clasess::getCodigo)
                .containsExactly(1010, 1020);
        assertThat(first.getCategoria().getCodigo()).isEqualTo(10);
        assertThat(second.getCreatedAt()).isNotNull();
        assertThat(second.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("POST registra una Clasess con código basado en la categoría")
    void create_persistsClassWithCategoryCode() throws Exception {
        Categoria categoria = saveCategory("Audio", 10);

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Mouse\",\"categoriaId\":\"" + categoria.getId() + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.codigo").value(1010))
                .andExpect(jsonPath("$.data.nombre").value("Mouse"))
                .andExpect(jsonPath("$.data.categoria").value("Audio"))
                .andExpect(jsonPath("$.data.categoriaId").doesNotExist())
                .andExpect(jsonPath("$.data.categoriaCodigo").doesNotExist());

        Clasess saved = clasessRepository.findAll().get(0);
        assertThat(saved.getCodigo()).isEqualTo(1010);
        assertThat(saved.getCategoria().getId()).isEqualTo(categoria.getId());
    }

    @Test
    @DisplayName("GET lista las Clasess ordenadas por código")
    void findAll_returnsPersistedClasses() throws Exception {
        Categoria categoria = saveCategory("Audio", 10);
        clasessRepository.saveAndFlush(new Clasess(null, 1020, "Parlantes", categoria, null, null));
        clasessRepository.saveAndFlush(new Clasess(null, 1010, "Audífonos", categoria, null, null));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].codigo").value(1010))
                .andExpect(jsonPath("$.data[1].codigo").value(1020));
    }

    @Test
    @DisplayName("GET lista devuelve una lista vacía cuando no existen Clasess")
    void findAll_returnsEmptyList() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("GET por ID devuelve una Clasess existente")
    void findById_returnsExistingClass() throws Exception {
        Categoria categoria = saveCategory("Audio", 10);
        Clasess saved = clasessRepository.saveAndFlush(
                new Clasess(null, 1010, "Audífonos", categoria, null, null)
        );

        mockMvc.perform(get(BASE_URL + "/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.data.codigo").value(1010))
                .andExpect(jsonPath("$.data.categoria").value("Audio"));
    }

    @Test
    @DisplayName("GET por ID responde 404 cuando la Clasess no existe")
    void findById_returnsNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT actualiza el nombre y conserva el código")
    void update_persistsNameAndKeepsCode() throws Exception {
        Categoria categoria = saveCategory("Audio", 10);
        Clasess saved = clasessRepository.saveAndFlush(
                new Clasess(null, 1010, "Audífonos", categoria, null, null)
        );

        mockMvc.perform(put(BASE_URL + "/" + saved.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Audífonos inalámbricos\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.codigo").value(1010))
                .andExpect(jsonPath("$.data.nombre").value("Audífonos inalámbricos"));

        Clasess updated = clasessRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getNombre()).isEqualTo("Audífonos inalámbricos");
        assertThat(updated.getCodigo()).isEqualTo(1010);
    }

    @Test
    @DisplayName("PUT rechaza nombres duplicados dentro de la misma categoría")
    void update_rejectsDuplicateNameInCategory() throws Exception {
        Categoria categoria = saveCategory("Audio", 10);
        Clasess first = clasessRepository.saveAndFlush(
                new Clasess(null, 1010, "Audífonos", categoria, null, null)
        );
        clasessRepository.saveAndFlush(
                new Clasess(null, 1020, "Parlantes", categoria, null, null)
        );

        mockMvc.perform(put(BASE_URL + "/" + first.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Parlantes\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT responde 404 para una Clasess inexistente")
    void update_returnsNotFoundForMissingClass() throws Exception {
        mockMvc.perform(put(BASE_URL + "/" + UUID.randomUUID())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Parlantes\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT responde 400 cuando el nombre está vacío")
    void update_rejectsBlankName() throws Exception {
        Categoria categoria = saveCategory("Audio", 10);
        Clasess saved = clasessRepository.saveAndFlush(
                new Clasess(null, 1010, "Audífonos", categoria, null, null)
        );

        mockMvc.perform(put(BASE_URL + "/" + saved.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    private Categoria saveCategory(String nombre, int codigo) {
        return categoriaRepository.saveAndFlush(new Categoria(null, nombre, codigo, null, null));
    }

    @Test
    @DisplayName("crea la tabla Clasess con el nombre configurado")
    void createsExpectedTableName() {
        String tableName = jdbcTemplate.queryForObject(
                "SELECT relname FROM pg_class WHERE oid = 'public.\"Clasess\"'::regclass",
                String.class
        );

        assertThat(tableName).isEqualTo("Clasess");
    }
}
