package fr.checkconsulting.scpiinvapi.integration;


import fr.checkconsulting.scpiinvapi.dto.response.ScpiDetailDto;
import fr.checkconsulting.scpiinvapi.service.ScpiService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ScpiResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScpiService scpiService;


    @Test
    void shouldReturnScpiDetails_whenSlugIsValid() throws Exception {
        ScpiDetailDto dto = new ScpiDetailDto();
        dto.setName("Comète");


        Mockito.when(scpiService.getScpiDetails(anyString(), anyString())).thenReturn(dto);

        mockMvc.perform(get("/api/v1/scpi/details/Comète-Alderan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Comète"));

    }

}
