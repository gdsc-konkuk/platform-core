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

-- 4. Add attendance_type column and migrate data
ALTER TABLE gdsc.participant
    ADD COLUMN attendance_type enum ('ATTEND', 'ABSENT', 'LATE') NOT NULL DEFAULT 'ABSENT';

UPDATE gdsc.participant
SET attendance_type = IF(attendance = TRUE, 'ATTEND', 'ABSENT');

-- 5. Drop constraints and clean up tables
-- 5.1. Remove foreign key constraints
ALTER TABLE gdsc.attendance
    DROP FOREIGN KEY attendance_ibfk_1;

-- 5.2. Drop event-related tables
DROP TABLE gdsc.event_image;
DROP TABLE gdsc.event;

-- 5.3 Drop unused columns
ALTER TABLE gdsc.attendance
    DROP COLUMN event_id;
ALTER TABLE gdsc.member
    DROP COLUMN password,
    MODIFY COLUMN member_role enum ('CORE', 'LEAD', 'MEMBER') NOT NULL;
ALTER TABLE gdsc.participant
    DROP COLUMN attendance;

-- 6. Make migrated columns non-nullable
ALTER TABLE gdsc.attendance
    MODIFY COLUMN title varchar(255) NOT NULL,
    MODIFY COLUMN attendance_time datetime(6) NOT NULL;

-- 7. Migration complete
