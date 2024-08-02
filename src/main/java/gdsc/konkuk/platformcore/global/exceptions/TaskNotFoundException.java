package gdsc.konkuk.platformcore.global.exceptions;

public class TaskNotFoundException extends BusinessException{

  protected TaskNotFoundException(CustomErrorCode errorCode, String logMessage) {
    super(errorCode, logMessage);
  }

  public static TaskNotFoundException of(CustomErrorCode errorCode) {
    return new TaskNotFoundException(errorCode, errorCode.getMessage());
  }
}
