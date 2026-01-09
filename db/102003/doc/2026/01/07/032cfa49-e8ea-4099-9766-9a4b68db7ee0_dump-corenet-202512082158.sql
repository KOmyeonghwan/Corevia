/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-11.8.3-MariaDB, for osx10.20 (arm64)
--
-- Host: localhost    Database: corenet
-- ------------------------------------------------------
-- Server version	11.8.3-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `board_community`
--

DROP TABLE IF EXISTS `board_community`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `board_community` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `file_url` varchar(2000) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `board_code` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `user_name` varchar(30) DEFAULT NULL,
  `create_at` datetime DEFAULT current_timestamp(),
  `views` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `board_community_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `board_community`
--

LOCK TABLES `board_community` WRITE;
/*!40000 ALTER TABLE `board_community` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `board_community` VALUES
(1,'test','test','/db/community/board/c3cceb62-8f10-439f-9647-fd9e321c42c8.sql','commumity','community',22,'삼길동','2025-11-27 21:18:15',0),
(2,'자유','자유',NULL,NULL,'community',21,'이길동','2025-11-28 22:40:16',0);
/*!40000 ALTER TABLE `board_community` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `board_doc`
--

DROP TABLE IF EXISTS `board_doc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `board_doc` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `file_url` varchar(2000) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `board_code` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `user_name` varchar(30) DEFAULT NULL,
  `create_at` datetime DEFAULT current_timestamp(),
  `views` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `board_doc_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `board_doc`
--

LOCK TABLES `board_doc` WRITE;
/*!40000 ALTER TABLE `board_doc` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `board_doc` VALUES
(1,'문서','문서','/db/doc/board/77bd8f7e-485d-47a4-87af-7f386298d710.sql','corenet_draft.sql','doc',21,'이길동','2025-12-06 17:01:43',0);
/*!40000 ALTER TABLE `board_doc` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `board_manager`
--

DROP TABLE IF EXISTS `board_manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `board_manager` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `board_code` varchar(255) NOT NULL,
  `board_name` varchar(255) NOT NULL,
  `dept_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `board_manager`
--

LOCK TABLES `board_manager` WRITE;
/*!40000 ALTER TABLE `board_manager` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `board_manager` VALUES
(1,'notice','공지사항','1'),
(2,'community','자유게시판','1'),
(3,'doc','문서','1');
/*!40000 ALTER TABLE `board_manager` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `board_notice`
--

DROP TABLE IF EXISTS `board_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `board_notice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `file_url` varchar(2000) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `board_code` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `user_name` varchar(30) DEFAULT NULL,
  `create_at` datetime DEFAULT current_timestamp(),
  `views` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `board_notice_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `board_notice`
--

