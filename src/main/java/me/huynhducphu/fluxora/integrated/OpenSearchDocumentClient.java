package me.huynhducphu.fluxora.integrated;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenSearchDocumentClient {

    private final RestClient restClient;
    private final String index;

    public OpenSearchDocumentClient(
            RestClient.Builder builder,
            @Value("${fluxora.opensearch.url}") String url,
            @Value("${fluxora.opensearch.index}") String index) {
        this.restClient = builder.baseUrl(url).build();
        this.index = index;
    }

    public void indexDocument(String documentId, String json) {
        // PUT cùng ID sẽ ghi đè document khi Kafka giao lại message.
        // Chờ OpenSearch trả thành công trước khi listener hoàn tất.
        restClient.put()
                .uri("/{index}/_doc/{id}", index, documentId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .retrieve()
                .toBodilessEntity();
    }
}
