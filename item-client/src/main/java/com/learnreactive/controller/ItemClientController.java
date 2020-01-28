package com.learnreactive.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static com.learnreactive.constants.ItemConstants.*;
import com.learnreactive.domain.*;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@Slf4j
public class ItemClientController {

	private final String reactivespringUrl = "http://localhost:8081";

	WebClient webClient = WebClient.create(reactivespringUrl);

	@GetMapping("/client/retrieve")
	public Flux<Item> getAllItemsUsingRetrieve() {

		return webClient.get().uri(ITEM_END_POINT_V1)         // hitting the reactive url
				.retrieve()                                      // get the response body
				.bodyToFlux(Item.class)                          // set it to domain object
				.log("Items in Client Project retrieve : ");
	}

	@GetMapping("/client/exchange")
	public Flux<Item> getAllItemsUsingExchange() {

		return webClient.get().uri(ITEM_END_POINT_V1)
				.exchange()                              // give access to the entire response
				.flatMapMany(clientResponse-> clientResponse.bodyToFlux(Item.class))     // setting the body from the get response 
				.log();
	}

	
	@GetMapping("/client/retrieve/singleItem")
	public Mono<Item> getOneItemUsingRetrieve(@PathVariable String id){

		return webClient.get().uri(ITEM_END_POINT_V1.concat("/{id}"),id)
				.retrieve()
				.bodyToMono(Item.class)
				.log("Items in Client Project retrieve single Item : ");
	}

	@GetMapping("/client/exchange/singleItem")
	public Mono<Item> getOneItemUsingExchange(){

		String id = "ABC";

		return webClient.get().uri(ITEM_END_POINT_V1.concat("/{id}"),id)
				.exchange()
				.flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
				.log("Items in Client Project retrieve single Item : ");
	}

	@PostMapping("/client/createItem")
	public Mono<Item> createItem(@RequestBody Item item){

		Mono<Item> itemMono = Mono.just(item);
		return webClient.post().uri(ITEM_END_POINT_V1)            
				.contentType(APPLICATION_JSON)     // request
				.body(itemMono, Item.class)                  // request body
				.retrieve()                                  // hit for response
				.bodyToMono(Item.class)                      // response body
				.log("Created item is : ");
	}
	
    @PutMapping("/client/updateItem/{id}")
    public Mono<Item> updateItem(@PathVariable String id,
                                 @RequestBody Item item){

        Mono<Item> itemBody = Mono.just(item);

        return webClient.put().uri(ITEM_END_POINT_V1, id)
                .body(itemBody, Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Updated Item is : ");
    }
    
    @DeleteMapping("/client/deleteItem/{id}")
    public Mono<Void> deleteItem(@PathVariable String id){

        return webClient.delete().uri(ITEM_END_POINT_V1, id)
                .retrieve()
                .bodyToMono(Void.class)
                .log("Deleted Item is");
    }
    
    @GetMapping("/client/retrieve/error")
    public Flux<Item> errorRetrieve(){

        return webClient.get().uri("/v1/items/runtimeException")
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse->{
                	Mono<String> errorMono = clientResponse.bodyToMono(String.class);
                	return errorMono.flatMap(errorMessage->{
                		log.error("The error Message is : " + errorMessage);
                        throw  new RuntimeException(errorMessage);
                	});
                }).bodyToFlux(Item.class);
                
    }     
    
    @GetMapping("/client/exchange/error")
    public Flux<Item> errorExchange(){

        return webClient.get().uri("/v1/items/runtimeException")
                .exchange()
                .flatMapMany((clientResponse -> {

                    if(clientResponse.statusCode().is5xxServerError()){

                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> {
                                    log.error("Error Message in errorExchange : " + errorMessage);
                                    throw  new RuntimeException(errorMessage);
                                });
                    }else{
                        return clientResponse.bodyToFlux(Item.class);
                    }

                }));

    }
    
    @GetMapping(value = "/client/retrieve/itemsStream", produces = APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemsStream() {
    	
    	return webClient.get().uri(ITEM_STREAM_END_POINT_V1)        
				.retrieve()                                      
				.bodyToFlux(ItemCapped.class)                          
				.log("Items in Client Project retrieve : ");
    }
}
