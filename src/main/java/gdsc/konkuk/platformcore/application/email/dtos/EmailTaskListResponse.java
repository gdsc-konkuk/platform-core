package gdsc.konkuk.platformcore.application.email.dtos;

import java.util.List;

public record EmailTaskListResponse(
        List<SimpleEmailTaskResponse> emailTasks
) {

}
