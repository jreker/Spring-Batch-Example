package com.jreker.spring.batchexample.batch;

import com.jreker.spring.batchexample.batch.tasklet.RecordCheckTasklet;
import com.jreker.spring.batchexample.model.Tatbestand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Configuration
public class BatchConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);
    /**
     * Custom conversionService that converts the String to a Date object
     * @return returns the conversion service
     */
    private ConversionService createConversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new Converter<String, Date>() {
            @Override
            public Date convert(String source) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                Date date;
                try {
                    date = sdf.parse(source);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                return date;
            }
        });
        return conversionService;
    }

    @Bean
    public FlatFileItemReader<Tatbestand> reader() {
        return new FlatFileItemReaderBuilder<Tatbestand>()
                .name("bussgeldItemReader")
                .resource(new ClassPathResource("verkehrsstraftaten.csv"))
                .delimited()
                .delimiter(";")
                .names("tattag", "tatzeit","tatort","tatort2","tatbestand","betrag")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Tatbestand>() {{
                    setTargetType(Tatbestand.class);
                    setConversionService(createConversionService());
                }})
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Tatbestand> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Tatbestand>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO tatbestand (tattag,tatzeit,tatort,tatort2,tatbestand,betrag) VALUES (:tattag,:tatzeit,:tatort,:tatort2,:tatbestand,:betrag)")
                .dataSource(dataSource)
                .build();
    }

    //DEFINES THE JOB
    @Bean
    public Job importTatbestandJob(JobRepository jobRepository,
                             JobCompletionNotificationListener listener, Step step1, Step taskletStep1) {
        return new JobBuilder("importTatbestandJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                //.preventRestart()
                .flow(taskletStep1)
                .next(step1)
                .end()
                .build();
    }

    //DEFINES THE SINGLE STEP
    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager, JdbcBatchItemWriter<Tatbestand> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Tatbestand, Tatbestand> chunk(10, transactionManager) // 10 Records at a time, max write
                .reader(reader())
                .processor(processor())
                .writer(writer)
                //.faultTolerant()
                //.skipLimit(10)
                //.skip(ArrayIndexOutOfBoundsException.class)
                //.retryLimit(3)
                //.retry(NullPointerException.class)
                .build();
    }
    //tasklet Step
    @Bean
    public Step taskletStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("taskletStep1", jobRepository)
                .tasklet(RecordCheckTasklet(), transactionManager)
                .build();
    }
    @Bean
    public RecordCheckTasklet RecordCheckTasklet() {
        return new RecordCheckTasklet();
    }
    @Bean
    public TatbestandItemProcessor processor() {
        return new TatbestandItemProcessor();
    }
}
