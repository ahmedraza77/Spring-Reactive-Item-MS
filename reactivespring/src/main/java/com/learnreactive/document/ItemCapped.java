package com.learnreactive.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document // @Entity in case of relational DB
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCapped {

	@Id
	private String id;
	private String description;
	private double price;
}
