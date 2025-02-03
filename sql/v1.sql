-- 1. Add new required columns to attendance table
ALTER TABLE gdsc.attendance
    ADD COLUMN title           varchar(255) NULL,
    ADD COLUMN attendance_time datetime(6)  NULL;

-- 2. Migrate data from event table to attendance table
UPDATE gdsc.attendance a
    JOIN gdsc.event e ON a.event_id = e.id
SET a.title           = e.title,
    a.attendance_time = e.start_at;

-- 3. Modify member table
-- 3.1. Rename member_id to student_id
ALTER TABLE gdsc.member
    RENAME COLUMN member_id TO student_id;

-- 3.2. Modify ENUM to include new value and update data
ALTER TABLE gdsc.member
    MODIFY COLUMN member_role enum ('ADMIN', 'CORE', 'LEAD', 'MEMBER') NOT NULL;

UPDATE gdsc.member
SET member_role = 'CORE'
WHERE member_role = 'ADMIN';

-- 4. Modify participant table
-- 4.1. Add attendance_type column
ALTER TABLE gdsc.participant
    ADD COLUMN attendance_type enum ('ATTEND', 'ABSENT', 'LATE') NOT NULL DEFAULT 'ABSENT';

-- 4.2. Convert existing boolean data to ENUM
UPDATE gdsc.participant
SET attendance_type =
        IF(attendance = TRUE, 'ATTEND', 'ABSENT');

-- 5. Remove old columns and modify new columns as required
-- 5.1. Drop event-related tables
DROP TABLE IF EXISTS gdsc.event_image;
DROP TABLE IF EXISTS gdsc.event;

-- 5.2. Remove event_id from attendance table
ALTER TABLE gdsc.attendance
    DROP COLUMN event_id;

-- 5.3. Remove password column from member table
ALTER TABLE gdsc.member
    DROP COLUMN password;

-- 5.4. Remove attendance column from participant table
ALTER TABLE gdsc.participant
    DROP COLUMN attendance;

-- 6. Migration complete
