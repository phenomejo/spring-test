package com.org.test.controller;

import java.time.LocalDateTime;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class BatchController {


    private final JobLauncher jobLauncher;
    private final Job importCustomerJob;

    public BatchController(@StepScope JobLauncher jobLauncher, @Qualifier("importCustomerJob") Job job) {
        this.jobLauncher = jobLauncher;
        this.importCustomerJob = job;
    }

    @GetMapping("/importCustomer")
    public String importCustomer() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLocalDateTime("dateTime", LocalDateTime.now()) // Ensure uniqueness
                    .toJobParameters();

            var status = jobLauncher.run(importCustomerJob, jobParameters).getStatus().name();
            return "Batch job with status: " + status;
        } catch (Exception e) {
            return "Error starting batch job: " + e.getMessage();
        }
    }
}
