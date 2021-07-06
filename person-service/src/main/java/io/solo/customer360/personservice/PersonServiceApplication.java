package io.solo.customer360.personservice;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.netty.channel.unix.DomainSocketAddress;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;

@SpringBootApplication
public class PersonServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonServiceApplication.class, args);
	}

	record Person(int personId, Name name) {}

	record Name(int personId, String first, String middle, String last) {}

	@Autowired
	private PersonService personService;

	@Bean
	public RouterFunction<ServerResponse> getPeople() {
		return route(GET("/api/person"),
			request ->
				ok().body(personService.getPeople(), Person.class));
	}

	@Service
	class PersonService {

		@Autowired
		private WebClient webClient;

		public Flux<Person> getPeople() {
			Flux<Name> names = this.webClient.get().uri("/api/name").header("Host", "name").retrieve().bodyToFlux(Name.class);
			Flux<Person> people = names.flatMap(name -> Mono.just(new Person(name.personId, name)));
			return people;
		}
	}

	@Bean
	@Profile("dev")
	WebClient webClientDev(WebClient.Builder webClientBuilder) {
		return webClientBuilder.baseUrl("http://localhost:8083").build();
	}

	@Bean
	@Profile("docker-compose")
	WebClient webClientDockerCompose(WebClient.Builder webClientBuilder) {
		return webClientBuilder.baseUrl("http://name:8080").build();
	}

	@Bean
	@Profile("kubernetes")
	WebClient webClientKubernetes(WebClient.Builder webClientBuilder) {
		return webClientBuilder.baseUrl("http://name").build();
	}

	@Bean
	@Profile("istio-domainsockets")
	WebClient webClientIstioDomainSockets(WebClient.Builder webClientBuilder) {
		var client = HttpClient.create().remoteAddress(() -> new DomainSocketAddress("/var/run/docker.sock"));
		return webClientBuilder.clientConnector(new ReactorClientHttpConnector(client)).build();
	}

    @Bean
    @Profile("istio-domainsockets")
    public NettyReactiveWebServerFactory factory() {
        var factory = new NettyReactiveWebServerFactory();
        factory.setServerCustomizers(Collections.singletonList(new NettyServerCustomizer() {
            @Override
            public HttpServer apply(HttpServer httpServer) {
                return httpServer.bindAddress(() -> new DomainSocketAddress("/tmp/test.sock"));
            }
        }));
        return factory;
    }
}