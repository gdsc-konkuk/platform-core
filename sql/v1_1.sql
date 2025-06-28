-- 1. 기존 email_receivers 테이블 백업 (롤백을 위해)
CREATE TABLE IF NOT EXISTS gdsc.email_receivers_backup AS
SELECT *
FROM gdsc.email_receivers;

-- 2. 임시 테이블 생성 (새로운 구조)
CREATE TABLE gdsc.email_receivers_new
(
    receiver_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id           BIGINT                                             NOT NULL,
    receiver_email    VARCHAR(255)                                       NOT NULL,
    receiver_name     VARCHAR(255)                                       NOT NULL,
    send_status       ENUM ('WAITING', 'PENDING', 'COMPLETED', 'FAILED') NOT NULL DEFAULT 'WAITING',
    status_updated_at DATETIME(6)                                        NOT NULL,
    sent_at           DATETIME(6)                                        NULL,
    CONSTRAINT fk_email_receivers_task
        FOREIGN KEY (task_id) REFERENCES gdsc.email_task (task_id)
            ON UPDATE CASCADE ON DELETE CASCADE
);

-- 3. 기존 데이터를 새로운 테이블로 마이그레이션
INSERT INTO gdsc.email_receivers_new
(task_id, receiver_email, receiver_name, send_status, status_updated_at, sent_at)
SELECT er.task_id,
       er.receiver_email,
       er.receiver_name,
       CASE
           WHEN et.is_sent = TRUE THEN 'COMPLETED'
           ELSE 'WAITING'
           END                     as send_status,
       COALESCE(et.send_at, NOW()) as status_updated_at,
       CASE
           WHEN et.is_sent = TRUE THEN et.send_at
           ELSE NULL
           END                     as sent_at
FROM gdsc.email_receivers er
         JOIN gdsc.email_task et ON er.task_id = et.task_id;

-- 4. 기존 테이블 교체
DROP TABLE gdsc.email_receivers;
RENAME TABLE gdsc.email_receivers_new TO gdsc.email_receivers;

CREATE INDEX idx_email_receivers_task_id ON gdsc.email_receivers(task_id);

SELECT
    'ID Assignment Check' as description,
    COUNT(*) as total_records,
    COUNT(receiver_id) as records_with_id,
    MIN(receiver_id) as min_id,
    MAX(receiver_id) as max_id
FROM gdsc.email_receivers;

SELECT
    et.task_id,
    et.is_sent as task_sent,
    COUNT(*) as total_receivers,
    SUM(CASE WHEN er.send_status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_receivers,
    SUM(CASE WHEN er.send_status = 'WAITING' THEN 1 ELSE 0 END) as waiting_receivers
FROM gdsc.email_task et
         LEFT JOIN gdsc.email_receivers er ON et.task_id = er.task_id
GROUP BY et.task_id, et.is_sent;

SELECT
    'Migration Summary' as description,
    COUNT(*) as total_receivers,
    SUM(CASE WHEN send_status = 'WAITING' THEN 1 ELSE 0 END) as waiting_count,
    SUM(CASE WHEN send_status = 'PENDING' THEN 1 ELSE 0 END) as pending_count,
    SUM(CASE WHEN send_status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_count,
    SUM(CASE WHEN send_status = 'FAILED' THEN 1 ELSE 0 END) as failed_count,
    MIN(receiver_id) as first_id,
    MAX(receiver_id) as last_id
FROM gdsc.email_receivers;