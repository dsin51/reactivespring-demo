package com.reactivespringdemo.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoErrorTest {

    @Test
    public void errorHandling() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorResume(e -> {
                    System.out.println("Exception: " + e);
                    return Flux.just("default");
                });
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNextCount(3)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    public void errorHandling_OnErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D")) // not executed as there is error returned in previous step
                .onErrorReturn("default");
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNextCount(3)
                .expectNext("default")
                .verifyComplete();

    }
}
