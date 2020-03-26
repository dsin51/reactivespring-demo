package com.reactivespringdemo.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> returnsFlux() {
        return Flux.just(1,2,3,4)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> returnsFluxStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping("/mono")
    public Mono<Integer> returnsMono() {
        return Mono.just(1).log();
    }
}
