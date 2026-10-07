package me.huynhducphu.fluxora.bootstrap;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.fluxora.integrated.OpenSearchDocumentClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WikimediaConsumer {

    private final OpenSearchDocumentClient openSearchDocumentClient;

    @KafkaListener(
            topics = "wikimedia.recentchange",
            groupId = "wikimedia-opensearch"
    )
    public void consume(ConsumerRecord<String, String> record) {
        String documentId = record.topic() + "-" + record.partition() + "-" + record.offset();
        openSearchDocumentClient.indexDocument(documentId, record.value());
        log.debug("Indexed document {} into OpenSearch", documentId);
    }


}
