package com.cibermax.ecommerce.modules.subCategoria.integration;

import com.cibermax.ecommerce.config.TestContainersConfig;
import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.categoria.repository.CategoriaRepository;
import com.cibermax.ecommerce.modules.subCategoria.entity.SubCategoria;
import com.cibermax.ecommerce.modules.subCategoria.repository.SubCategoriaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@WithMockUser(username = "subcategoria-integration-user", roles = "USER")
class SubCategoriaIntegrationTest {

    private static final String BASE_URL = "/api/v1/subcategorias";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private SubCategoriaRepository subCategoriaRepository;

    @BeforeEach
    void cleanBeforeEachTest() {
        clearDatabase();
    }

    @AfterEach
    void cleanAfterEachTest() {
        clearDatabase();
    }

    @Test
    @DisplayName("POST registra una subcategoría y la relaciona con una categoría existente")
    void create_persistsSubCategoryWithParentCategory() throws Exception {
        Categoria categoria = saveCategory("Computadoras");

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Laptops\",\"categoriaId\":\""
                                + categoria.getId() + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Subcategoría creada correctamente"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.nombre").value("Laptops"))
                .andExpect(jsonPath("$.data.categoriaId").value(categoria.getId().toString()))
                .andExpect(jsonPath("$.data.categoriaNombre").value("Computadoras"))
                .andExpect(jsonPath("$.data.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.data.updatedAt").isNotEmpty());

        SubCategoria saved = subCategoriaRepository.findAll().get(0);
        assertThat(saved.getNombre()).isEqualTo("Laptops");
        assertThat(saved.getCategoria().getId()).isEqualTo(categoria.getId());
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("POST responde 400 cuando faltan nombre y categoría")
    void create_rejectsMissingRequiredData() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        assertThat(subCategoriaRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST responde 400 cuando el nombre está vacío")
    void create_rejectsBlankName() throws Exception {
        Categoria categoria = saveCategory("Computadoras");

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"   \",\"categoriaId\":\""
                                + categoria.getId() + "\"}"))
                .andExpect(status().isBadRequest());

        assertThat(subCategoriaRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST responde 404 cuando la categoría padre no existe")
    void create_rejectsUnknownParentCategory() throws Exception {
        UUID unknownCategoryId = UUID.randomUUID();

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Laptops\",\"categoriaId\":\""
                                + unknownCategoryId + "\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        assertThat(subCategoriaRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST responde 409 cuando el nombre ya existe en la categoría")
    void create_rejectsDuplicateNameInSameCategory() throws Exception {
        Categoria categoria = saveCategory("Computadoras");
        saveSubCategory("Laptops", categoria);

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"laptops\",\"categoriaId\":\""
                                + categoria.getId() + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        assertThat(subCategoriaRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("POST permite el mismo nombre bajo otra categoría")
    void create_allowsSameNameInDifferentCategory() throws Exception {
        Categoria computers = saveCategory("Computadoras");
        Categoria phones = saveCategory("Celulares");
        saveSubCategory("Accesorios", computers);

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Accesorios\",\"categoriaId\":\""
                                + phones.getId() + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.categoriaId").value(phones.getId().toString()));

        assertThat(subCategoriaRepository.count()).isEqualTo(2);
    }

    private Categoria saveCategory(String nombre) {
        return categoriaRepository.saveAndFlush(new Categoria(null, nombre, null, null));
    }

    private SubCategoria saveSubCategory(String nombre, Categoria categoria) {
        return subCategoriaRepository.saveAndFlush(new SubCategoria(null, nombre, categoria, null, null));
    }

    private void clearDatabase() {
        subCategoriaRepository.deleteAll();
        subCategoriaRepository.flush();
        categoriaRepository.deleteAll();
        categoriaRepository.flush();
    }
}
