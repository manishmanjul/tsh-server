CREATE DATABASE  IF NOT EXISTS `tsh` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `tsh`;
-- MySQL dump 10.13  Distrib 8.0.20, for Win64 (x86_64)
--
-- Host: localhost    Database: tsh
-- ------------------------------------------------------
-- Server version	5.6.48-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `app_init`
--

DROP TABLE IF EXISTS `app_init`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `app_init` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `init_for` date DEFAULT NULL,
  `activity` varchar(45) DEFAULT NULL,
  `created_on` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `attendence`
--

DROP TABLE IF EXISTS `attendence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendence` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `student_batch_id` int(11) DEFAULT NULL,
  `teacher_id` int(11) DEFAULT NULL,
  `absence_date` datetime DEFAULT NULL,
  `half_day` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COMMENT='		';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `batch`
--

DROP TABLE IF EXISTS `batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timeslot_id` int(11) DEFAULT NULL,
  `batch_startdate` datetime DEFAULT NULL,
  `batch_enddate` datetime DEFAULT NULL,
  `strength` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=797 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `batch_details`
--

DROP TABLE IF EXISTS `batch_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `batch_id` int(11) DEFAULT NULL,
  `batch_name` varchar(45) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `teacher_id` int(11) DEFAULT NULL,
  `course_id` int(11) DEFAULT NULL,
  `grade_id` int(11) DEFAULT NULL,
  `performance` int(11) DEFAULT NULL,
  `training_type_id` int(11) DEFAULT NULL,
  `term_id` int(11) DEFAULT NULL,
  `active` tinyint(4) DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1218 DEFAULT CHARSET=utf8mb4 COMMENT='	';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `batch_progress`
--

DROP TABLE IF EXISTS `batch_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch_progress` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `batch_detail_id` int(11) DEFAULT NULL,
  `topic_id` int(11) DEFAULT NULL,
  `course_id` int(11) DEFAULT NULL,
  `startdate` datetime DEFAULT NULL,
  `enddate` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `planned_startdate` date DEFAULT NULL,
  `planned_enddate` datetime DEFAULT NULL,
  `print_booklet` tinyint(4) DEFAULT '0',
  `teacher_id` int(11) DEFAULT NULL,
  `planned_time` time DEFAULT NULL,
  `canceled` tinyint(4) DEFAULT NULL,
  `created_on` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=685 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(45) DEFAULT NULL,
  `short_description` varchar(45) DEFAULT NULL,
  `category` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `feature_type`
--

DROP TABLE IF EXISTS `feature_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feature_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type_name` varchar(255) DEFAULT NULL,
  `subtype` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1 COMMENT='			';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `features`
--

DROP TABLE IF EXISTS `features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `features` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `permission` varchar(10) DEFAULT NULL,
  `target` varchar(100) DEFAULT NULL,
  `style` varchar(255) DEFAULT NULL,
  `order` int(11) DEFAULT NULL,
  `feature_type` int(11) DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  `page` varchar(45) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `short_description` varchar(45) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `criteria` varchar(255) DEFAULT NULL,
  `active` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COMMENT='	';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `feedback_category`
--

DROP TABLE IF EXISTS `feedback_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `active` tinyint(4) DEFAULT NULL,
  `order` int(11) DEFAULT NULL,
  `grade` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grades`
--

DROP TABLE IF EXISTS `grades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grades` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `grade` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `import_item`
--

