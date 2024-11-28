package gdsc.konkuk.platformcore.domain.attendance.entity;

public enum AttendanceType {
    ATTEND("ATTEND"), ABSENT("ABSENT"), LATE("LATE"),;

    private final String value;

    AttendanceType(String value) {
        this.value = value;
    }

    public boolean isAbsent() {
        return this == ABSENT;
    }

    @Override
    public String toString() {
        return value;
    }
}
