package com.learnreactive.initialize;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import com.learnreactive.document.Item;
import com.learnreactive.document.ItemCapped;
import com.learnreactive.repository.*;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {
	
	@Autowired
	ItemReactiveRepository itemReactiveRepository;
	
	@Autowired
	ItemReactiveCappedRepository itemReactiveCappedRepository;
	
	@Autowired
	MongoOperations mongoOperations;

	@Override
	public void run(String... args) throws Exception {

		initialDataSetup();
		createCappedCollection();
		dataSetUpforCappedCollection();
	}  

	
	public List<Item> data() {
		
		return Arrays.asList(
				new Item(null, "Samsung TV", 400.0),
	            new Item(null, "LG TV", 420.0),
	            new Item(null, "Apple Watch", 299.99),
	            new Item(null, "Beats Headphones", 149.99),
	            new Item("ABC", "Bose Headphones", 149.99));
	}

	private void initialDataSetup() {

		itemReactiveRepository.deleteAll()
		                  .thenMany(Flux.fromIterable(data()))
		                  .flatMap(itemReactiveRepository::save)
		                  .thenMany(itemReactiveRepository.findAll())
		                  .subscribe(item->{
		                	  System.out.println("Items inserted from command line: "+item);
		                  });
	}
	
	
	private void createCappedCollection() {   // collection of fixed-size in mongoDB; Preserve the insertion Order
		
		mongoOperations.dropCollection(ItemCapped.class);
		mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped()); 
	}                                                                          // maxDocument is that many values would be displayed at a time
	
	
	// keep inserting the data into ItemCapped Repository collection
	private void dataSetUpforCappedCollection() {
		
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null,"Random Item " + i, (100.00+i)));
        
        itemReactiveCappedRepository.insert(itemCappedFlux)          // subscribe to the flux and every element coming out will be persisted
                                .subscribe(itemCapped->{
                                    log.info("Inserted Item is " + itemCapped);
                                });
	}

}
