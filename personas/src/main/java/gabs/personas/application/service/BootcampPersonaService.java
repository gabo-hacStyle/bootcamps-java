package gabs.personas.application.service;

import gabs.personas.application.port.BootcampPersonaUseCases;
import gabs.personas.domain.model.BootcampPersona;
import gabs.personas.domain.port.BootcampPersonaRepositoryPort;
import gabs.personas.domain.port.PersonaRepositoryPort;
import gabs.personas.dto.BootcampSimpleResponse;
import gabs.personas.dto.EnrollRequest;
import gabs.personas.dto.PersonaRegisteredResponse;
import gabs.personas.infraestructure.adapter.out.BootcampClient;
import gabs.personas.infraestructure.adapter.out.ReportClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BootcampPersonaService implements BootcampPersonaUseCases {

    private final BootcampPersonaRepositoryPort repository;
    private final PersonaRepositoryPort personaRepository;
    private final BootcampClient bootcampClient;
    private final ReportClient reportClient;


    @Override
    public Mono<PersonaRegisteredResponse> registerInBootcamp(EnrollRequest req){
        Long bootcampId = req.getBootcampId();
        Long personaId = req.getPersonaId();

        // 1. Verificar que la persona existe
        return personaRepository.findById(personaId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La persona no existe")))
                .flatMap(persona ->
                        // 2. Buscar los bootcamps en los que está inscrito
                        repository.findByPersonaId(personaId)
                                .map(BootcampPersona::getBootcampId)
                                .collectList()
                                .flatMap(idsBootcampsInscritos -> {
                                    // 3. Validar el máximo de 5 bootcamps
                                    if (idsBootcampsInscritos.size() >= 5) {
                                        return Mono.error(new IllegalStateException("No puedes inscribirte en más de 5 bootcamps."));
                                    }
                                    // 4. Construir lista de IDs para la petición (bootcamps inscritos + nuevo)
                                    List<Long> allIds = new ArrayList<>(idsBootcampsInscritos);
                                    allIds.add(bootcampId);

                                    // 5. Hacer una sola petición para traer info de todos los bootcamps
                                    String idsString = allIds.stream().map(String::valueOf).collect(Collectors.joining(","));
                                    log.info("Ids String: {}", idsString);

                                    return bootcampClient.bringSimpleResponseForManyBootcamps(idsString)
                                            .collectList()
                                            .flatMap(bootcamps -> {
                                                // Separar el nuevo bootcamp de los inscritos
                                                BootcampSimpleResponse nuevo = bootcamps.stream()
                                                        .filter(b -> b.getId().equals(bootcampId))
                                                        .findFirst()
                                                        .orElseThrow(() -> new IllegalStateException("Bootcamp a inscribir no encontrado."));
                                                List<BootcampSimpleResponse> inscritos = bootcamps.stream()
                                                        .filter(b -> !b.getId().equals(bootcampId))
                                                        .toList();

                                                // Validar fechas
                                                for (BootcampSimpleResponse b : inscritos) {
                                                    if (fechasSeCruzan(b.getFechaLanzamiento(), b.getFechaFinalizacion(),
                                                            nuevo.getFechaLanzamiento(), nuevo.getFechaFinalizacion())) {
                                                        String mensaje = String.format(
                                                                "Ya estás inscrito en el bootcamp '%s' que se cruza en fechas con '%s'.",
                                                                b.getNombre(), nuevo.getNombre()
                                                        );
                                                        return Mono.error(new IllegalStateException(mensaje));
                                                    }
                                                }

                                                // 6. Guardar inscripción
                                                BootcampPersona bp = new BootcampPersona();
                                                bp.setPersonaId(personaId);
                                                bp.setBootcampId(bootcampId);

                                                return repository.save(bp)
                                                        .map(saved -> {
                                                            PersonaRegisteredResponse resp = new PersonaRegisteredResponse();
                                                            resp.setNombrePersona(persona.getNombre());
                                                            resp.setCorreoPersona(persona.getCorreo());
                                                            resp.setPersonaId(personaId);
                                                            resp.setBootcampId(bootcampId);
                                                            resp.setNombreBootcamp(nuevo.getNombre());

                                                            return resp;
                                                        })
                                                        .flatMap(resp -> reportClient.postInscriptionReport(resp)
                                                                .thenReturn(resp));
                                            });
                                })
                );
    }

    private boolean fechasSeCruzan(LocalDate inicio1, LocalDate fin1, LocalDate inicio2, LocalDate fin2) {
        return !fin1.isBefore(inicio2) && !fin2.isBefore(inicio1);
    }
}
