package com.luv2code.springbatchintegration.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luv2code.springbatchintegration.config.SpringBatchJmsConfig;
import com.luv2code.springbatchintegration.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonSender {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public int MSG_COUNT = 0;

    @Scheduled(fixedRate = 10000)
    public void sendMessage() {
        MSG_COUNT++;
        Person message = Person
                .builder()
                .firstName("Jack")
                .lastName("Tran")
                .build();

        message.setLastName(message.getLastName() + Integer.toString(MSG_COUNT));

        log.info("======> Sending Message: {}", message);
        jmsTemplate.convertAndSend(message);
        log.info("======> Message sent...");
    }
}
