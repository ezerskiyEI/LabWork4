package com.example.demo.Controller;


import com.example.demo.service.RequestCounterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestCounterControllerTest {

    @Mock
    private RequestCounterService counterService;

    @InjectMocks
    private RequestCounterController counterController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(counterController).build();
    }

    @Test
    @DisplayName("Should return current request count")
    void shouldReturnCurrentRequestCount() throws Exception {
        when(counterService.getRequestCount()).thenReturn(42L);

        mockMvc.perform(get("/api/counter"))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));

        verify(counterService).getRequestCount();
    }
}