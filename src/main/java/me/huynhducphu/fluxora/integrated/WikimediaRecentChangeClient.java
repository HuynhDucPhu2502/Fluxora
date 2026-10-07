package me.huynhducphu.fluxora.integrated;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Objects;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WikimediaRecentChangeClient {

    WebClient webClient;

    private static final String URI = "/v2/stream/recentchange";

    public WikimediaRecentChangeClient(
            @Qualifier(value = "wikimediaWebClient")
            WebClient webClient
    ) {
        this.webClient = webClient;
    }

    public Flux<String> streamRecentChanges() {
        return webClient
                .get()
                .uri(URI)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                .filter(Objects::nonNull)
                .map(ServerSentEvent::data);
    }


}
