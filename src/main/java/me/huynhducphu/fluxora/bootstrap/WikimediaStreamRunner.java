package me.huynhducphu.fluxora.bootstrap;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import me.huynhducphu.fluxora.integrated.WikimediaRecentChangeClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WikimediaStreamRunner implements ApplicationRunner {

    WikimediaRecentChangeClient wikimediaRecentChangeClient;

    KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        wikimediaRecentChangeClient
                .streamRecentChanges()
                .concatMap(data ->
                        Mono.fromCallable(() ->
                                        kafkaTemplate.send(
                                                "wikimedia.recentchange", data))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(future -> Mono.fromFuture(future))
                )
                .subscribe(
                        result -> log.info(
                                "Sent to partition={}, offset={}",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        ),
                        error -> log.error(
                                "Wikimedia → Kafka stream failed", error)
                );
    }
}
