package com.org.test.batch.writer;

import com.org.test.entity.CustomerEntity;
import com.org.test.publisher.NotificationPublisher;
import com.org.test.repository.CustomerRepository;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class ImportCustomerWriter implements ItemWriter<List<CustomerEntity>> {

    private final CustomerRepository customerRepository;
    private final NotificationPublisher notificationPublisher;

    public ImportCustomerWriter(CustomerRepository customerRepository, NotificationPublisher notificationPublisher) {
        this.customerRepository = customerRepository;
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public void write(Chunk<? extends List<CustomerEntity>> lists) throws Exception {
        List<CustomerEntity> customerEntities = lists.getItems().stream().flatMap(Collection::stream)
                .toList();

        customerRepository.saveAll(customerEntities);

        customerEntities.forEach(notificationPublisher::publishMessage);
    }
}
