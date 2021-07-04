package io.solo.customer360.nameservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;


@Configuration
public class NameRoutes {

	@Bean
	public RouterFunction<ServerResponse> monoRouterFunction(NameHandler nameHandler) {
		return route().GET("/api/name", accept(APPLICATION_JSON), nameHandler::getAll, ops -> ops.beanClass(NameRepository.class).beanMethod("findAll")).build()
		  .and(route().GET("/api/name/{id}", accept(APPLICATION_JSON), nameHandler::getName, ops -> ops.beanClass(NameRepository.class).beanMethod("findById")).build()
		  .and(route().GET("/api/name/last/{last}", accept(APPLICATION_JSON), nameHandler::getNameByLast, ops -> ops.beanClass(NameRepository.class).beanMethod("findByLast")).build()
		  .and(route().POST("/api/name/post", accept(APPLICATION_JSON), nameHandler::postName, ops -> ops.beanClass(NameRepository.class).beanMethod("saveAll")).build())));
    }

}