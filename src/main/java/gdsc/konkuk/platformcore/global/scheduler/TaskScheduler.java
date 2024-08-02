package gdsc.konkuk.platformcore.global.scheduler;

public interface TaskScheduler {
  void scheduleSyncTask(Object task, long delay);
  void cancelTask(String taskId);
}
