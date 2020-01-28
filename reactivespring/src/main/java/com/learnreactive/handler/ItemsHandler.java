package com.learnreactive.handler;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.learnreactive.document.Item;
import com.learnreactive.document.ItemCapped;
import com.learnreactive.repository.ItemReactiveCappedRepository;
import com.learnreactive.repository.ItemReactiveRepository;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import reactor.core.publisher.Mono;

@Component
public class ItemsHandler {
	
	@Autowired
	ItemReactiveRepository itemReactiveRepository;
	
	@Autowired
	ItemReactiveCappedRepository itemReactiveCappedRepository;
	
	public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
		
		return ServerResponse.ok()
				.contentType(APPLICATION_JSON)
				.body(itemReactiveRepository.findAll(), Item.class);
	}
	
	public Mono<ServerResponse> getItemById(ServerRequest serverRequest) {
		
        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = itemReactiveRepository.findById(id);

        return itemMono.flatMap(item ->                                    // accessing item in mono
                ServerResponse.ok()                                       // data is there
                        .contentType(APPLICATION_JSON)
                        .body(fromValue(item)))                          // insert body in response
                .switchIfEmpty(ServerResponse.notFound().build());      // if there is no data or invalid id passed 
	}
	
	public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
		
		Mono<Item> itemToBeInserted = serverRequest.bodyToMono(Item.class);
		
		return itemToBeInserted.flatMap(item->
				ServerResponse.ok()
				.contentType(APPLICATION_JSON)
				.body(itemReactiveRepository.save(item), Item.class));
	}
	
	public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
		
        String id = serverRequest.pathVariable("id");
		Mono<Void> deletedItem = itemReactiveRepository.deleteById(id);
		
		return ServerResponse.ok()
				.contentType(APPLICATION_JSON)
				.body(deletedItem, Item.class);
	}
	
	public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
		
        String id = serverRequest.pathVariable("id");
        
        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)         // getting item from request
                 .flatMap(item->{                                  // item points to updatedItem
                	  	Mono<Item> itemMono = itemReactiveRepository.findById(id)        // getting item by id from DB 
                	  			.flatMap(currentItem->{           // current item points to itemMono
                	            	  currentItem.setDescription(item.getDescription());
                	            	  currentItem.setPrice(item.getPrice());
                	            	  return itemReactiveRepository.save(currentItem);     // save item
                	  			});
                	  	return itemMono;       // return item
                 });
        
        return updatedItem.flatMap(item->
                     ServerResponse.ok()
                     .contentType(APPLICATION_JSON)
                     .body(fromValue(item))
                     .switchIfEmpty(ServerResponse.notFound().build()));
	}
	
    public Mono<ServerResponse> itemsEx(ServerRequest serverRequest){

        throw new RuntimeException("RuntimeException Occurred");
    }
	
	public Mono<ServerResponse> streamItems(ServerRequest serverRequest) {
		
		return ServerResponse.ok()
				.contentType(APPLICATION_STREAM_JSON)
				.body(itemReactiveCappedRepository.findItemsBy(), ItemCapped.class);
	}
}
