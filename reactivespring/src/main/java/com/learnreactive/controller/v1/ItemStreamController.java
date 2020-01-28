package com.learnreactive.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnreactive.document.ItemCapped;
import com.learnreactive.repository.ItemReactiveCappedRepository;

import reactor.core.publisher.Flux;

import static com.learnreactive.constants.ItemConstants.ITEM_STREAM_END_POINT_V1;

@RestController
public class ItemStreamController {

	@Autowired
	ItemReactiveCappedRepository itemReactiveCappedRepo;
	
	@GetMapping(value = ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)   // Stream JSON introduced in Spring 5
	public Flux<ItemCapped> getItemStream() {
		
		return itemReactiveCappedRepo.findItemsBy();
	}
}
