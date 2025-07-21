package gabs.tecnologias.infraestructure.adapter.in;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CapacidadRouter {
    private static final String PATH = "skill";

    @Bean
    RouterFunction<ServerResponse> router(CapacidadHandler handler, CapacidadBootcampHandler handlerBtcamp) {
        return RouterFunctions.route()
                .GET(PATH, handler::getAll)
                .GET(PATH + "/bootcamp/{id}", handlerBtcamp::getCapacidadesByBootcamp)
                .GET(PATH + "/{id}", handler::getById)
                .GET(PATH + "/name/{nombre}", handler::findByNombre)
                .POST(PATH , handler::save)
                .PUT(PATH + "/{id}", handler::update)
                .DELETE(PATH + "/{id}", handler::delete)
                .build();

    }
}
