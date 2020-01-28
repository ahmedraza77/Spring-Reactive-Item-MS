package com.learnreactive.controller.v1;

import com.learnreactive.constants.ItemConstants;
import com.learnreactive.document.ItemCapped;
import com.learnreactive.repository.ItemReactiveCappedRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemStreamControllerTest {

	@Autowired
	ItemReactiveCappedRepository itemReactiveCappedRepo;
	
	@Autowired
	MongoOperations mongoOperations;

	@Autowired
	WebTestClient webTestClient;
	
	@Before
	public void setUP() {
		
		mongoOperations.dropCollection(ItemCapped.class);
		mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
		
		Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
		    .map(i-> new ItemCapped(null, "Random Item " + i, (100.00+i)))           
		    .take(5);         // takes only 5 values from flux 
		
		itemReactiveCappedRepo.insert(itemCappedFlux)
		                      .doOnNext(itemCapped -> System.out.println("Inserted item in setUP"+ itemCapped))
		                      .blockLast();   // until all 5 are values are taken
	}
	
    @Test
    public void testStreamAllItems(){

        Flux<ItemCapped> itemCappedFlux = webTestClient.get().uri(ItemConstants.ITEM_STREAM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .returnResult((ItemCapped.class))
                .getResponseBody()
                .take(5);

        StepVerifier.create(itemCappedFlux)
                .expectNextCount(5)
                .thenCancel()
                .verify();
    }

}
