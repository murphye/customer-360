package io.solo.customer360.nameservice;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class NameHandler {

    private final NameRepository nameRepository;

    public NameHandler(NameRepository nameRepository) {
        this.nameRepository = nameRepository;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        Flux<Name> nameFlux = nameRepository.findAll();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(nameFlux, Name.class);
    }

    public Mono<ServerResponse> getName(ServerRequest request) {
        long id = Long.valueOf(request.pathVariable("id"));
        Mono<Name> nameMono = nameRepository.findById(id);
        return nameMono
            .flatMap(name -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromValue(name)))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getNameByLast(ServerRequest request) {
        String last = request.pathVariable("last");
        Flux<Name> nameFlux = nameRepository.findByLast(last);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(nameFlux, Name.class);
    }

    public Mono<ServerResponse> postName(ServerRequest request) {
        Mono<Name> nameMono = request.bodyToMono(Name.class);
        nameMono = nameRepository.saveAll(nameMono).single();

        return nameMono
            .flatMap(name -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromValue(name)))
            .switchIfEmpty(ServerResponse.notFound().build());
    }
}