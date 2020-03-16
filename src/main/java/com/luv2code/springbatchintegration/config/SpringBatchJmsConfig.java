package com.luv2code.springbatchintegration.config;

import com.luv2code.springbatchintegration.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.jms.JmsItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@EnableJms
@Configuration
@EnableBatchProcessing
@Slf4j
@PropertySource("classpath:application.yml")
public class SpringBatchJmsConfig {

//    @Value("${spring.jms.template.default-destination}")
//    public static String MY_QUEUE;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public JmsListenerContainerFactory<?> queueListenerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
//        factory.setConcurrency("1-5");
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }


    @Bean
    public JmsItemReader<Person> personJmsItemReader() {
        log.info("Reading.............");
        JmsItemReader<Person> personJmsItemReader = new JmsItemReader<>();
        personJmsItemReader.setJmsTemplate(jmsTemplate);
        personJmsItemReader.setItemType(Person.class);
        return personJmsItemReader;
    }


    @Bean
    public FlatFileItemWriter<Person> personFlatFileItemWriter() {
        log.info("Writing.............");
        FlatFileItemWriter<Person> personFlatFileItemWriter = new FlatFileItemWriter<>();
        personFlatFileItemWriter.setLineAggregator(person -> person.toString() + "123");
        personFlatFileItemWriter.setLineSeparator(System.lineSeparator());
        personFlatFileItemWriter.setResource(new FileSystemResource("person.txt"));
        return personFlatFileItemWriter;
    }

//    @Bean
//    public Step step1() {
//        return stepBuilderFactory.get("step1")
////                .tasklet((contribution, chunkContext) -> {
////                    log.info("step executed");
////                    return RepeatStatus.FINISHED;
////                })
//                .<Person, Person>chunk(1)
//                .reader(personJmsItemReader(messageConverter()))
//                .writer(personFlatFileItemWriter())
//                .build();
//    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Person, Person> chunk(1)
                .reader(personJmsItemReader())
                .writer(personFlatFileItemWriter())
                .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, chunkContext) -> {
                    log.info("step executed");
                    return RepeatStatus.FINISHED;
                })
//                .<Person, Person>chunk(1)
//                .reader(personJmsItemReader(messageConverter()))
//                .writer(personFlatFileItemWriter())
                .build();
    }

    @Bean
    public Job job(Step step1) throws Exception {
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(jobExecutionListener())
                .flow(step1)
                .next(step2())
                .end()
                .build();
    }

    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
//            @Override
//            public void beforeJob(JobExecution jobExecution) {
//                IntStream.rangeClosed(1,300).forEach(token->{
//                    Person[] people = {new Person("Jack", "Ryan"), new Person("Raymond", "Red"), new Person("Olivia", "Dunham"),
//                            new Person("Walter", "Bishop"), new Person("Harry", "Bosch")};
//                    for (Person person : people) {
//                        log.info(person.toString());
//                        jmsTemplate.convertAndSend(person);
//                    }
//                });
//            }

            @Override
            public void beforeJob(JobExecution jobExecution) {
//                    Person[] people = {new Person("Jack", "Ryan"), new Person("Raymond", "Red"), new Person("Olivia", "Dunham"),
//                            new Person("Walter", "Bishop"), new Person("Harry", "Bosch")};
//                    for (Person person : people) {
//                        log.info(person.toString());
//                        jmsTemplate.convertAndSend(person);
//                    };
                System.out.println("beforeJob........");
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                System.out.println("afterJob........");
            }
        };
    }
}
