package gdsc.konkuk.platformcore.controller.email.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class EmailTaskListResponse {

  private final List<SimpleEmailTaskResponse> emailTasks;

  public EmailTaskListResponse(List<SimpleEmailTaskResponse> emailTasks) {
    this.emailTasks = emailTasks;
  }
}
