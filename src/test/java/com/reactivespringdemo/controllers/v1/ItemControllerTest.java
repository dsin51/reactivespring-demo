package com.reactivespringdemo.controllers.v1;

import com.reactivespringdemo.constants.ItemConstants;
import com.reactivespringdemo.documents.Item;
import com.reactivespringdemo.repository.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
public class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV", 400.00),
                new Item(null, "LG TV", 500.00),
                new Item(null, "Apple watch", 700.00),
                new Item(null, "Beats Headphones", 100.00),
                new Item("ABC", "Bose Headphones", 100.00));
    }

    @Before
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted item: " + item))
                .blockLast();
    }

    @Test
    public void getAllItemsTest() {
        webTestClient.get()
                .uri(ItemConstants.ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    public void getAllItemsTest_approach2() {
        webTestClient.get()
                .uri(ItemConstants.ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith(response -> response.getResponseBody()
                        .forEach(item -> assertTrue(item.getId() != null)));
    }

    @Test
    public void getAllItemsTest_approach3() {
        Flux<Item> itemsFlux = webTestClient.get()
                .uri(ItemConstants.ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();
        StepVerifier.create(itemsFlux)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemByIdTest() {
        webTestClient.get()
                .uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 100.00);
    }

    @Test
    public void getItemByIdTest_notFound() {
        webTestClient.get()
                .uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "DEF")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItemTest() {
        Item item = new Item(null, "iPhoneX", 1000.00);
        webTestClient.post()
                .uri(ItemConstants.ITEM_ENDPOINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("iPhoneX")
                .jsonPath("$.price").isEqualTo(1000.00);
    }

    @Test
    public void deleteItemTest() {
        webTestClient.delete()
                .uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItemTest() {
        double newPrice = 250.00;
        Item item = new Item(null, "Bose Headphones", newPrice);
        webTestClient.put()
                .uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo(newPrice);
    }
    @Test
    public void updateItemTest_notFound() {
        double newPrice = 250.00;
        Item item = new Item(null, "Bose Headphones", newPrice);
        webTestClient.put()
                .uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "XXX")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

}
