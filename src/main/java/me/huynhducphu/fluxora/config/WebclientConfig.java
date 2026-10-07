package me.huynhducphu.fluxora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebclientConfig {

    private static final String BASE_URL = "https://stream.wikimedia.org";
    private static final String USER_AGENT_HEADER_TITLE = "User-Agent";
    private static final String USER_AGENT_HEADER_VALUE = "Fluxora/1.0.0";

    @Bean
    public WebClient wikimediaWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(BASE_URL)
                .defaultHeader(USER_AGENT_HEADER_TITLE, USER_AGENT_HEADER_VALUE)
                .build();
    }

}
