package io.solo.customer360.customerservice;

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
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

	record Customer(String customerId, Person person) {}

	record Person(int personId, Name name) {}

	record Name(int id, String first, String middle, String last) {}

	@Autowired
	private CustomerService customerService;

	@Bean
	public RouterFunction<ServerResponse> getCustomers() {
		return route(GET("/api/customer"),
			request ->
				ok().body(customerService.getCustomers(), Customer.class));
	}

	@Service
	class CustomerService {

		@Autowired
		private WebClient webClient;

		public Flux<Customer> getCustomers() {
			Flux<Person> people = this.webClient.get().uri("/api/person").header("Host", "person").retrieve().bodyToFlux(Person.class);
			Flux<Customer> customers = people.flatMap(person -> Mono.just(new Customer(person.personId + "-" + 1, person)));
			return customers;
		}
	}

	@Bean
	@Profile("dev")
	WebClient webClientDev(WebClient.Builder webClientBuilder) {
		return webClientBuilder.baseUrl("http://person:8082").build();
	}

	@Bean
	@Profile("docker-compose")
	WebClient webClientDockerCompose(WebClient.Builder webClientBuilder) {
		return webClientBuilder.baseUrl("http://person:8080").build();
	}

	@Bean
	@Profile("kubernetes")
	WebClient webClientKubernetes(WebClient.Builder webClientBuilder) {
		return webClientBuilder.baseUrl("http://person").build();
	}

	@Bean
	@Profile("istio-domainsockets")
	WebClient webClientIstioDomainSockets(WebClient.Builder webClientBuilder) {
		HttpClient client = HttpClient.create().remoteAddress(() -> new DomainSocketAddress("/var/run/netty/outbound.sock"));
		return webClientBuilder.clientConnector(new ReactorClientHttpConnector(client)).build();
	}

    @Bean
    @Profile("istio-domainsockets")
    public NettyReactiveWebServerFactory factory() {
        var factory = new NettyReactiveWebServerFactory();
        factory.setServerCustomizers(Collections.singletonList(new NettyServerCustomizer() {
            @Override
            public HttpServer apply(HttpServer httpServer) {
                return httpServer.bindAddress(() -> new DomainSocketAddress("/var/run/netty/inbound.sock"));
            }
        }));
        return factory;
    }
}