package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.entities.Scpi;
import fr.checkconsulting.scpiinvapi.repositories.ScpiRepository;
import fr.checkconsulting.scpiinvapi.services.ScpiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ScpiServiceTest {

    private ScpiRepository repository;
    private ScpiService service;

    @BeforeEach
    void setUp() {
        repository = mock(ScpiRepository.class);
        service = new ScpiService(repository);
    }

    @Test
    void shouldReturnAllScpis() {
        // Given
        Scpi scpi1 = new Scpi(1L, "Corum Origin", 6.25);
        Scpi scpi2 = new Scpi(2L, "Épargne Pierre", 5.80);
        when(repository.findAll()).thenReturn(List.of(scpi1, scpi2));

        // When
        List<Scpi> result = service.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(scpi1, scpi2);
        verify(repository).findAll();
    }

    @Test
    void shouldSaveScpi() {
        // Given
        Scpi scpi = new Scpi(null, "Corum XL", 6.10);
        Scpi saved = new Scpi(3L, "Corum XL", 6.10);
        when(repository.save(scpi)).thenReturn(saved);

        // When
        Scpi result = service.save(scpi);

        // Then
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Corum XL");
        assertThat(result.getYield()).isEqualTo(6.10);

        ArgumentCaptor<Scpi> captor = ArgumentCaptor.forClass(Scpi.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Corum XL");
    }
}
