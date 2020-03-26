package com.reactivespringdemo.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException{
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200)).log();
        infiniteFlux.subscribe(ele -> System.out.println("Value: " + ele));
        Thread.sleep(1000);
    }

    @Test
    public void infiniteSequenceTest() throws InterruptedException{
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .take(3)
                .log();
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }
}
