package com.br.exs.consumer.config;

import com.br.exs.consumer.JobCompletionNotificationListener;
import com.br.exs.consumer.domain.Customer;
import com.br.exs.consumer.domain.DueDayProcessing;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Configuration
public class BatchConfig {
    @Autowired
    @Qualifier("transactionManagerConsumer")
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;

    @Bean
    public ItemReader<Customer> reader(@Qualifier("consumerDataSource") DataSource dataSource) {
        JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT * FROM customer;");
        reader.setRowMapper(new CustomerRowMapper());
        return reader;
    }

    @Bean
    public ItemProcessor<Customer, DueDayProcessing> processor() {
        return new CustomerToDueDayProcessingProcessor();
    }

    @Bean
    public ItemWriter<DueDayProcessing> writer(@Qualifier("consumerDataSource") DataSource dataSource) {
        JdbcBatchItemWriter<DueDayProcessing> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("INSERT INTO due_day_processing (next_due_day, customer_id) VALUES (:next_due_day, :customer_id);");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return writer;
    }

    @Bean
    public Job importJob(JobRepository jobRepository, JobCompletionNotificationListener listener, Step step1) {
        return new JobBuilder("importJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(ItemReader<Customer> reader, ItemWriter<DueDayProcessing> writer, JobRepository jobRepository) {
        return new StepBuilder("step1",jobRepository)
                .<Customer, DueDayProcessing> chunk(10, transactionManager)
                .reader(reader)
                .processor(processor())
                .writer(writer)
                .build();
    }

    public class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setId(rs.getInt("id"));
            customer.setCustomerName(rs.getString("customerName"));
            customer.setDueDay(rs.getInt("dueDay"));
            return customer;
        }
    }

    public class CustomerToDueDayProcessingProcessor implements ItemProcessor<Customer, DueDayProcessing> {
        @Override
        public DueDayProcessing process(Customer Customer) throws Exception {
            DueDayProcessing dueDayProcessing = new DueDayProcessing();
            dueDayProcessing.setNextDueDay(LocalDate.now().plusDays(Customer.getDueDay()));
            dueDayProcessing.setCustomerId(Customer.getId());
            return dueDayProcessing;
        }
    }

}
