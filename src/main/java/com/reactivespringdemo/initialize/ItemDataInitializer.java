package com.reactivespringdemo.initialize;

import com.reactivespringdemo.documents.Item;
import com.reactivespringdemo.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetUp();
    }

    public List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV", 400.00),
                new Item(null, "LG TV", 500.00),
                new Item(null, "Apple watch", 700.00),
                new Item(null, "Beats Headphones", 100.00),
                new Item("ABC", "Bose Headphones", 100.00));
    }

    private void initialDataSetUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> System.out.println("Item inserted by CLR: " + item));
    }
}
