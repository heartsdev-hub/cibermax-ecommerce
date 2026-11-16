package com.cibermax.ecommerce.modules.subCategoria.controller;

import com.cibermax.ecommerce.modules.subCategoria.dto.request.SubCategoryCreateRequestDTO;
import com.cibermax.ecommerce.modules.subCategoria.dto.response.SubCategoryResponse;
import com.cibermax.ecommerce.modules.subCategoria.service.SubCategoriaService;
import com.cibermax.ecommerce.shared.exception.DuplicateResourceException;
import com.cibermax.ecommerce.shared.exception.GlobalExceptionHandler;
import com.cibermax.ecommerce.shared.exception.ResourceNotFoundException;
import com.cibermax.ecommerce.shared.response.ApiResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubCategoriaController.class)
@Import(GlobalExceptionHandler.class)
@TestPropertySource(properties = "spring.profiles.active=test")
@WithMockUser(username = "qa-user")
class SubCategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubCategoriaService subCategoriaService;

    private final UUID subCategoriaId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID categoriaId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final SubCategoryResponse response = new SubCategoryResponse(
            subCategoriaId,
            "Laptops",
            categoriaId,
            "Computadoras",
            Instant.parse("2026-01-01T10:00:00Z"),
            Instant.parse("2026-01-01T10:00:00Z")
    );

    @Test
    @DisplayName("POST /api/v1/subcategorias responde 201 cuando la solicitud es válida")
    void create_returnsCreated() throws Exception {
        when(subCategoriaService.create(any(SubCategoryCreateRequestDTO.class)))
                .thenReturn(ApiResult.success("Subcategoría creada correctamente", response));

        mockMvc.perform(post("/api/v1/subcategorias")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Laptops\",\"categoriaId\":\""
                                + categoriaId + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(subCategoriaId.toString()))
                .andExpect(jsonPath("$.data.nombre").value("Laptops"))
                .andExpect(jsonPath("$.data.categoriaId").value(categoriaId.toString()));
    }

    @Test
    @DisplayName("POST /api/v1/subcategorias responde 400 cuando faltan datos obligatorios")
    void create_returnsBadRequestWhenRequiredDataIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/subcategorias")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(subCategoriaService, never()).create(any(SubCategoryCreateRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/subcategorias responde 404 cuando la categoría no existe")
    void create_returnsNotFoundWhenParentCategoryDoesNotExist() throws Exception {
        when(subCategoriaService.create(any(SubCategoryCreateRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("No se encontró la categoría con id: " + categoriaId));

        mockMvc.perform(post("/api/v1/subcategorias")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Laptops\",\"categoriaId\":\""
                                + categoriaId + "\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/v1/subcategorias responde 409 cuando el nombre está duplicado")
    void create_returnsConflictWhenNameIsDuplicated() throws Exception {
        when(subCategoriaService.create(any(SubCategoryCreateRequestDTO.class)))
                .thenThrow(new DuplicateResourceException(
                        "Ya existe una subcategoría con el nombre indicado en la categoría seleccionada"
                ));

        mockMvc.perform(post("/api/v1/subcategorias")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Laptops\",\"categoriaId\":\""
                                + categoriaId + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("GET /api/v1/subcategorias responde 200 con las subcategorías")
    void findAll_returnsOkWithSubCategories() throws Exception {
        when(subCategoriaService.findAll()).thenReturn(ApiResult.success(
                "Subcategorías obtenidas correctamente",
                List.of(response)
        ));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/v1/subcategorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].nombre").value("Laptops"));
    }
}
