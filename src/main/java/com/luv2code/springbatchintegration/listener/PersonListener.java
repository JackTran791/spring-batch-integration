package com.luv2code.springbatchintegration.listener;

import com.luv2code.springbatchintegration.config.SpringBatchJmsConfig;
import com.luv2code.springbatchintegration.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * @author Jack Tran
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PersonListener {

    private final JmsTemplate jmsTemplate;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;


//    @JmsListener(destination = JmsConfig.MY_QUEUE)
    @JmsListener(destination = "person-queue")
    public void listen(@Payload Person person,
                       @Headers MessageHeaders headers, Message message) throws Exception{
        log.info("Message Received: {}", person);

        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(job, params);
    }
}
