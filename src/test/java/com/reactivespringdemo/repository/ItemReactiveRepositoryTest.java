package com.reactivespringdemo.repository;

import com.reactivespringdemo.documents.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(new Item(null, "Samsung TV", 400.00),
            new Item(null, "LG TV", 500.00),
            new Item(null, "Apple watch", 700.00),
            new Item(null, "Beats Headphones", 100.00),
            new Item("ABC", "Bose Headphones", 100.00));

    @Before
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted Item: " + item))
                .blockLast();
    }

    @Test
    public void getAllItemsTest() {
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemByIdTest() {
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Bose Headphones"))
                .verifyComplete();
    }

    @Test
    public void findItemByDescriptionTest() {
        StepVerifier.create(itemReactiveRepository.findByDescription("LG TV"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItemTest() {
        Item item = new Item(null, "Google Home Mini", 300.00);
        StepVerifier.create(itemReactiveRepository.save(item))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId() != null && item1.getDescription().equals("Google Home Mini"))
                .verifyComplete();
    }

    @Test
    public void updateItemTest() {
        double newPrice = 520.00;
        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(item -> itemReactiveRepository.save(item));
        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 520.00)
                .verifyComplete();
    }

    @Test
    public void deleteItemByIdTest() {
        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")
                .map(Item::getId)
                .flatMap(id -> itemReactiveRepository.deleteById(id));
        StepVerifier.create(deletedItem)
                .expectSubscription()
                .verifyComplete();
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectNextCount(4)
                .verifyComplete();
    }
}
