package io.solo.customer360.nameservice;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

interface NameRepository extends ReactiveCrudRepository<Name, Long> {

	@Query("select id, first, middle, last from name n where n.last = :last")
	Flux<Name> findByLast(String last);
}
