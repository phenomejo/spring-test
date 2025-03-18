package com.org.test.batch;

import com.org.test.batch.processor.ImportCustomerProcessor;
import com.org.test.batch.writer.ImportCustomerWriter;
import com.org.test.dto.ImportCustomerDTO;
import com.org.test.entity.CustomerEntity;
import com.org.test.mapper.CustomerMapper;
import com.org.test.publisher.NotificationPublisher;
import com.org.test.repository.CustomerRepository;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import java.io.File;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class ImportCustomerBatchConfig {

    private final PlatformTransactionManager jpaTransactionManager;
    private final JobRepository jobRepository;
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;
    private final NotificationPublisher notificationPublisher;

    @Bean(name = "importCustomerJob")
    public Job importCustomerJob() {
        return new JobBuilder("importCustomerJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return new StepBuilder("importCustomerJob", jobRepository)
                .<ImportCustomerDTO, List<CustomerEntity>>chunk(3, jpaTransactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @StepScope
    @Bean
    public ItemReader<ImportCustomerDTO> reader() {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.getFormat().setDelimiter(",");
        settings.setNumberOfRowsToSkip(0);

        BeanListProcessor<ImportCustomerDTO> processor = new BeanListProcessor<>(ImportCustomerDTO.class);
        settings.setProcessor(processor);

        CsvParser parser = new CsvParser(settings);
        parser.parse(new File("import/customer.csv"));

        return new ListItemReader<>(processor.getBeans());
    }

    @Bean
    public ImportCustomerProcessor processor() {
        return new ImportCustomerProcessor(customerRepository, customerMapper);
    }

    @Bean
    public ImportCustomerWriter writer() {
        return new ImportCustomerWriter(customerRepository, notificationPublisher);
    }

}
