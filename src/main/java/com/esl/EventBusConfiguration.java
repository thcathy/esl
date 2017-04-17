package com.esl;

import com.esl.service.event.history.UpdatePracticeHistoryEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import reactor.Environment;
import reactor.bus.EventBus;

import javax.annotation.PostConstruct;

import static reactor.bus.selector.Selectors.$;

@Configuration
@EnableAsync
public class EventBusConfiguration
{
    private static Logger log = LoggerFactory.getLogger(EventBusConfiguration.class);

    @Bean
    Environment env() {
        return Environment
                .initializeIfEmpty()
                .assignErrorJournal();
    }

    @Bean
    EventBus createEventBus(Environment env) {
        return EventBus.create(env, Environment.THREAD_POOL);
    }

    @PostConstruct
    public void CreateHistoryConsumer(){
        eventBus.on($("addHistory"), createHistoryConsumer);
    }

    @Autowired
    private EventBus eventBus;

    @Autowired
    private UpdatePracticeHistoryEventConsumer createHistoryConsumer;

}