DROP TABLE IF EXISTS `import_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `import_item` (
  `name` varchar(255) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `subject` varchar(45) NOT NULL,
  `grade` varchar(45) NOT NULL,
  `teacher` varchar(45) DEFAULT NULL,
  `location` varchar(500) DEFAULT NULL,
  `batch_date` datetime DEFAULT NULL,
  `batch_start_time` time DEFAULT NULL,
  `batch_end_time` time DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `father_name` varchar(255) DEFAULT NULL,
  `mother_name` varchar(255) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `import_date` datetime DEFAULT NULL,
  `cycle` int(11) DEFAULT NULL,
  `message` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3492 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `process`
--

DROP TABLE IF EXISTS `process`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `process` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `startTime` datetime DEFAULT NULL,
  `endTime` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '1 - In Progress\\\\\\\\\\\\\\\\n2- On Hold\\\\\\\\\\\\\\\\n3- Stopped\\\\\\\\\\\\\\\\n4- Completed\\\\\\\\\\\\\\\\n5- Failed',
  `totalSteps` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `process_details`
--

DROP TABLE IF EXISTS `process_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `process_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `process_id` int(11) DEFAULT NULL,
  `step` int(11) DEFAULT NULL,
  `stepName` varchar(255) DEFAULT NULL,
  `startTime` datetime DEFAULT NULL,
  `endTime` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '1 - In Progress\\\\\\\\n2- On Hold\\\\\\\\n3- Stopped\\\\\\\\n4- Completed\\\\\\\\n5- Failed',
  `weight` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(45) DEFAULT NULL,
  `permission_string` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `student_name` varchar(255) DEFAULT NULL,
  `grade_id` int(11) DEFAULT NULL,
  `joining_date` datetime DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `father_name` varchar(255) DEFAULT NULL,
  `mother_name` varchar(255) DEFAULT NULL,
  `phone` varchar(10) CHARACTER SET utf8 DEFAULT NULL,
  `active` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1983 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `student_batches`
--

DROP TABLE IF EXISTS `student_batches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_batches` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `student_id` int(11) DEFAULT NULL,
  `batch_details_id` int(11) DEFAULT NULL,
  `course_id` int(11) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2792 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `student_feedback`
--

DROP TABLE IF EXISTS `student_feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_feedback` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `student_batches_id` int(11) DEFAULT NULL,
  `feedback_id` int(11) DEFAULT NULL,
  `feedback_text` varchar(255) DEFAULT NULL,
  `feedback_date` datetime DEFAULT NULL,
  `teacher_id` int(11) DEFAULT NULL,
  `topic_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `topic_progress_id` int(11) DEFAULT NULL,
  `update_user` int(11) DEFAULT NULL,
  `update_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=672 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `teacher`
--

DROP TABLE IF EXISTS `teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `teacher_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(10) CHARACTER SET utf8 DEFAULT NULL,
  `active` tinyint(4) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `teacher_details`
--

DROP TABLE IF EXISTS `teacher_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `teacher_id` int(11) DEFAULT NULL,
  `grade_id` int(11) DEFAULT NULL,
  `course_id` int(11) DEFAULT NULL,
  `bg_color` varchar(45) DEFAULT NULL,
  `font_color` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `term`
--

DROP TABLE IF EXISTS `term`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `term` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `term` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `startdate` datetime DEFAULT NULL,
  `endDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `timeslot`
--

DROP TABLE IF EXISTS `timeslot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `timeslot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `weekday` int(11) DEFAULT NULL,
  `starttime` time DEFAULT NULL,
  `endtime` time DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1271 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `topic_progress`
--

DROP TABLE IF EXISTS `topic_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `topic_progress` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `student_id` int(11) DEFAULT NULL,
  `course_id` int(11) DEFAULT NULL,
  `topic_id` int(11) DEFAULT NULL,
  `startdate` datetime DEFAULT NULL,
  `enddate` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `planned_startdate` datetime DEFAULT NULL,
  `planned_enddate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=342 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `topic_status`
--

DROP TABLE IF EXISTS `topic_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `topic_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `topics`
--

DROP TABLE IF EXISTS `topics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `topics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `chapter` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `topic_name` varchar(45) DEFAULT NULL,
  `term_id` int(11) DEFAULT NULL,
  `estimated_hours` float DEFAULT NULL,
  `grade_id` int(11) DEFAULT NULL,
  `complexity` int(11) DEFAULT NULL,
  `course_id` int(11) DEFAULT NULL,
  `mapped_Id` varchar(45) DEFAULT NULL,
  `week_id` int(11) DEFAULT NULL,
  `active` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4314 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `training_type`
--

DROP TABLE IF EXISTS `training_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `phone` varchar(10) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_login` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `week`
--

DROP TABLE IF EXISTS `week`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `week` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `week_number` int(11) DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=167 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-03-15  9:52:27
