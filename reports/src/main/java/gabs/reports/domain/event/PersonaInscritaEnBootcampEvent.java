package gabs.reports.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PersonaInscritaEnBootcampEvent {
    private final Long bootcampId;
    private final Long personaId;
} 