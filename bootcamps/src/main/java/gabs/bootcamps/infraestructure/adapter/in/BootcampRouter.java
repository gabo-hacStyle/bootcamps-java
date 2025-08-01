package gabs.bootcamps.infraestructure.adapter.in;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class BootcampRouter {
    private static final String PATH = "bootcamp";

    @Bean
    RouterFunction<ServerResponse> router(BootcampHandler handler) {
        return RouterFunctions.route()
                .GET(PATH, handler::getAll)

                .GET(PATH + "/simple", handler::getSimpleBootcampResponseByIds)
                .GET(PATH + "/{id}", handler::getById)
                //.GET(PATH + "/name/{nombre}", handler::findByNombre)
                .POST(PATH , handler::save)
                //.PUT(PATH + "/{id}", handler::update)
                .DELETE(PATH + "/{id}", handler::delete)
                .build();

    }
}
