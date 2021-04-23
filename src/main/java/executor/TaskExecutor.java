package executor;

public interface TaskExecutor {
    void init(Integer threads);
    void submit(Runnable runnable);
}
