package gabs.tecnologias.infraestructure.adapter.in;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class TecnologiaRouter {
    private static final String PATH = "technology";

    @Bean
    RouterFunction<ServerResponse> router(TecnologiaHandler handler) {
        return RouterFunctions.route()
                .GET(PATH, handler::getAll)
                .GET(PATH + "/{id}", handler::existsById)
                .GET(PATH + "/name/{nombre}", handler::findByNombre)
                .POST(PATH , handler::save)
                .PUT(PATH + "/{id}", handler::update)
                .DELETE(PATH + "/{id}", handler::delete)
                .build();
    }
}
