package com.learnreactive.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.learnreactive.constants.ItemConstants.*;
import com.learnreactive.handler.ItemsHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class ItemsRouter {
	
	@Bean
	public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler) {
		
		return RouterFunctions
				.route(GET(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(APPLICATION_JSON)), itemsHandler::getAllItems)
				.andRoute(GET(ITEM_FUNCTIONAL_END_POINT_V1+"/{id}").and(accept(APPLICATION_JSON)), itemsHandler::getItemById)
				.andRoute(POST(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(APPLICATION_JSON)), itemsHandler::createItem)
				.andRoute(DELETE(ITEM_FUNCTIONAL_END_POINT_V1+"/{id}").and(accept(APPLICATION_JSON)), itemsHandler::deleteItem)
				.andRoute(PUT(ITEM_FUNCTIONAL_END_POINT_V1+"/{id}").and(accept(APPLICATION_JSON)), itemsHandler::updateItem);
	}
	
    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemsHandler itemsHandler){
        return RouterFunctions
                .route(GET("/fun/runtimeexception").and(accept(APPLICATION_JSON))
                        ,itemsHandler::itemsEx);
    }
	
	@Bean
	public RouterFunction<ServerResponse> streamItemsRoute(ItemsHandler itemsHandler) {
		
		return RouterFunctions
				.route(GET(ITEM_STREAM_FUNCTIONAL_END_POINT_V1).and(accept(APPLICATION_JSON)), itemsHandler::streamItems);
	}

}
