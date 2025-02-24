package gdsc.konkuk.platformcore.controller.member.dtos;

import gdsc.konkuk.platformcore.application.member.dtos.MemberUpdateCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequest {

    @NotNull
    @Valid
    private List<MemberUpdateInfo> memberUpdateInfoList;

    public static List<MemberUpdateCommand> toCommand(MemberUpdateRequest request) {
        return request.getMemberUpdateInfoList().stream()
                .map(MemberUpdateInfo::toCommand)
                .toList();
    }
}
