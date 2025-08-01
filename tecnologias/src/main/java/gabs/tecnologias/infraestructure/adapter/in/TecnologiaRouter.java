package gabs.tecnologias.infraestructure.adapter.in;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class TecnologiaRouter {
    private static final String PATH = "technology";
    private static final String CAP_PATH = PATH + "/capacidad";

    @Bean
    RouterFunction<ServerResponse> router(TecnologiaHandler handler, CapacidadTecnologiaHandler handlerCap) {
        return RouterFunctions.route()
                .GET(PATH, handler::getAll)
                .GET(CAP_PATH+  "/{id}", handlerCap::getTechsByCapacidadId)
                .POST(CAP_PATH+  "/{id}", handlerCap::saveCapacidadTecnologia)
                .DELETE(CAP_PATH , handlerCap::deleteTecnologiasOfCapacidadesIds)
                .GET(PATH + "/exists/{id}", handler::existsById)
                .GET(PATH + "/{id}", handler::getById)
                .GET(PATH + "/name/{nombre}", handler::findByNombre)
                .POST(PATH , handler::save)
                .PUT(PATH + "/{id}", handler::update)
                .DELETE(PATH + "/{id}", handler::delete)
                .build();
    }
}
