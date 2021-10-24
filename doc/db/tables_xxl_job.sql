-- MySQL dump 10.13  Distrib 5.7.35, for Win32 (AMD64)
--
-- Host: 192.168.31.37    Database: xxl-job1
-- ------------------------------------------------------
-- Server version	5.7.35

CREATE database if NOT EXISTS `jd-xxljob` default character set utf8 collate utf8_general_ci;
use `jd-xxljob`;



/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `evn`
--

DROP TABLE IF EXISTS `env`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `env`
(
    `id`          int(11)      NOT NULL AUTO_INCREMENT,
    `env_name`    varchar(128) NOT NULL,
    `remarks`     varchar(128)          DEFAULT NULL,
    `env_value`   varchar(256) NOT NULL,
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `sort`        int(11)      NOT NULL DEFAULT '0',
    `status`      tinyint(1)   NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8 COMMENT ='环境变量';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evn`
--

LOCK TABLES `env` WRITE;
/*!40000 ALTER TABLE `env`
    DISABLE KEYS */;
INSERT INTO `env`
VALUES (1, 'JD_COOKIE', '李伟2号',
        'pt_key=AAJhMoazADDzGgTvjoaytT-Ibu0KB4X58qdZwuAZo44PygrREezoKubrGzKMXlm7OS1-VnAbuhM;pt_pin=jd_trbybgrVjMqE;',
        '2021-09-09 03:04:14', 0, 0),
       (2, 'JD_COOKIE', '京-什么_东',
        'pt_key=AAJhMQSFAEDpKABMNbc_OnIYLPXvxZJ742n5QPFXRtkm0c2Hi0DYHFN7VXDJX1IHNIITB-viic0sL5pk_UN3OUShqTOzCDxb;pt_pin=%E6%80%AA%E7%9B%97%E5%9F%BA%E5%BE%B78768611;',
        '2021-09-09 05:06:12', 0, 0),
       (3, 'JD_COOKIE', '网络电视宝',
        'pt_key=AAJhMjvJADCPtNcnXO-PewkiuqdAU6rTZrshmCYTPOZeX7dNmqCZZ54otfR0STiU2O_vjs9MiDk;pt_pin=jd_511851479ac61;',
        '2021-09-09 05:06:28', 0, 0);
/*!40000 ALTER TABLE `env`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_BLOB_TRIGGERS`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_BLOB_TRIGGERS`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_BLOB_TRIGGERS`
(
    `SCHED_NAME`    varchar(120) NOT NULL,
    `TRIGGER_NAME`  varchar(200) NOT NULL,
    `TRIGGER_GROUP` varchar(200) NOT NULL,
    `BLOB_DATA`     blob,
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    CONSTRAINT `XXL_JOB_QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `XXL_JOB_QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_BLOB_TRIGGERS`
--

LOCK TABLES `XXL_JOB_QRTZ_BLOB_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_BLOB_TRIGGERS`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_BLOB_TRIGGERS`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_CALENDARS`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_CALENDARS`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_CALENDARS`
(
    `SCHED_NAME`    varchar(120) NOT NULL,
    `CALENDAR_NAME` varchar(200) NOT NULL,
    `CALENDAR`      blob         NOT NULL,
    PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_CALENDARS`
--

LOCK TABLES `XXL_JOB_QRTZ_CALENDARS` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_CALENDARS`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_CALENDARS`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_CRON_TRIGGERS`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_CRON_TRIGGERS`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_CRON_TRIGGERS`
(
    `SCHED_NAME`      varchar(120) NOT NULL,
    `TRIGGER_NAME`    varchar(200) NOT NULL,
    `TRIGGER_GROUP`   varchar(200) NOT NULL,
    `CRON_EXPRESSION` varchar(200) NOT NULL,
    `TIME_ZONE_ID`    varchar(80) DEFAULT NULL,
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    CONSTRAINT `XXL_JOB_QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `XXL_JOB_QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_CRON_TRIGGERS`
--

LOCK TABLES `XXL_JOB_QRTZ_CRON_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_CRON_TRIGGERS`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_CRON_TRIGGERS`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_FIRED_TRIGGERS`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_FIRED_TRIGGERS`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_FIRED_TRIGGERS`
(
    `SCHED_NAME`        varchar(120) NOT NULL,
    `ENTRY_ID`          varchar(95)  NOT NULL,
    `TRIGGER_NAME`      varchar(200) NOT NULL,
    `TRIGGER_GROUP`     varchar(200) NOT NULL,
    `INSTANCE_NAME`     varchar(200) NOT NULL,
    `FIRED_TIME`        bigint(13)   NOT NULL,
    `SCHED_TIME`        bigint(13)   NOT NULL,
    `PRIORITY`          int(11)      NOT NULL,
    `STATE`             varchar(16)  NOT NULL,
    `JOB_NAME`          varchar(200) DEFAULT NULL,
    `JOB_GROUP`         varchar(200) DEFAULT NULL,
    `IS_NONCONCURRENT`  varchar(1)   DEFAULT NULL,
    `REQUESTS_RECOVERY` varchar(1)   DEFAULT NULL,
    PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_FIRED_TRIGGERS`
--

LOCK TABLES `XXL_JOB_QRTZ_FIRED_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_FIRED_TRIGGERS`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_FIRED_TRIGGERS`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_JOB_DETAILS`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_JOB_DETAILS`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_JOB_DETAILS`
(
    `SCHED_NAME`        varchar(120) NOT NULL,
    `JOB_NAME`          varchar(200) NOT NULL,
    `JOB_GROUP`         varchar(200) NOT NULL,
    `DESCRIPTION`       varchar(250) DEFAULT NULL,
    `JOB_CLASS_NAME`    varchar(250) NOT NULL,
    `IS_DURABLE`        varchar(1)   NOT NULL,
    `IS_NONCONCURRENT`  varchar(1)   NOT NULL,
    `IS_UPDATE_DATA`    varchar(1)   NOT NULL,
    `REQUESTS_RECOVERY` varchar(1)   NOT NULL,
    `JOB_DATA`          blob,
    PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_JOB_DETAILS`
--

LOCK TABLES `XXL_JOB_QRTZ_JOB_DETAILS` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_JOB_DETAILS`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_JOB_DETAILS`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_LOCKS`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_LOCKS`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_LOCKS`
(
    `SCHED_NAME` varchar(120) NOT NULL,
    `LOCK_NAME`  varchar(40)  NOT NULL,
    PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_LOCKS`
--

LOCK TABLES `XXL_JOB_QRTZ_LOCKS` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_LOCKS`
    DISABLE KEYS */;
INSERT INTO `XXL_JOB_QRTZ_LOCKS`
VALUES ('getSchedulerFactoryBean', 'STATE_ACCESS'),
       ('getSchedulerFactoryBean', 'TRIGGER_ACCESS');
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_LOCKS`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_PAUSED_TRIGGER_GRPS`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_PAUSED_TRIGGER_GRPS`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_PAUSED_TRIGGER_GRPS`
(
    `SCHED_NAME`    varchar(120) NOT NULL,
    `TRIGGER_GROUP` varchar(200) NOT NULL,
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_PAUSED_TRIGGER_GRPS`
--

LOCK TABLES `XXL_JOB_QRTZ_PAUSED_TRIGGER_GRPS` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_PAUSED_TRIGGER_GRPS`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_PAUSED_TRIGGER_GRPS`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_SCHEDULER_STATE`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_SCHEDULER_STATE`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_SCHEDULER_STATE`
(
    `SCHED_NAME`        varchar(120) NOT NULL,
    `INSTANCE_NAME`     varchar(200) NOT NULL,
    `LAST_CHECKIN_TIME` bigint(13)   NOT NULL,
    `CHECKIN_INTERVAL`  bigint(13)   NOT NULL,
    PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_SCHEDULER_STATE`
--

LOCK TABLES `XXL_JOB_QRTZ_SCHEDULER_STATE` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_SCHEDULER_STATE`
    DISABLE KEYS */;
INSERT INTO `XXL_JOB_QRTZ_SCHEDULER_STATE`
VALUES ('getSchedulerFactoryBean', 'MiWiFi-RM1800-srv1631123639457', 1631175431151, 5000);
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_SCHEDULER_STATE`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_SIMPLE_TRIGGERS`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_SIMPLE_TRIGGERS`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_SIMPLE_TRIGGERS`
(
    `SCHED_NAME`      varchar(120) NOT NULL,
    `TRIGGER_NAME`    varchar(200) NOT NULL,
    `TRIGGER_GROUP`   varchar(200) NOT NULL,
    `REPEAT_COUNT`    bigint(7)    NOT NULL,
    `REPEAT_INTERVAL` bigint(12)   NOT NULL,
    `TIMES_TRIGGERED` bigint(10)   NOT NULL,
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    CONSTRAINT `XXL_JOB_QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `XXL_JOB_QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_SIMPLE_TRIGGERS`
--

LOCK TABLES `XXL_JOB_QRTZ_SIMPLE_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_SIMPLE_TRIGGERS`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_SIMPLE_TRIGGERS`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_SIMPROP_TRIGGERS`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_SIMPROP_TRIGGERS`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_SIMPROP_TRIGGERS`
(
    `SCHED_NAME`    varchar(120) NOT NULL,
    `TRIGGER_NAME`  varchar(200) NOT NULL,
    `TRIGGER_GROUP` varchar(200) NOT NULL,
    `STR_PROP_1`    varchar(512)   DEFAULT NULL,
    `STR_PROP_2`    varchar(512)   DEFAULT NULL,
    `STR_PROP_3`    varchar(512)   DEFAULT NULL,
    `INT_PROP_1`    int(11)        DEFAULT NULL,
    `INT_PROP_2`    int(11)        DEFAULT NULL,
    `LONG_PROP_1`   bigint(20)     DEFAULT NULL,
    `LONG_PROP_2`   bigint(20)     DEFAULT NULL,
    `DEC_PROP_1`    decimal(13, 4) DEFAULT NULL,
    `DEC_PROP_2`    decimal(13, 4) DEFAULT NULL,
    `BOOL_PROP_1`   varchar(1)     DEFAULT NULL,
    `BOOL_PROP_2`   varchar(1)     DEFAULT NULL,
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    CONSTRAINT `XXL_JOB_QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `XXL_JOB_QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_SIMPROP_TRIGGERS`
--

LOCK TABLES `XXL_JOB_QRTZ_SIMPROP_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_SIMPROP_TRIGGERS`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_SIMPROP_TRIGGERS`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_TRIGGER_GROUP`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_TRIGGER_GROUP`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_TRIGGER_GROUP`
(
    `id`           int(11)     NOT NULL AUTO_INCREMENT,
    `app_name`     varchar(64) NOT NULL COMMENT '执行器AppName',
    `title`        varchar(12) NOT NULL COMMENT '执行器名称',
    `order`        tinyint(4)  NOT NULL DEFAULT '0' COMMENT '排序',
    `address_type` tinyint(4)  NOT NULL DEFAULT '0' COMMENT '执行器地址类型：0=自动注册、1=手动录入',
    `address_list` varchar(512)         DEFAULT NULL COMMENT '执行器地址列表，多地址逗号分隔',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_TRIGGER_GROUP`
--

LOCK TABLES `XXL_JOB_QRTZ_TRIGGER_GROUP` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_TRIGGER_GROUP`
    DISABLE KEYS */;
INSERT INTO `XXL_JOB_QRTZ_TRIGGER_GROUP`
VALUES (1, 'xxl-job-executor-sample', '示例执行器', 1, 0, NULL);
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_TRIGGER_GROUP`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_TRIGGER_INFO`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_TRIGGER_INFO`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_TRIGGER_INFO`
(
    `id`                        int(11)      NOT NULL AUTO_INCREMENT,
    `job_group`                 int(11)      NOT NULL COMMENT '执行器主键ID',
    `job_cron`                  varchar(128) NOT NULL COMMENT '任务执行CRON',
    `job_desc`                  varchar(255) NOT NULL,
    `add_time`                  datetime              DEFAULT NULL,
    `update_time`               datetime              DEFAULT NULL,
    `author`                    varchar(64)           DEFAULT NULL COMMENT '作者',
    `alarm_email`               varchar(255)          DEFAULT NULL COMMENT '报警邮件',
    `executor_route_strategy`   varchar(50)           DEFAULT NULL COMMENT '执行器路由策略',
    `executor_handler`          varchar(255)          DEFAULT NULL COMMENT '执行器任务handler',
    `executor_param`            varchar(512)          DEFAULT NULL COMMENT '执行器任务参数',
    `executor_block_strategy`   varchar(50)           DEFAULT NULL COMMENT '阻塞处理策略',
    `executor_timeout`          int(11)      NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
    `executor_fail_retry_count` int(11)      NOT NULL DEFAULT '0' COMMENT '失败重试次数',
    `glue_type`                 varchar(50)  NOT NULL COMMENT 'GLUE类型',
    `glue_source`               mediumtext COMMENT 'GLUE源代码',
    `glue_remark`               varchar(128)          DEFAULT NULL COMMENT 'GLUE备注',
    `glue_updatetime`           datetime              DEFAULT NULL COMMENT 'GLUE更新时间',
    `child_jobid`               varchar(255)          DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_TRIGGER_INFO`
--

LOCK TABLES `XXL_JOB_QRTZ_TRIGGER_INFO` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_TRIGGER_INFO`
    DISABLE KEYS */;
INSERT INTO `XXL_JOB_QRTZ_TRIGGER_INFO`
VALUES (2, 1, '0 0 0 * * ? *', '测试任务2', '2021-09-08 18:16:12', '2021-09-08 18:16:12', 'kid', '', 'FIRST',
        'httpJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2021-09-08 18:16:12', '');
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_TRIGGER_INFO`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_TRIGGER_LOG`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_TRIGGER_LOG`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_TRIGGER_LOG`
(
    `id`                        int(11)    NOT NULL AUTO_INCREMENT,
    `job_group`                 int(11)    NOT NULL COMMENT '执行器主键ID',
    `job_id`                    int(11)    NOT NULL COMMENT '任务，主键ID',
    `executor_address`          varchar(255)        DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
    `executor_handler`          varchar(255)        DEFAULT NULL COMMENT '执行器任务handler',
    `executor_param`            varchar(512)        DEFAULT NULL COMMENT '执行器任务参数',
    `executor_sharding_param`   varchar(20)         DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
    `executor_fail_retry_count` int(11)    NOT NULL DEFAULT '0' COMMENT '失败重试次数',
    `trigger_time`              datetime            DEFAULT NULL COMMENT '调度-时间',
    `trigger_code`              int(11)    NOT NULL COMMENT '调度-结果',
    `trigger_msg`               text COMMENT '调度-日志',
    `handle_time`               datetime            DEFAULT NULL COMMENT '执行-时间',
    `handle_code`               int(11)    NOT NULL COMMENT '执行-状态',
    `handle_msg`                text COMMENT '执行-日志',
    `alarm_status`              tinyint(4) NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
    PRIMARY KEY (`id`),
    KEY `I_trigger_time` (`trigger_time`),
    KEY `I_handle_code` (`handle_code`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 62
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_TRIGGER_LOG`
--

LOCK TABLES `XXL_JOB_QRTZ_TRIGGER_LOG` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_TRIGGER_LOG`
    DISABLE KEYS */;
INSERT INTO `XXL_JOB_QRTZ_TRIGGER_LOG`
VALUES (55, 1, 2, '10.0.1.72:9999', 'httpJobHandler', '', NULL, 0, '2021-09-09 13:27:39', 200,
        '任务触发类型：手动触发<br>调度机器：10.0.1.72<br>执行器-注册方式：自动注册<br>执行器-地址列表：[10.0.1.72:9999]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：10.0.1.72:9999<br>code：200<br>msg：null',
        '2021-09-09 13:27:40', 200, 'JDUser(curPin=jd_511851479ac61, nickname=网络电视宝)', 0),
       (56, 1, 2, '10.0.1.72:9999', 'httpJobHandler', '', NULL, 0, '2021-09-09 13:32:46', 200,
        '任务触发类型：手动触发<br>调度机器：10.0.1.72<br>执行器-注册方式：自动注册<br>执行器-地址列表：[10.0.1.72:9999]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：10.0.1.72:9999<br>code：200<br>msg：null',
        '2021-09-09 13:32:48', 200, '', 0),
       (57, 1, 2, '10.0.1.72:9999', 'httpJobHandler', '', NULL, 0, '2021-09-09 13:34:33', 200,
        '任务触发类型：手动触发<br>调度机器：10.0.1.72<br>执行器-注册方式：自动注册<br>执行器-地址列表：[10.0.1.72:9999]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：10.0.1.72:9999<br>code：200<br>msg：null',
        '2021-09-09 13:34:45', 200, '', 0),
       (58, 1, 2, '10.0.1.72:9999', 'httpJobHandler', '', NULL, 0, '2021-09-09 13:55:52', 200,
        '任务触发类型：手动触发<br>调度机器：10.0.1.72<br>执行器-注册方式：自动注册<br>执行器-地址列表：[10.0.1.72:9999]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：10.0.1.72:9999<br>code：200<br>msg：null',
        '2021-09-09 13:56:03', 200, '', 0),
       (59, 1, 2, '10.0.1.72:9999', 'httpJobHandler', '', NULL, 0, '2021-09-09 14:24:20', 200,
        '任务触发类型：手动触发<br>调度机器：10.0.1.72<br>执行器-注册方式：自动注册<br>执行器-地址列表：[10.0.1.72:9999]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：10.0.1.72:9999<br>code：200<br>msg：null',
        '2021-09-09 14:24:32', 200, '', 0),
       (60, 1, 2, '10.0.1.72:9999', 'httpJobHandler', '', NULL, 0, '2021-09-09 14:26:20', 200,
        '任务触发类型：手动触发<br>调度机器：10.0.1.72<br>执行器-注册方式：自动注册<br>执行器-地址列表：[10.0.1.72:9999]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：10.0.1.72:9999<br>code：200<br>msg：null',
        '2021-09-09 14:26:39', 200, '', 0),
       (61, 1, 2, '10.0.1.72:9999', 'httpJobHandler', '', NULL, 0, '2021-09-09 14:27:37', 200,
        '任务触发类型：手动触发<br>调度机器：10.0.1.72<br>执行器-注册方式：自动注册<br>执行器-地址列表：[10.0.1.72:9999]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：10.0.1.72:9999<br>code：200<br>msg：null',
        '2021-09-09 14:27:48', 200, '', 0);
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_TRIGGER_LOG`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_TRIGGER_LOGGLUE`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_TRIGGER_LOGGLUE`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_TRIGGER_LOGGLUE`
(
    `id`          int(11)      NOT NULL AUTO_INCREMENT,
    `job_id`      int(11)      NOT NULL COMMENT '任务，主键ID',
    `glue_type`   varchar(50)       DEFAULT NULL COMMENT 'GLUE类型',
    `glue_source` mediumtext COMMENT 'GLUE源代码',
    `glue_remark` varchar(128) NOT NULL COMMENT 'GLUE备注',
    `add_time`    timestamp    NULL DEFAULT NULL,
    `update_time` timestamp    NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_TRIGGER_LOGGLUE`
--

LOCK TABLES `XXL_JOB_QRTZ_TRIGGER_LOGGLUE` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_TRIGGER_LOGGLUE`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_TRIGGER_LOGGLUE`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_TRIGGER_REGISTRY`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_TRIGGER_REGISTRY`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_TRIGGER_REGISTRY`
(
    `id`             int(11)      NOT NULL AUTO_INCREMENT,
    `registry_group` varchar(255) NOT NULL,
    `registry_key`   varchar(255) NOT NULL,
    `registry_value` varchar(255) NOT NULL,
    `update_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_TRIGGER_REGISTRY`
--

LOCK TABLES `XXL_JOB_QRTZ_TRIGGER_REGISTRY` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_TRIGGER_REGISTRY`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_TRIGGER_REGISTRY`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XXL_JOB_QRTZ_TRIGGERS`
--

DROP TABLE IF EXISTS `XXL_JOB_QRTZ_TRIGGERS`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XXL_JOB_QRTZ_TRIGGERS`
(
    `SCHED_NAME`     varchar(120) NOT NULL,
    `TRIGGER_NAME`   varchar(200) NOT NULL,
    `TRIGGER_GROUP`  varchar(200) NOT NULL,
    `JOB_NAME`       varchar(200) NOT NULL,
    `JOB_GROUP`      varchar(200) NOT NULL,
    `DESCRIPTION`    varchar(250) DEFAULT NULL,
    `NEXT_FIRE_TIME` bigint(13)   DEFAULT NULL,
    `PREV_FIRE_TIME` bigint(13)   DEFAULT NULL,
    `PRIORITY`       int(11)      DEFAULT NULL,
    `TRIGGER_STATE`  varchar(16)  NOT NULL,
    `TRIGGER_TYPE`   varchar(8)   NOT NULL,
    `START_TIME`     bigint(13)   NOT NULL,
    `END_TIME`       bigint(13)   DEFAULT NULL,
    `CALENDAR_NAME`  varchar(200) DEFAULT NULL,
    `MISFIRE_INSTR`  smallint(2)  DEFAULT NULL,
    `JOB_DATA`       blob,
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    KEY `SCHED_NAME` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`),
    CONSTRAINT `XXL_JOB_QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `XXL_JOB_QRTZ_JOB_DETAILS` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XXL_JOB_QRTZ_TRIGGERS`
--

LOCK TABLES `XXL_JOB_QRTZ_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_TRIGGERS`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `XXL_JOB_QRTZ_TRIGGERS`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2021-09-09 18:45:04