LOCK TABLES `board_notice` WRITE;
/*!40000 ALTER TABLE `board_notice` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `board_notice` VALUES
(1,'test','test','/db/notice/board/c6300891-e7c4-4952-a800-ba222f4a0b08.sql','test\n','notice',22,'삼길동','2025-11-27 21:17:56',0),
(2,'test22','test22','/db/notice/board/c1c0d60b-c544-4628-afab-e664b6187ba0.sql','test\n','notice',22,'삼길동','2025-11-28 21:32:58',0),
(3,'추가','추가',NULL,NULL,'notice',21,'이길동','2025-11-28 22:25:48',0);
/*!40000 ALTER TABLE `board_notice` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `chat_room_participants`
--

DROP TABLE IF EXISTS `chat_room_participants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_room_participants` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `room_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `joined_at` datetime DEFAULT current_timestamp(),
  `last_read_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `room_id` (`room_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_chat_room_participants_room` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`),
  CONSTRAINT `fk_chat_room_participants_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_room_participants`
--

LOCK TABLES `chat_room_participants` WRITE;
/*!40000 ALTER TABLE `chat_room_participants` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `chat_room_participants` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `chat_rooms`
--

DROP TABLE IF EXISTS `chat_rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_rooms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `is_group` tinyint(1) DEFAULT 0,
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_rooms`
--

LOCK TABLES `chat_rooms` WRITE;
/*!40000 ALTER TABLE `chat_rooms` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `chat_rooms` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `comment_community`
--

DROP TABLE IF EXISTS `comment_community`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_community` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `post_id` bigint(20) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `user_name` varchar(30) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `depth` int(11) DEFAULT 0,
  `create_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `comment_community_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `board_community` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comment_community_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_community`
--

LOCK TABLES `comment_community` WRITE;
/*!40000 ALTER TABLE `comment_community` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `comment_community` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `comment_doc`
--

DROP TABLE IF EXISTS `comment_doc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_doc` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `post_id` bigint(20) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `user_name` varchar(30) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `depth` int(11) DEFAULT 0,
  `create_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `comment_doc_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `board_doc` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comment_doc_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_doc`
--

LOCK TABLES `comment_doc` WRITE;
/*!40000 ALTER TABLE `comment_doc` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `comment_doc` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `comment_notice`
--

DROP TABLE IF EXISTS `comment_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_notice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `post_id` bigint(20) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `user_name` varchar(30) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `depth` int(11) DEFAULT 0,
  `create_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `comment_notice_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `board_notice` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comment_notice_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_notice`
--

LOCK TABLES `comment_notice` WRITE;
/*!40000 ALTER TABLE `comment_notice` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `comment_notice` VALUES
(1,1,1,'이길동','ㅏㅏ',NULL,0,'2025-12-08 11:20:14'),
(2,2,2,'이길동','ㅑㅑ',NULL,0,'2025-12-08 11:20:57'),
(3,1,1,'이길동','테스트',NULL,0,'2025-12-08 11:37:52'),
(4,1,1,'이길동','ㅇㄹㅁ',1,1,'2025-12-08 11:42:02'),
(5,1,1,'이길동','ㄴㄹㅇ',4,2,'2025-12-08 11:42:07'),
(6,1,1,'이길동','ㄴㅇㄹ',1,1,'2025-12-08 11:42:17'),
(7,1,1,'이길동','sdf',NULL,0,'2025-12-08 11:54:08'),
(8,1,1,'이길동','fsa',1,1,'2025-12-08 11:54:22'),
(9,1,1,'이길동','ㅇㅎ',4,2,'2025-12-08 11:58:16'),
(10,1,1,'이길동','ㅇㅎㄹ',4,2,'2025-12-08 11:58:30'),
(11,1,1,'이길동','ㅇㅎ',1,1,'2025-12-08 11:58:36'),
(12,1,1,'이길동','ㅊㄴ',3,1,'2025-12-08 11:58:47'),
(13,1,1,'이길동','ㄴㅁㄹㅇ',12,2,'2025-12-08 11:58:57'),
(14,1,1,'이길동','ㄴㄹㅇ',3,1,'2025-12-08 11:59:00'),
(15,1,1,'이길동','ㅁㄴㄹ',14,2,'2025-12-08 11:59:05'),
(16,1,1,'삼길동','sfd',NULL,0,'2025-12-08 11:59:43'),
(17,1,1,'삼길동','asfd',7,1,'2025-12-08 11:59:47'),
(18,1,1,'삼길동','asdf',17,2,'2025-12-08 11:59:49'),
(19,1,1,'삼길동','sadf',7,1,'2025-12-08 11:59:52');
/*!40000 ALTER TABLE `comment_notice` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `departments`
--

DROP TABLE IF EXISTS `departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `departments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `department_name` varchar(40) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departments`
--

LOCK TABLES `departments` WRITE;
/*!40000 ALTER TABLE `departments` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `departments` VALUES
(1,'예외','2025-10-02 12:54:25','2025-10-02 12:54:25'),
(101,'개발부','2025-10-02 12:54:25','2025-11-12 18:28:29'),
(102,'인사','2025-10-02 12:54:25','2025-10-02 12:54:25'),
(103,'마케팅','2025-10-02 12:54:25','2025-10-02 12:54:25'),
(104,'기획','2025-10-02 12:54:25','2025-10-02 12:54:25'),
(108,'해외부','2025-10-15 13:32:41','2025-10-15 13:32:41');
/*!40000 ALTER TABLE `departments` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `mails`
--

DROP TABLE IF EXISTS `mails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `mails` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sender_email` varchar(100) NOT NULL,
  `sender_name` varchar(50) DEFAULT NULL,
  `recipient_email` varchar(100) NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `attachment_path` varchar(1000) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mails`
--

LOCK TABLES `mails` WRITE;
/*!40000 ALTER TABLE `mails` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `mails` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `room_id` int(11) NOT NULL,
  `sender_id` int(11) NOT NULL,
  `message` text DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `room_id` (`room_id`),
  KEY `sender_id` (`sender_id`),
  CONSTRAINT `FKksncokrajreiaqmmd4j3tq017` FOREIGN KEY (`room_id`) REFERENCES `chat_room_participants` (`id`),
  CONSTRAINT `fk_messages_room` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`),
  CONSTRAINT `fk_messages_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `sender_id` int(11) DEFAULT NULL,
  `type` enum('mail','message','schedule','system') NOT NULL,
  `reference_id` int(11) DEFAULT NULL,
  `title` varchar(100) NOT NULL,
  `content` text DEFAULT NULL,
  `is_read` tinyint(1) DEFAULT 0,
  `created_at` datetime DEFAULT current_timestamp(),
  `read_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_notifications_user` (`user_id`),
  KEY `fk_notifications_sender` (`sender_id`),
  CONSTRAINT `fk_notifications_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `positions`
--

DROP TABLE IF EXISTS `positions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `positions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `position_title` varchar(25) NOT NULL,
  `level` int(11) DEFAULT 0,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `positions`
--

LOCK TABLES `positions` WRITE;
/*!40000 ALTER TABLE `positions` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `positions` VALUES
(1,'대표',0,'2025-10-02 12:54:20','2025-10-02 12:54:20'),
(2,'부장',1,'2025-10-02 12:54:20','2025-10-02 12:54:20'),
(3,'과장',2,'2025-10-02 12:54:20','2025-10-02 12:54:20'),
(4,'대리',3,'2025-10-02 12:54:20','2025-10-02 12:54:20'),
(5,'사원',4,'2025-10-02 12:54:20','2025-10-02 12:54:20'),
(6,'시스템관리자',10,'2025-10-02 12:54:20','2025-10-02 12:54:20'),
(7,'외부시스템관리자',11,'2025-10-02 12:54:20','2025-10-02 12:54:20');
/*!40000 ALTER TABLE `positions` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `schedules`
--

DROP TABLE IF EXISTS `schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedules` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `description` text DEFAULT NULL,
  `start_datetime` datetime DEFAULT NULL,
  `end_datetime` datetime DEFAULT NULL,
  `is_admin_view` tinyint(1) DEFAULT 0,
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_schedules_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedules`
--

LOCK TABLES `schedules` WRITE;
/*!40000 ALTER TABLE `schedules` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `schedules` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `security_logs`
--

DROP TABLE IF EXISTS `security_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `security_logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `event_type` enum('login_success','login_failure','password_change','access_denied','role_change','system_error','external_ip_login') NOT NULL,
  `event_description` text DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `page_url` varchar(500) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `fk_security_logs_user` (`user_id`),
  CONSTRAINT `fk_security_logs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `security_logs`
--

LOCK TABLES `security_logs` WRITE;
/*!40000 ALTER TABLE `security_logs` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `security_logs` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `Jobcode` int(11) DEFAULT NULL,
  `user_id` varchar(50) NOT NULL,
  `user_name` varchar(50) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role` int(11) DEFAULT NULL,
  `position_id` int(11) DEFAULT NULL,
  `department_id` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `company_email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `position_id` (`position_id`),
  KEY `department_id` (`department_id`),
  CONSTRAINT `fk_users_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`),
  CONSTRAINT `fk_users_position` FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `users` VALUES
(1,'ceo@example.com',10001,'ceo','대표이사','010-1111-1111','$2a$10$v88CcTQB2naikzYfVH0VdejaY1MK85WUoUDnkx2oAEGE7fVyXYdmy',0,1,1,'2025-10-02 13:20:32','2025-10-16 21:54:24','10001@company.com'),
(2,'admin@example.com',10002,'admin','시스템관리자','010-2222-2222','$2a$10$uO7b/7hXy5lIhDkZ7QrE.u1j5UBlWgFh/a7pQ0j8Y2bE9x4Zl3QXy',0,6,1,'2025-10-02 13:21:16','2025-10-16 22:11:43','10002@company.com'),
(3,'externalAdmin@gmail.com',10003,'ExternalITAdministrator','외부시스템관리자','01012349999','$2a$10$pewaiAShB3G6bhjnV.KNxu08jm6gS0QCDBF4O0HDVozyBPMKhzH72',0,7,1,'2025-10-16 22:00:11','2025-10-16 22:10:53','10003@corenet.com'),
(4,'hr@gmail.com',102002,'HRTeamLeader','인사부장','01022223333','$2a$10$rV5QrDXElO5xaTE8eBe9je7vwXMnLNNTThHhL35PR4xnsVEwwUuvS',2,2,102,'2025-10-15 00:41:21','2025-10-16 22:24:23','102002@company.com'),
(5,'marketing@gmail.com',103001,'MarketingTeamLeader','마케팅부장','01012344321','$2a$10$P01XgZcttP6Zw/ExYXBduuNr1MZa3A4AV96JP8HhlYUaGrhzgqNxi',2,2,103,'2025-10-16 13:50:39','2025-10-16 22:24:23','103001@company.com'),
(6,'planning@gmail.com',104001,'PlanningTeamLeader','기획부장','01022332222','$2a$10$4fMGxkjAz/FKqZ0lNN9o.uNLMJczt1D/hdQxrlWOPyQVNBS1S2Pge',2,2,104,'2025-10-16 17:43:41','2025-10-16 22:24:23','104001@corenet.com'),
(7,'www@gmail.com',101004,'devTeamLeader','개발부장','01022223333','$2a$10$QH7UXV2apSV1HMvLG9b8eOhgjMhnCOEGwae8cUa6s9jWG04WxPA1y',2,2,101,'2025-10-13 11:42:47','2025-10-16 22:24:23','101004@company.com'),
(10,'devManager@gmail.com',101005,'devManager','개발과장','01022223334','$2a$10$GrEQe/QOwU/D0zSHzKZm8u2jKIerRd3RF23FbsoSMPfo.d4LSSrUC',2,3,101,'2025-10-16 22:17:59','2025-11-12 18:28:09','101005@corenet.com'),
(11,'HRManager@gmail.com',102004,'HRManager','인사과장','01022221111','$2a$10$rVLsB51hFffbyKNJSzBGOegxzwUk5JGBEnJLAp2VVachhS0POV/su',2,3,102,'2025-10-16 22:18:46','2025-10-16 22:23:21','102004@corenet.com'),
(12,'marketingManager@gmail.com',103003,'MarketingManager','마케팅과장','01011112222','$2a$10$CiIYbzy6FpB4IJK9cVPzgOOOUYzC.In1KaEc/BxNvVTZr1btRHoEq',2,3,103,'2025-10-16 22:19:44','2025-10-16 22:23:21','103003@corenet.com'),
(13,'PlanningManager@gmail.com',104003,'PlanningManager','기획과장','01033338888','$2a$10$fjSKzW.BLUV1QVTP62LW9eVFKY2cstU1xC.1wIpOIXEMNqTheIaCS',2,3,104,'2025-10-16 22:20:26','2025-10-16 22:23:21','104003@corenet.com'),
(14,'devAssistantManager@gmail.com',101006,'devAssistantManager','개발대리','0102222322','$2a$10$ySY8UGS8y0dEUNanIn7fTey8IaHkoLaD.iHUQU8ry7cf2rEVcKVo6',2,4,101,'2025-10-16 22:25:43','2025-10-16 22:29:44','101006@corenet.com'),
(15,'HRAssistantManager@gmail.com',102005,'HRAssistantManager','인사대리','01012346633','$2a$10$RJ.89tnC1UMM6Bvtb2k4/.rRpgCgT4FWOBpYqrnWDt.7J/ryVb4v.',2,4,102,'2025-10-16 22:26:34','2025-10-16 22:29:44','102005@corenet.com'),
(16,'MarketingAssistantManager@gmail.com',103004,'MarketingAssistantManager','마케팅대리','01088889999','$2a$10$qTZWTjvlytIiKxzOOjygsu0JQAQNgnYlwIhUywE5q1lD86HJmsjOy',2,4,103,'2025-10-16 22:27:34','2025-10-16 22:29:44','103004@corenet.com'),
(17,'PlanningAssistantManager@gmail.com',104004,'PlanningAssistantManager','기획대리','01088889900','$2a$10$xfFsIedB/xOHuUW4SU8DAuY57zTHfLaQJg9ebs8q6O04KRGyOrc72',2,4,104,'2025-10-16 22:28:26','2025-10-16 22:29:44','104004@corenet.com'),
(20,'aaaa@naver.com',101001,'yangjung','홍길동','01066669999','$2a$10$/1UU9vLrot.FU28weUXYbOGEP1h4L.6Rt0jB/A8iXxBj49VsWvfX.',2,5,101,'2025-10-02 15:01:30','2025-10-16 22:23:21','101001@company.com'),
(21,'ccc@gmail.com',102003,'yangjung1','이길동','01022223333','$2a$10$aEFcwk9gNhyd5NrmNSF8kuoUXIbJhgsgvvSYdokIDqLHIiBPu0NWS',2,5,102,'2025-10-16 22:12:47','2025-10-16 22:23:21','102003@corenet.com'),
(22,'bbb@gmail.com',103002,'yangjung2','삼길동','01022223344','$2a$10$rY4Ll.EmjGWGPA6GcSGadefmgySEV8I3WEuDA48w89Lrdzt6GnUSS',2,5,103,'2025-10-16 22:13:15','2025-10-16 22:23:21','103002@corenet.com'),
(23,'ddd@gmail.com',104002,'yangjung3','사길동','01023452134','$2a$10$2kzLDCzj.Ud1ZCXzkHk/beUGSmiWpRO5uQ5p9sPkXY.n4EeonQy2y',2,5,104,'2025-10-16 22:14:24','2025-10-16 22:23:21','104002@corenet.com'),
(24,'uuu@gmail.com',108001,'yangjung8','육길동','010-1234-5678','$2a$10$B9HyAjDhrPnA7cueUBpS.uLKaDbhKBTe33yXBXU9NE623B.hPr1c6',2,5,108,'2025-10-16 22:32:17','2025-10-16 22:32:17','108001@corenet.com'),
(25,'yang@gmail.com',102006,'yang','유효성','01012341111','$2a$10$PrTYVcBwaBmeSDr8LCTTu.khoWHFP9WIvo2rUmoqdBAmNU8Ej/H.e',2,5,102,'2025-11-16 22:01:45','2025-11-16 22:01:45','102006@corenet.com');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Dumping routines for database 'corenet'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2025-12-08 21:58:04
