package me.huynhducphu.fluxora.bootstrap;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.huynhducphu.fluxora.integrated.WikimediaRecentChangeClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WikimediaStreamRunner implements ApplicationRunner {

    WikimediaRecentChangeClient wikimediaRecentChangeClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        wikimediaRecentChangeClient
                .streamRecentChanges()
                .subscribe(System.out::println);
    }
}
