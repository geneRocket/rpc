package executor;

import javafx.concurrent.Task;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolTaskExecutorImpl implements TaskExecutor {
    private ExecutorService executorService;

    public ThreadPoolTaskExecutorImpl(){
        init(1);
    }

    @Override
    public void init(Integer threads) {
        executorService = new ThreadPoolExecutor(
                threads,
                threads,
                0,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(),
                new ThreadFactory() {
                    private AtomicInteger atomicInteger = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r,"threadpool-" + atomicInteger.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Override
    public void submit(Runnable runnable) {
        executorService.submit(runnable);
    }
}
