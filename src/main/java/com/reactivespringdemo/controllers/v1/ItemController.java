package com.reactivespringdemo.controllers.v1;

import com.reactivespringdemo.documents.Item;
import com.reactivespringdemo.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.reactivespringdemo.constants.ItemConstants.ITEM_ENDPOINT_V1;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ITEM_ENDPOINT_V1)
    public Flux<Item> getAllItems() {
        return itemReactiveRepository.findAll();
    }

    @GetMapping(ITEM_ENDPOINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> getItemById(@PathVariable String id) {
        return itemReactiveRepository.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ITEM_ENDPOINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item) {
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ITEM_ENDPOINT_V1 + "/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        return itemReactiveRepository.deleteById(id);
    }

    @GetMapping(ITEM_ENDPOINT_V1 + "/runtimeException")
    public Flux<Item> runtimeException() {
        return itemReactiveRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("Runtime Exception occurred")));
    }

    @PutMapping(ITEM_ENDPOINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id,
                                                 @RequestBody Item item) {
        return itemReactiveRepository.findById(id)
                .flatMap(currentItem -> {
                    currentItem.setPrice(item.getPrice());
                    currentItem.setDescription(item.getDescription());
                    return itemReactiveRepository.save(currentItem);
                })
                .map(updateItem -> new ResponseEntity<>(updateItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
