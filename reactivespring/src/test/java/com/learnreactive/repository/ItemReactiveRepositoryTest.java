package com.learnreactive.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.learnreactive.document.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext       // whenever altering the state of application context; basically every test case will get brand new application context
public class ItemReactiveRepositoryTest {
	
	@Autowired
	ItemReactiveRepository itemReactiveRepository;
	
    List<Item> itemList = Arrays.asList(new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple Watch", 299.99),
            new Item(null, "Beats Headphones", 149.99),
            new Item("ABC", "Bose Headphones", 149.99));
    
    @Before
    public void setUp() {
    	itemReactiveRepository.deleteAll()
    	                  .thenMany(Flux.fromIterable(itemList))
    	                  .flatMap(itemReactiveRepository::save)
    	                  .doOnNext(item->{
    	                	  System.out.println("Insertem item: " + item);
    	                	  })
    	                  .blockLast();       // wait until all operations above are executed; saved.... recommended only in test case.
    }
	
    @Test
    public void getAllItems() {

        StepVerifier.create(itemReactiveRepository.findAll()) 
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }
    
    @Test
    public void getItemById() {
    	
    	StepVerifier.create(itemReactiveRepository.findById("ABC"))
    	        .expectSubscription()
    	        .expectNextMatches(item->item.getDescription().equals("Bose Headphones"))     // predicate
    	        .verifyComplete();
    }
    
    @Test
    public void getItemByDescription() {
    	
    	StepVerifier.create(itemReactiveRepository.findByDescription("Apple Watch"))
    	        .expectSubscription()
    	        .expectNextCount(1)
    	        .verifyComplete();
    }
    
    @Test
    public void saveItem() {
    	
    	Item item = new Item(null, "Oneplus 7T", 249.99);
    	
    	Mono<Item> savedItem = itemReactiveRepository.save(item);
    	
    	StepVerifier.create(savedItem.log("New item inserted"))
    	        .expectSubscription()
    	        .expectNextMatches(item1-> item1.getId()!=null && item1.getDescription().equals("Oneplus 7T"))
    	        .verifyComplete();
    }

    @Test
    public void updateItem() {
    	double newPrice = 520.0;
    	
    	Mono<Item> newItem = itemReactiveRepository.findByDescription("LG TV")
    	                  .map(item->{
    	                	  item.setPrice(newPrice);                 // setting the new price
    	                	  return item;
    	                  })
    	                  .flatMap(item->{
    	                	  return itemReactiveRepository.save(item);     // saving the item with new price
    	                  });
    	
    	StepVerifier.create(newItem.log("Updating the Item"))
    	        .expectSubscription()
    	        .expectNextMatches(item-> item.getPrice()==newPrice)
    	        .verifyComplete();
    }
    
    @Test
    public void deleteItemById() {
    	
    	Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")          // got the item
    	                  .map(Item::getId)             // got the id of that item
    	                  .flatMap(id->{
    	                	  return itemReactiveRepository.deleteById(id);
    	                  });  
    	
        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();
    	
        StepVerifier.create(itemReactiveRepository.findAll().log("New Item List")) 
                .expectSubscription()
                .expectNextCount(4)    // now 4 items after delete
                .verifyComplete();
    }
}
