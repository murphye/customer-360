package io.solo.customer360.nameservice;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class NameService {

	private final NameRepository nameRepository;

    public NameService(NameRepository nameRepository) {
        this.nameRepository = nameRepository;
    }

	@Transactional
	public Mono<Name> save(Name name) {

		return nameRepository.save(name).map(it -> {

			if (it.first().equals("Eric")) {
				throw new IllegalStateException();
			} else {
				return it;
			}
		});
	}
}
