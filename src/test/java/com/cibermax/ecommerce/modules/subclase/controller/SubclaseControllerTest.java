package com.cibermax.ecommerce.modules.subclase.controller;

import com.cibermax.ecommerce.modules.subclase.dto.request.SubclaseCreateRequest;
import com.cibermax.ecommerce.modules.subclase.dto.response.SubclaseResponse;
import com.cibermax.ecommerce.modules.subclase.service.SubclaseService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubclaseController.class)
@Import(GlobalExceptionHandler.class)
@WithMockUser(username = "qa-user")
class SubclaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubclaseService subclaseService;

    @Test
    void create_returnsCreated() throws Exception {
        UUID clasessId = UUID.randomUUID();
        SubclaseResponse response = new SubclaseResponse(
                UUID.randomUUID(), "101010", "Inalámbricos", "Audio", "Audífonos", null, null
        );
        when(subclaseService.create(any(SubclaseCreateRequest.class)))
                .thenReturn(ApiResult.success("Subclase creada correctamente", response));

        mockMvc.perform(post("/api/v1/subclases")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Inalámbricos\",\"clasessId\":\"" + clasessId + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.codigo").value("101010"))
                .andExpect(jsonPath("$.data.categoria").value("Audio"))
                .andExpect(jsonPath("$.data.clase").value("Audífonos"));
    }

    @Test
    void create_returnsBadRequestWhenDataIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/subclases")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(subclaseService, never()).create(any(SubclaseCreateRequest.class));
    }

    @Test
    void create_returnsNotFoundWhenClassDoesNotExist() throws Exception {
        UUID clasessId = UUID.randomUUID();
        when(subclaseService.create(any(SubclaseCreateRequest.class)))
                .thenThrow(new ResourceNotFoundException("No se encontró la Clasess con id: " + clasessId));

        mockMvc.perform(post("/api/v1/subclases")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Inalámbricos\",\"clasessId\":\"" + clasessId + "\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_returnsOkWithSubclases() throws Exception {
        SubclaseResponse response = new SubclaseResponse(
                UUID.randomUUID(), "101010", "Inalámbricos", "Audio", "Audífonos", null, null
        );
        when(subclaseService.findAll()).thenReturn(ApiResult.success(
                "Subclases obtenidas correctamente", java.util.List.of(response)
        ));

        mockMvc.perform(get("/api/v1/subclases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].codigo").value("101010"))
                .andExpect(jsonPath("$.data[0].clase").value("Audífonos"));
    }

    @Test
    void findAll_returnsEmptyList() throws Exception {
        when(subclaseService.findAll()).thenReturn(ApiResult.success(
                "Subclases obtenidas correctamente", java.util.List.of()
        ));

        mockMvc.perform(get("/api/v1/subclases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
