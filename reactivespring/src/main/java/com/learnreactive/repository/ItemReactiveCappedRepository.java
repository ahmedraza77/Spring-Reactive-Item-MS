package com.learnreactive.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import com.learnreactive.document.ItemCapped;

import reactor.core.publisher.Flux;


public interface ItemReactiveCappedRepository extends ReactiveMongoRepository<ItemCapped, String>{

	@Tailable                          // opens the tailable cursor; connections remains open after all the results are retrieved
	Flux<ItemCapped> findItemsBy();         // used for sending streams of data; keeps on sending as new data arrives
}
