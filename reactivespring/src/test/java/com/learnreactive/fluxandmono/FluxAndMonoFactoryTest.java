package com.learnreactive.fluxandmono;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoFactoryTest {
	
	List<String> names = Arrays.asList("First", "Second", "Third", "Fourth");
	
	@Test
	public void fluxFromIterable() {
		Flux<String> fluxNames = Flux.fromIterable(names)
				.log();                           // not necessary just to see the logs
		
		StepVerifier.create(fluxNames)
		         .expectNext("First", "Second", "Third", "Fourth")
		         .verifyComplete();
	}
	
    @Test
    public void fluxUsingArray(){

        String[] stringNames = new String[]{"fluxUsingArray", "Second", "Third", "Fourth"};

        Flux<String> namesFlux = Flux.fromArray(stringNames);
        StepVerifier.create(namesFlux)
                .expectNext("fluxUsingArray", "Second", "Third", "Fourth")
                .verifyComplete();
    }
	
    @Test
    public void fluxUsingStream(){

        Flux<String> namesFlux = Flux.fromStream(names.stream())
        		.log();
        
        StepVerifier.create(namesFlux)
                .expectNext("First", "Second", "Third", "Fourth")
                .verifyComplete();
    }
    
    @Test
    public void monoUsingJustOrEmpty(){

        Mono<String> mono = Mono.justOrEmpty(null); //Mono.Empty();

        StepVerifier.create(mono.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier(){

        Supplier<String> stringSupplier = () -> "adam";

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        System.out.println(stringSupplier.get());       // just to get the value

        StepVerifier.create(stringMono.log())
                .expectNext("adam")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange(){

       Flux<Integer> integerFlux = Flux.range(1,5)
    		   .log();

       StepVerifier.create(integerFlux)
               .expectNext(1,2,3,4,5)
               .verifyComplete();
    }

}
