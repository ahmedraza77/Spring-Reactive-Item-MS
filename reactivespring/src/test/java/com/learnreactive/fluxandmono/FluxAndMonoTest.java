package com.learnreactive.fluxandmono;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {
	
	@Test
	public void fluxTest() {
		Flux<String> stringFlux = Flux.just("First element", "Second", "Third")
//                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("After Error"))              // won't run after error occur
                .log();
		
		stringFlux.subscribe(System.out::println,
				(e)->System.err.println("Exception is:"+e),
				()->System.out.println("After Completion"));  // used to run at the end 
	}

	@Test
	public void fluxElements_WithoutErrorTest() {
		Flux<String> stringFlux = Flux.just("First element", "Second", "Third")
				.log();
		
		StepVerifier.create(stringFlux)
		        .expectNext("First element")
		        .expectNext("Second")
		        .expectNext("Third")
		        .verifyComplete();       // same as subscribe method, starts the flow of events from flux to subscriber
	}
	
	@Test
	public void fluxElements_WithoutErrorTest1() {
		Flux<String> stringFlux = Flux.just("First element", "Second", "Third")
				.log();
		
		StepVerifier.create(stringFlux)
		        .expectNext("First element", "Second", "Third") 
		        .verifyComplete();       // same as subscribe method, starts the flow of events from flux to subscriber
	}
	
    @Test
    public void fluxElements_WithErrorTest() {

        Flux<String> stringFlux = Flux.just("First element", "Second", "Third")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("First element")
                .expectNext("Second")
                .expectNext("Third")
                //.expectError(RuntimeException.class)                
                .expectErrorMessage("Exception Occurred")         // Run one of them 
                .verify();                                   // in case of expecting error, acts same as verify Complete() 
    }
    
    @Test
    public void fluxElementsCount_WithErrorTest() {

        Flux<String> stringFlux = Flux.just("First element", "Second", "Third")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                //.expectError(RuntimeException.class)                
                .expectErrorMessage("Exception Occurred")         
                .verify();                                   
    }
    
    @Test
    public void monoTest(){

        Mono<String>  stringMono = Mono.just("Spring");

        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTest_Error(){

        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
    
}
