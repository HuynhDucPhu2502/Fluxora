package me.huynhducphu.fluxora.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WikimediaConsumer {

    @KafkaListener(
            topics = "wikimedia.recentchange",
            groupId = "wikimedia-opensearch"
    )
    public void consume(String data) {
        log.info("Received: {}", data);

    }


}
