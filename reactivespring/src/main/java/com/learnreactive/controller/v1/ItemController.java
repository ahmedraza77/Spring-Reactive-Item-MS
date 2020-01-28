package com.learnreactive.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.learnreactive.constants.ItemConstants.ITEM_END_POINT_V1;
import com.learnreactive.document.Item;
import com.learnreactive.repository.ItemReactiveRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ItemController {

	@Autowired
	ItemReactiveRepository itemReactiveRepository;
	
	@GetMapping(ITEM_END_POINT_V1)
	public Flux<Item> getAllItems() {
		return itemReactiveRepository.findAll();
	}
	
	@GetMapping(ITEM_END_POINT_V1+"/{id}")
	public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id) {
		return itemReactiveRepository.findById(id)
				                 .map(item-> new ResponseEntity<>(item, HttpStatus.OK))
				                 .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping(ITEM_END_POINT_V1)
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Item> createItem(@RequestBody Item item) {
		return itemReactiveRepository.save(item);
	}
	
	@DeleteMapping(ITEM_END_POINT_V1+"/{id}")
	public Mono<Void> deleteItem (@PathVariable String id) {
		return itemReactiveRepository.deleteById(id);
	}
	
	@PutMapping(ITEM_END_POINT_V1+"/{id}")
	public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item) {
		
		return itemReactiveRepository.findById(id)
				                .flatMap(currentItem->{
				                	currentItem.setDescription(item.getDescription());
				                	currentItem.setPrice(item.getPrice());
				                	return itemReactiveRepository.save(currentItem);
				                })
				                .map(updatedItem-> new ResponseEntity<>(updatedItem, HttpStatus.OK))
				                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
    @GetMapping(ITEM_END_POINT_V1+"/runtimeException")
    public Flux<Item> runtimeException(){

        return itemReactiveRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("RuntimeException Occurred.")));
    }
}
