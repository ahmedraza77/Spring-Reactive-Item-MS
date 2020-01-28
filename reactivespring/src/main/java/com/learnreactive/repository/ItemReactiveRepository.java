package com.learnreactive.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.learnreactive.document.Item;

import reactor.core.publisher.Mono;


public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String>{

	//custom methods for methods which are not present in repository
	Mono<Item> findByDescription(String Description);
}
