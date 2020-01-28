package com.learnreactive.fluxandmono;


import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import static reactor.core.scheduler.Schedulers.parallel;


public class FluxAndMono_FlatMapTest {
	
	List<String> letters = Arrays.asList("A","B","C","D","E","F");
	
    @Test
    public void tranformUsingFlatMap(){

        Flux<String> stringFlux = Flux.fromIterable(letters)
        		.flatMap(s->{
        			
        			return Flux.fromIterable(convertToList(s));      // A -> List[A, newValue] , B -> List[B, newValue]
        		})            //db or external service call that returns a flux -> s -> Flux<String>
        		.log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }
    
    private List<String> convertToList(String s)  {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }
    
    @Test
    public void tranformUsingFlatMap_usingparallel(){

        Flux<String> stringFlux = Flux.fromIterable(letters)
                .window(2)            //Flux<Flux<String> -> (A,B), (C,D), (E,F)
                .flatMap((s) ->
                    s.map(this::convertToList).subscribeOn(parallel()))    // Flux<List<String>
                    .flatMap(s -> Flux.fromIterable(s))                    //Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }       
    
    @Test
    public void tranformUsingFlatMap_parallel_maintain_order(){

        Flux<String> stringFlux = Flux.fromIterable(letters)
                .window(2) //Flux<Flux<String> -> (A,B), (C,D), (E,F)
               /* .concatMap((s) ->
                        s.map(this::convertToList).`(parallel())) */// Flux<List<String>
                .flatMapSequential((s) ->
                        s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s)) //Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

}
