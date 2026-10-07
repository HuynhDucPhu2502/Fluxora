package me.huynhducphu.fluxora.integrated;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WikimediaRecentChangeClient {

    private final WebClient webClient;

    public WikimediaRecentChangeClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://stream.wikimedia.org")
                .build();
    }


}
