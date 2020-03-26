package com.reactivespringdemo.handler;

import com.reactivespringdemo.documents.Item;
import com.reactivespringdemo.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ItemsHandler {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getItemById(ServerRequest serverRequest) {
        Mono<Item> itemResponse = itemReactiveRepository.findById(serverRequest.pathVariable("id"));
        /*return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findById(serverRequest.pathVariable("id")), Item.class)
                .switchIfEmpty(ServerResponse.notFound().build());*/
        return itemResponse.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromObject(item))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
        Mono<Item> itemToSave = serverRequest.bodyToMono(Item.class);
        return itemToSave.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemReactiveRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Void> deleteItem = itemReactiveRepository.deleteById(id);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleteItem, Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
                .flatMap(item -> itemReactiveRepository.findById(id)
                        .flatMap(currentItem -> {
                            currentItem.setDescription(item.getDescription());
                            currentItem.setPrice(item.getPrice());
                            return itemReactiveRepository.save(currentItem);
                        }));
        return updatedItem.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromObject(item)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
