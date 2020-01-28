package com.learnreactive.fluxandmono;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


public class FluxAndMonoFilterTest {
	
	List<String> names = Arrays.asList("Ahmed", "Akash", "Third", "Fourth");
	
	@Test
	public void filterTest() {
		Flux<String> namesFlux = Flux.fromIterable(names)
				.filter(s->s.startsWith("A"))
				.log();
		
		StepVerifier.create(namesFlux)
		        .expectNext("Ahmed", "Akash")
		        .verifyComplete();
	}
	
    @Test
    public void filterTestLength(){

        Flux<String> namesFlux = Flux.fromIterable(names) 
                .filter(s->s.length() >5)
                .log(); //jenny

        StepVerifier.create(namesFlux)
                .expectNext("Fourth")
                .verifyComplete();

    }


}
