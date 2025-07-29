package gabs.reports.infraestructure.adapter.in;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class InscripcionRouter {

    private static final String PATH = "/reports/enrollments";
    @Bean
    public RouterFunction<ServerResponse> inscripcionRoutes(InscripcionHandler handler) {
        return RouterFunctions.route()
                .POST(PATH, handler::registrarInscripcion)
                .build();
    }
} 