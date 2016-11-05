package it.tredi.ecm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadConfiguration {
	@Value("${thread.corePoolSize}")
	private int corePoolSize = 0;
	
	@Value("${thread.maxPoolSize}")
	private int maxPoolSize = 10;
	
	@Value("${thread.queueCapacity}")
	private int queueCapacity = 25;
	
	@Value("${thread.threadNamePrefix}")
	private String threadNamePrefix = "ecm-scheduled-tasks-thread-";
	
	@Value("${thread.waitForTasksToCompleteOnShutdown}")
	private boolean waitForTasksToCompleteOnShutdown = true;

	@Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        
        return executor;
    }
}
