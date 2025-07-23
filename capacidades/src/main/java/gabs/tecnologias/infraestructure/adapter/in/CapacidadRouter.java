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
    private static final String BTCMP_PATH = PATH + "/bootcamp";

    @Bean
    RouterFunction<ServerResponse> router(CapacidadHandler handler, CapacidadBootcampHandler handlerBtcamp) {
        return RouterFunctions.route()
                .GET(PATH, handler::getAll)
                .GET(BTCMP_PATH + "/{id}", handlerBtcamp::getCapacidadesByBootcamp)
                .POST(BTCMP_PATH + "/{id}", handlerBtcamp::saveCapacidadBootcamp)
                .DELETE(BTCMP_PATH + "/{id}", handlerBtcamp::deleteCapacidadesByBootcampDeleted)
                .GET(PATH + "/{id}", handler::getById)
                .GET(PATH + "/exists/{id}", handler::existsById)
                .POST(PATH , handler::save)
                .PUT(PATH + "/{id}", handler::update)
                .DELETE(PATH + "/{id}", handler::delete)
                .build();

    }
}
