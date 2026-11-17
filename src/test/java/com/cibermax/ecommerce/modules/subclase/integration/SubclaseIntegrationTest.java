package com.cibermax.ecommerce.modules.subclase.integration;

import com.cibermax.ecommerce.config.TestContainersConfig;
import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.categoria.repository.CategoriaRepository;
import com.cibermax.ecommerce.modules.clasess.entity.Clasess;
import com.cibermax.ecommerce.modules.clasess.repository.ClasessRepository;
import com.cibermax.ecommerce.modules.subclase.entity.Subclase;
import com.cibermax.ecommerce.modules.subclase.repository.SubclaseRepository;
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
@WithMockUser(username = "subclase-integration-user", roles = "USER")
class SubclaseIntegrationTest {

    private static final String BASE_URL = "/api/v1/subclases";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ClasessRepository clasessRepository;

    @Autowired
    private SubclaseRepository subclaseRepository;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        subclaseRepository.deleteAll();
        subclaseRepository.flush();
        clasessRepository.deleteAll();
        clasessRepository.flush();
        categoriaRepository.deleteAll();
        categoriaRepository.flush();
    }

    @Test
    @DisplayName("POST registra una subclase con código basado en la Clasess")
    void create_persistsSubclaseAndHierarchy() throws Exception {
        Clasess clasess = saveClasess("Audio", 10, "Audífonos", 1010);

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Inalámbricos\",\"clasessId\":\"" + clasess.getId() + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.codigo").value(101010))
                .andExpect(jsonPath("$.data.nombre").value("Inalámbricos"))
                .andExpect(jsonPath("$.data.categoria").value("Audio"))
                .andExpect(jsonPath("$.data.clase").value("Audífonos"))
                .andExpect(jsonPath("$.data.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.data.updatedAt").isNotEmpty());

        Subclase saved = subclaseRepository.findAll().get(0);
        assertThat(saved.getCodigo()).isEqualTo(101010);
        assertThat(saved.getClasess().getId()).isEqualTo(clasess.getId());
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("POST incrementa el código de subclase dentro de la misma Clasess")
    void create_incrementsCodeForSameClass() throws Exception {
        Clasess clasess = saveClasess("Audio", 10, "Audífonos", 1020);
        subclaseRepository.saveAndFlush(
                new Subclase(null, 102010, "Bluetooth", clasess, null, null)
        );

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Portátiles\",\"clasessId\":\"" + clasess.getId() + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.codigo").value(102020));
    }

    @Test
    @DisplayName("POST rechaza una Clasess inexistente")
    void create_returnsNotFoundWhenClassDoesNotExist() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Inalámbricos\",\"clasessId\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isNotFound());

        assertThat(subclaseRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST rechaza un nombre vacío")
    void create_rejectsBlankName() throws Exception {
        Clasess clasess = saveClasess("Audio", 10, "Audífonos", 1010);

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"   \",\"clasessId\":\"" + clasess.getId() + "\"}"))
                .andExpect(status().isBadRequest());

        assertThat(subclaseRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST rechaza una subclase duplicada dentro de la misma Clasess")
    void create_rejectsDuplicateName() throws Exception {
        Clasess clasess = saveClasess("Audio", 10, "Audífonos", 1010);
        subclaseRepository.saveAndFlush(
                new Subclase(null, 101010, "Inalámbricos", clasess, null, null)
        );

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Inalámbricos\",\"clasessId\":\"" + clasess.getId() + "\"}"))
                .andExpect(status().isConflict());

        assertThat(subclaseRepository.count()).isEqualTo(1);
    }

    private Clasess saveClasess(String categoriaNombre, int categoriaCodigo, String nombre, int codigo) {
        Categoria categoria = categoriaRepository.saveAndFlush(
                new Categoria(null, categoriaNombre, categoriaCodigo, null, null)
        );
        return clasessRepository.saveAndFlush(
                new Clasess(null, codigo, nombre, categoria, null, null)
        );
    }
}
