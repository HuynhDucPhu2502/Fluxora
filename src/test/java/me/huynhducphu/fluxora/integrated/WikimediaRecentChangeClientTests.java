package me.huynhducphu.fluxora.integrated;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class WikimediaRecentChangeClientTests {

    @Test
    void skipsEventsWithoutDataAndContinuesWithRecentChanges() {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(request -> Mono.just(ClientResponse.create(HttpStatus.OK)
                        .header("Content-Type", MediaType.TEXT_EVENT_STREAM_VALUE)
                        .body(": heartbeat\n\nid: 1\nevent: message\n\ndata: {\"id\":2}\n\n")
                        .build()))
                .build();

        StepVerifier.create(new WikimediaRecentChangeClient(webClient).streamRecentChanges())
                .expectNext("{\"id\":2}")
                .verifyComplete();
    }
}
