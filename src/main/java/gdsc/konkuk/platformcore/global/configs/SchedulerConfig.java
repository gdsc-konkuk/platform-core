package gdsc.konkuk.platformcore.global.configs;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class SchedulerConfig {

    private final ScheduledThreadPoolExecutor executor;

    /*
     * ScheduledThreadPoolExecutor with a single thread.
     * Remove Task from the executor's queue when it is canceled.
     * This results using lock inside ThreadPoolExecutor.
     * */
    public SchedulerConfig() {
        this.executor = new ScheduledThreadPoolExecutor(1);
        this.executor.setRemoveOnCancelPolicy(true);
    }

    @Bean
    public ScheduledThreadPoolExecutor getTaskScheduler() {
        return this.executor;
    }
}
