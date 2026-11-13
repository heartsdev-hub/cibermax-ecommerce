package com.cibermax.ecommerce.modules.categoria.controller;

import com.cibermax.ecommerce.modules.categoria.dto.request.CategoryCreateRequestDTO;
import com.cibermax.ecommerce.modules.categoria.dto.response.CategoryResponse;
import com.cibermax.ecommerce.modules.categoria.service.CategoriaService;
import com.cibermax.ecommerce.shared.exception.DuplicateResourceException;
import com.cibermax.ecommerce.shared.exception.GlobalExceptionHandler;
import com.cibermax.ecommerce.shared.response.ApiResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriaController.class)
@Import(GlobalExceptionHandler.class)
@TestPropertySource(properties = "spring.profiles.active=test")
@WithMockUser(username = "qa-user")
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    private final UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final CategoryResponse response = new CategoryResponse(
            id,
            "Tecnología",
            Instant.parse("2026-01-01T10:00:00Z"),
            Instant.parse("2026-01-01T10:00:00Z")
    );

    @Test
    @DisplayName("POST /api/categorias responde 201 cuando la categoría es válida")
    void create_returnsCreated() throws Exception {
        when(categoriaService.create(any(CategoryCreateRequestDTO.class)))
                .thenReturn(ApiResult.success("Categoría creada correctamente", response));

        mockMvc.perform(post("/api/categorias")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Tecnología\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.nombre").value("Tecnología"));
    }

    @Test
    @DisplayName("POST /api/categorias responde 400 cuando falta el nombre")
    void create_returnsBadRequestWhenNameIsMissing() throws Exception {
        mockMvc.perform(post("/api/categorias")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(categoriaService, never()).create(any(CategoryCreateRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/categorias responde 409 cuando el nombre está duplicado")
    void create_returnsConflictWhenNameIsDuplicated() throws Exception {
        when(categoriaService.create(any(CategoryCreateRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Ya existe una categoría con el nombre indicado"));

        mockMvc.perform(post("/api/categorias")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Tecnología\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Ya existe una categoría con el nombre indicado"));
    }
}
