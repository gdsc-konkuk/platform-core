package gdsc.konkuk.platformcore.application.member;

import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceInfo;
import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceQueryDto;
import gdsc.konkuk.platformcore.application.member.dtos.MemberAttendanceAggregate;
import gdsc.konkuk.platformcore.application.member.dtos.MemberAttendanceInfos;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberAttendanceAggregator {

    public static List<MemberAttendanceAggregate> process(
            final List<MemberAttendanceQueryDto> queryDtos) {
        Map<Long, MemberAttendanceAggregate> memberAttendanceAggregates = new HashMap<>();
        Map<Long, MemberAttendanceInfos> memberAttendanceInfos = new HashMap<>();
        aggregateMemberAttendances(memberAttendanceAggregates, memberAttendanceInfos, queryDtos);
        validateAggregation(memberAttendanceAggregates, memberAttendanceInfos);
        return mergeIntoList(memberAttendanceAggregates, memberAttendanceInfos);
    }

    /**
     * 사용자별 출석 정보를 집계하는 함수로 사용자별 출석 통계 정보와 출석 상세 정보를 집계한다.
     *
     * @param memberAttendanceAggregates 출석 통계 정보
     * @param memberAttendanceInfos      출석 상세 정보
     * @param queries                    출석정보 쿼리 dto
     */
    private static void aggregateMemberAttendances(
            final Map<Long, MemberAttendanceAggregate> memberAttendanceAggregates,
            final Map<Long, MemberAttendanceInfos> memberAttendanceInfos,
            final List<MemberAttendanceQueryDto> queries) {
        for (MemberAttendanceQueryDto queryDto : queries) {
            memberAttendanceAggregates.putIfAbsent(queryDto.getMemberId(),
                    MemberAttendanceAggregate.from(queryDto));
            addToMemberAttendanceRecords(memberAttendanceInfos, queryDto);
        }
    }

    /**
     * query 데이터를 읽고 사용자별 기록에 추가하는 함수
     */
    private static void addToMemberAttendanceRecords(
            final Map<Long, MemberAttendanceInfos> attendanceRecordsMap,
            final MemberAttendanceQueryDto queryDto) {
        MemberAttendanceInfos records = attendanceRecordsMap
                .computeIfAbsent(queryDto.getMemberId(), key -> new MemberAttendanceInfos());
        records.addAttendanceInfo(MemberAttendanceInfo.from(queryDto));
    }

    private static void validateAggregation(
            final Map<Long, MemberAttendanceAggregate> memberAttendanceSummary,
            final Map<Long, MemberAttendanceInfos> memberAttendanceDetails) {
        if (memberAttendanceSummary.size() != memberAttendanceDetails.size()) {
            throw new IllegalStateException(
                    "Member attendance summary and details are not matched");
        }
    }

    /**
     * 사용자별 출석 통계 정보에 출석 상세 정보들을 추가하고 리스트로 반환하는 함수
     *
     * @param memberAttendanceAggregates 출석 통계 정보
     * @param memberAttendanceInfos      출석 상세 정보
     * @return 사용자별 출석 통계 정보 리스트
     */
    private static List<MemberAttendanceAggregate> mergeIntoList(
            final Map<Long, MemberAttendanceAggregate> memberAttendanceAggregates,
            final Map<Long, MemberAttendanceInfos> memberAttendanceInfos) {
        for (Map.Entry<Long, MemberAttendanceInfos> memberAttendance : memberAttendanceInfos.entrySet()) {
            MemberAttendanceAggregate aggregate = memberAttendanceAggregates.get(
                    memberAttendance.getKey());
            aggregate.setAttendanceInfoList(memberAttendance.getValue().getAttendanceInfoList());
            aggregate.setTotalAttendances(memberAttendance.getValue().getTotalAttendances());
            aggregate.setActualAttendances(memberAttendance.getValue().getActualAttendances());
        }
        return memberAttendanceAggregates.values().stream().toList();
    }
}
