create table if not exists gdsc.event
(
    id                 bigint auto_increment primary key,
    title              varchar(255) NOT NULL,
    content            text         NOT NULL,
    location           varchar(255) NOT NULL,
    end_at             datetime(6)  NOT NULL,
    start_at           datetime(6)  NOT NULL,
    retrospect_content text         NOT NULL
);

create table if not exists gdsc.event_image
(
    id       bigint auto_increment primary key,
    event_id bigint       NOT NULL,
    url      varchar(255) NOT NULL,
    CONSTRAINT FOREIGN KEY (event_id)
        REFERENCES gdsc.event (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

create table if not exists gdsc.attendance
(
    id             bigint auto_increment primary key,
    event_id       bigint       NOT NULL,
    active_qr_uuid varchar(255) NULL,
    CONSTRAINT FOREIGN KEY (event_id)
        REFERENCES gdsc.event (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

create table if not exists gdsc.email_task
(
    task_id       bigint auto_increment primary key,
    is_sent       boolean DEFAULT FALSE,
    send_at       datetime(6)  NOT NULL,
    email_content text         NOT NULL,
    email_subject varchar(255) NOT NULL
);

create table if not exists gdsc.email_receivers
(
    task_id        bigint       NOT NULL,
    receiver_email varchar(255) NOT NULL,
    receiver_name  varchar(255) NOT NULL,
    CONSTRAINT FOREIGN KEY (task_id)
        REFERENCES gdsc.email_task (task_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

create table if not exists gdsc.member
(
    id              bigint auto_increment primary key,
    member_id       varchar(255) UNIQUE                NOT NULL,
    member_name     varchar(255)                       NOT NULL,
    password        varchar(255)                       NOT NULL,
    member_role     enum ( 'ADMIN', 'LEAD', 'MEMBER' ) NOT NULL,
    batch           varchar(255)                       NOT NULL,
    department      varchar(255)                       NOT NULL,
    member_email    varchar(255)                       NOT NULL,
    is_activated    boolean DEFAULT TRUE,
    is_deleted      boolean DEFAULT FALSE,
    soft_deleted_at datetime(6)                        NULL
);

create table if not exists gdsc.participant
(
    id            bigint auto_increment primary key,
    member_id     bigint NOT NULL,
    attendance_id bigint NOT NULL,
    attendance    boolean DEFAULT FALSE,
    CONSTRAINT FOREIGN KEY (member_id)
        REFERENCES gdsc.member (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT FOREIGN KEY (attendance_id)
        REFERENCES gdsc.attendance (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
