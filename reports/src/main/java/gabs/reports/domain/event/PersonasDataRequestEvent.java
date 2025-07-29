package gabs.reports.domain.event;

import gabs.reports.dto.PersonaInfo;
import lombok.Getter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class PersonasDataRequestEvent {
    private final List<Long> personaIds;
    private final CompletableFuture<List<PersonaInfo>> future;
    
    public PersonasDataRequestEvent(List<Long> personaIds, CompletableFuture<List<PersonaInfo>> future) {
        this.personaIds = personaIds;
        this.future = future;
    }
} 