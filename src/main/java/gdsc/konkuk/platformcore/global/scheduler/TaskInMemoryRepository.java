package gdsc.konkuk.platformcore.global.scheduler;

import gdsc.konkuk.platformcore.global.exceptions.GlobalErrorCode;
import gdsc.konkuk.platformcore.global.exceptions.TaskNotFoundException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import org.springframework.stereotype.Component;

@Component
public class TaskInMemoryRepository {

  private final ConcurrentMap<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();

  public void addTask(String taskId, ScheduledFuture<?> future) {
    taskMap.put(taskId, future);
  }

  public ScheduledFuture<?> getTask(String taskId) {

    ScheduledFuture<?> future = taskMap.get(taskId);
    if(future == null) {
      throw TaskNotFoundException.of(GlobalErrorCode.SCHEDULED_TASK_NOT_AVAILABLE);
    }
    return future;
  }

  public int size() {
    return taskMap.size();
  }

  public void removeAll() {
    for(ScheduledFuture<?> task : taskMap.values()) {
      task.cancel(true);
    }
    taskMap.clear();
  }

  public void removeTask(String taskId) {
    ScheduledFuture<?> task = taskMap.remove(taskId);
    if(task == null) {
      throw TaskNotFoundException.of(GlobalErrorCode.SCHEDULED_TASK_NOT_AVAILABLE);
    }
  }
}
