package gabs.reports.infraestructure.adapter.in;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class BootcampRouter {
    private static final String PATH = "/reports/bootcamps";

   

    @Bean
    public RouterFunction<ServerResponse> bootcampRoutes(BootcampHandler handler) {
        return RouterFunctions.route()
                .POST(PATH, handler::registrarBootcamp)
                .GET(PATH + "/max-inscritos", handler::bootcampConMasInscritos)
                .build();
    }
}
