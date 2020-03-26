package com.reactivespringdemo.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "ana", "jack", "jenny");

    @Test
    public void transformUsingMap() {
        Flux<Integer> namesLength = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();
        StepVerifier.create(namesLength)
                .expectNext(4,3,4,5)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {
        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> Flux.fromIterable(convertToList(s)))
                .log(); //flatMap returns flux of each string
        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_parallel() {
        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2) // returns Flux<Flux<String>> -> (A,B), (C,D)
                .flatMap(s -> s.map(this::convertToList).subscribeOn(parallel())) // returns Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) // returns Flux<String>
                .log(); // flatMap returns flux of each string
        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_parallel_maintainOrder() {
        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2) // returns Flux<Flux<String>> -> (A,B), (C,D)
                .flatMapSequential(s -> s.map(this::convertToList).subscribeOn(parallel())) // returns Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) // returns Flux<String>
                .log(); // flatMap returns flux of each string
        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }
}
