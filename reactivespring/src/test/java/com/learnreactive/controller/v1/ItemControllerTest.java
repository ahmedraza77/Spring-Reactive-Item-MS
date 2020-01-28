package com.learnreactive.controller.v1;

import com.learnreactive.document.Item;
import com.learnreactive.repository.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static com.learnreactive.constants.ItemConstants.ITEM_END_POINT_V1;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {
	
	@Autowired
	WebTestClient webTestClient;
	
	@Autowired
	ItemReactiveRepository itemReactiveRepository;
	
	public List<Item> data() {
		return Arrays.asList(
				new Item(null, "Samsung TV", 400.0),
	            new Item(null, "LG TV", 420.0),
	            new Item(null, "Apple Watch", 299.99),
	            new Item(null, "Beats Headphones", 149.99),
	            new Item("ABC", "Bose Headphones", 149.99));
	}
	
	@Before
	public void setUp() {
		itemReactiveRepository.deleteAll()
		                  .thenMany(Flux.fromIterable(data()))
		                  .flatMap(itemReactiveRepository::save)
		                  .doOnNext(item->{
		                	  System.out.println("Inserted item: " +item);
		                  })
		                  .blockLast();
	}
	
	@Test
	public void getAllItems() {
		
		webTestClient.get().uri(ITEM_END_POINT_V1)
		         .exchange()
		         .expectStatus().isOk()
		         .expectHeader().contentType(MediaType.APPLICATION_JSON)
		         .expectBodyList(Item.class)
		         .hasSize(5);
	}
	
	@Test
	public void getAllItems_approach2() {
		
		webTestClient.get().uri(ITEM_END_POINT_V1)
		         .exchange()
		         .expectStatus().isOk()
		         .expectHeader().contentType(MediaType.APPLICATION_JSON)
		         .expectBodyList(Item.class)
		         .hasSize(5)
		         .consumeWith(response->{
		        	List<Item> items= response.getResponseBody();
		        	items.forEach(item->{
		        		assertTrue(item.getId()!=null);
		        	});
		         });
	}
	
	@Test
	public void getOneItem() {
		
		webTestClient.get().uri(ITEM_END_POINT_V1.concat("/{id}"), "ABC")
		         .exchange()
		         .expectStatus().isOk()
		         .expectBody().jsonPath("$.price", 149.99);
	}
	
	@Test
	public void getOneItem_notFound() {
		
		webTestClient.get().uri(ITEM_END_POINT_V1.concat("/{id}"), "XYZ")
		         .exchange()
		         .expectStatus().isNotFound();
	}
	
	@Test
	public void createItem() {
		
		Item item = new Item(null, "Iphone X", 999.99);
		
		webTestClient.post().uri(ITEM_END_POINT_V1)
		         .contentType(MediaType.APPLICATION_JSON)
		         .body(Mono.just(item), Item.class)
		         .exchange()
		         .expectStatus().isCreated()
		         .expectBody()
		         .jsonPath("$.id").isNotEmpty()
		         .jsonPath("$.description").isEqualTo("Iphone X");
	}
	
	@Test
	public void deleteItem() {
		webTestClient.delete().uri(ITEM_END_POINT_V1.concat("/{id}"), "ABC")
		         .exchange()
		         .expectStatus().isOk()
		         .expectBody(Void.class);
	}
	
	@Test
	public void updateItem() {
		double newPrice= 100.99;
		Item updateItem = new Item(null, "Bose Headphones", newPrice);
		
		webTestClient.put().uri(ITEM_END_POINT_V1.concat("/{id}"), "ABC")
		         .contentType(MediaType.APPLICATION_JSON)
		         .accept(MediaType.APPLICATION_JSON)
		         .body(Mono.just(updateItem), Item.class)
		         .exchange()
		         .expectStatus().isOk()
		         .expectBody().jsonPath("$.price", newPrice);
	}
	
    @Test
    public void runTimeException(){
        webTestClient.get().uri(ITEM_END_POINT_V1+"/runtimeException")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("RuntimeException Occurred.");

    }
	
}
