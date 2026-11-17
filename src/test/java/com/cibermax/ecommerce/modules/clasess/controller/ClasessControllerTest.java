package com.cibermax.ecommerce.modules.clasess.controller;

import com.cibermax.ecommerce.modules.clasess.dto.request.ClasessCreateRequest;
import com.cibermax.ecommerce.modules.clasess.dto.request.ClasessUpdateRequest;
import com.cibermax.ecommerce.modules.clasess.dto.response.ClasessResponse;
import com.cibermax.ecommerce.modules.clasess.service.ClasessService;
import com.cibermax.ecommerce.shared.exception.DuplicateResourceException;
import com.cibermax.ecommerce.shared.exception.GlobalExceptionHandler;
import com.cibermax.ecommerce.shared.exception.ResourceNotFoundException;
import com.cibermax.ecommerce.shared.response.ApiResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClasessController.class)
@Import(GlobalExceptionHandler.class)
@WithMockUser(username = "qa-user")
class ClasessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClasessService clasessService;

    @Test
    void create_returnsCreated() throws Exception {
        UUID categoriaId = UUID.randomUUID();
        ClasessResponse response = new ClasessResponse(
                UUID.randomUUID(), 1010, "Audífonos", "Audio", null, null
        );
        when(clasessService.create(any(ClasessCreateRequest.class)))
                .thenReturn(ApiResult.success("Clasess creada correctamente", response));

        mockMvc.perform(post("/api/v1/clasess")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Audífonos\",\"categoriaId\":\"" + categoriaId + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.codigo").value(1010))
                .andExpect(jsonPath("$.data.categoria").value("Audio"))
                .andExpect(jsonPath("$.data.categoriaId").doesNotExist())
                .andExpect(jsonPath("$.data.categoriaCodigo").doesNotExist());
    }

    @Test
    void create_returnsBadRequestWhenDataIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/clasess")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(clasessService, never()).create(any(ClasessCreateRequest.class));
    }

    @Test
    void create_returnsNotFoundWhenCategoryDoesNotExist() throws Exception {
        UUID categoriaId = UUID.randomUUID();
        when(clasessService.create(any(ClasessCreateRequest.class)))
                .thenThrow(new ResourceNotFoundException("No se encontró la categoría con id: " + categoriaId));

        mockMvc.perform(post("/api/v1/clasess")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Audífonos\",\"categoriaId\":\"" + categoriaId + "\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_returnsOk() throws Exception {
        ClasessResponse response = new ClasessResponse(
                UUID.randomUUID(), 1010, "Audífonos", "Audio", null, null
        );
        when(clasessService.findAll()).thenReturn(ApiResult.success(
                "Clasess obtenidas correctamente", java.util.List.of(response)
        ));

        mockMvc.perform(get("/api/v1/clasess"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].codigo").value(1010))
                .andExpect(jsonPath("$.data[0].categoria").value("Audio"));
    }

    @Test
    void findById_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        ClasessResponse response = new ClasessResponse(id, 1010, "Audífonos", "Audio", null, null);
        when(clasessService.findById(id)).thenReturn(ApiResult.success(
                "Clasess obtenida correctamente", response
        ));

        mockMvc.perform(get("/api/v1/clasess/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.codigo").value(1010));
    }

    @Test
    void findById_returnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(clasessService.findById(id))
                .thenThrow(new ResourceNotFoundException("No se encontró la Clasess con id: " + id));

        mockMvc.perform(get("/api/v1/clasess/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_returnsBadRequestForInvalidUuid() throws Exception {
        mockMvc.perform(get("/api/v1/clasess/{id}", "uuid-invalido"))
                .andExpect(status().isBadRequest());

        verify(clasessService, never()).findById(any(UUID.class));
    }

    @Test
    void update_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        ClasessResponse response = new ClasessResponse(
                id, 1010, "Audífonos inalámbricos", "Audio", null, null
        );
        when(clasessService.update(any(UUID.class), any(ClasessUpdateRequest.class)))
                .thenReturn(ApiResult.success("Clasess actualizada correctamente", response));

        mockMvc.perform(put("/api/v1/clasess/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Audífonos inalámbricos\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.codigo").value(1010))
                .andExpect(jsonPath("$.data.nombre").value("Audífonos inalámbricos"));
    }

    @Test
    void update_returnsBadRequestWhenNameIsMissing() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/api/v1/clasess/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(clasessService, never()).update(any(UUID.class), any(ClasessUpdateRequest.class));
    }

    @Test
    void update_returnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(clasessService.update(any(UUID.class), any(ClasessUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException("No se encontró la Clasess"));

        mockMvc.perform(put("/api/v1/clasess/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Parlantes\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_returnsConflictForDuplicateName() throws Exception {
        UUID id = UUID.randomUUID();
        when(clasessService.update(any(UUID.class), any(ClasessUpdateRequest.class)))
                .thenThrow(new DuplicateResourceException("Ya existe una clase con el nombre indicado"));

        mockMvc.perform(put("/api/v1/clasess/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Parlantes\"}"))
                .andExpect(status().isConflict());
    }
}
