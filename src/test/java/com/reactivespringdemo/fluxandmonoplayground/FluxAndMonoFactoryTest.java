package com.reactivespringdemo.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFactoryTest {
    List<String> names = Arrays.asList("adam", "ana", "jack", "jenny");

    @Test
    public void fluxUsingIterable() {
        Flux<String> namesFlux = Flux.fromIterable(names);
        StepVerifier.create(namesFlux.log())
                .expectNext("adam", "ana", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray() {
        String[] names = new String[]{"adam", "ana", "jack", "jenny"};
        Flux<String> namesFlux = Flux.fromArray(names);
        StepVerifier.create(namesFlux.log())
                .expectNext("adam", "ana", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream() {
        Flux<String> namesFlux = Flux.fromStream(names.stream());
        StepVerifier.create(namesFlux.log())
                .expectNext("adam", "ana", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty() {
        Mono<String> mono = Mono.justOrEmpty(null); //return Mono.Empty
        StepVerifier.create(mono.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        Mono<String> stringMono = Mono.fromSupplier(() -> "Spring");
        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1,5);
        StepVerifier.create(integerFlux.log())
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }
}
