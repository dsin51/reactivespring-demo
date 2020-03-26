package com.reactivespringdemo.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void fluxTest() {
        Flux<String> stringFlux = Flux.just("string 1", "string 2", "string 3")
                //.concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("After error"))
                .log();
        stringFlux
                .subscribe(System.out::println,
                        e -> System.err.println("Exception is: " + e),
                        () -> System.out.println("Completed!!"));
    }

    @Test
    public void fluxTestElements_WithoutError() {
        Flux<String> stringFlux = Flux.just("String 1", "String 2", "String 3")
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("String 1")
                .expectNext("String 2")
                .expectNext("String 3")
                .verifyComplete();
    }

    @Test
    public void monoTest() {
        Mono<String> stringMono = Mono.just("Spring");
        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTestError() {
        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
