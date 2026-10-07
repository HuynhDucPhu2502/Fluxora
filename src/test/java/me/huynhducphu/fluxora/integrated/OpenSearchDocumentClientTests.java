package me.huynhducphu.fluxora.integrated;

import org.junit.jupiter.api.Test;
import me.huynhducphu.fluxora.config.RestClientConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class OpenSearchDocumentClientTests {

    @Test
    void createsClientWithBuilderProvidedByConfiguration() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource(
                    "test", Map.of("fluxora.opensearch.url", "http://localhost:9200",
                    "fluxora.opensearch.index", "wikimedia-recentchange")));
            context.register(RestClientConfig.class, OpenSearchDocumentClient.class);
            context.refresh();
            org.junit.jupiter.api.Assertions.assertNotNull(context.getBean(OpenSearchDocumentClient.class));
        }
    }

    @Test
    void sendsRawJsonWithStableDocumentId() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenSearchDocumentClient client = new OpenSearchDocumentClient(
                builder, "http://localhost:9200", "wikimedia-recentchange");
        String json = "{\"title\":\"Kafka\"}";

        server.expect(requestTo("http://localhost:9200/wikimedia-recentchange/_doc/wikimedia.recentchange-0-42"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(json))
                .andRespond(withSuccess());

        client.indexDocument("wikimedia.recentchange-0-42", json);
        server.verify();
    }

    @Test
    void propagatesIndexingFailureToKafkaListener() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenSearchDocumentClient client = new OpenSearchDocumentClient(
                builder, "http://localhost:9200", "wikimedia-recentchange");
        server.expect(requestTo("http://localhost:9200/wikimedia-recentchange/_doc/event-1"))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(RestClientResponseException.class,
                () -> client.indexDocument("event-1", "{}"));
        server.verify();
    }
}
