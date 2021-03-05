SET MODE MySQL; -- for h2 test

CREATE TABLE `rate_limiter_info`
(
    `id`            int(11) unsigned NOT NULL AUTO_INCREMENT,
    `app_name`      varchar(50)      NOT NULL DEFAULT '',
    `resource_name` varchar(50)      NOT NULL DEFAULT '',
    `max_permits`   int(11)          NOT NULL,
    `rate`          int(11)          NOT NULL,
    `created_by`    varchar(50)               DEFAULT NULL,
    `updated_by`    varchar(50)               DEFAULT NULL,
    `created_at`    timestamp        NULL     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    timestamp        NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app_resource_name` (`app_name`, `resource_name`)
) AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4;