-- MySQL dump 10.13  Distrib 5.6.30, for Win32 (AMD64)
--
-- Host: localhost    Database: parkinglot
-- ------------------------------------------------------
-- Server version	5.6.30

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `parkinglot`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `parkinglot` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `parkinglot`;

--
-- Table structure for table `building_table`
--

DROP TABLE IF EXISTS `building_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building_table` (
  `SEQ_NO` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `BLDG_NO` int(10) unsigned NOT NULL COMMENT 'A number assigned to a building',
  PRIMARY KEY (`SEQ_NO`),
  UNIQUE KEY `BLDG_NO` (`BLDG_NO`)
) ENGINE=InnoDB AUTO_INCREMENT=198 DEFAULT CHARSET=utf8 COMMENT='building number in an apartment complex';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building_table`
--

LOCK TABLES `building_table` WRITE;
/*!40000 ALTER TABLE `building_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `building_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building_unit`
--

DROP TABLE IF EXISTS `building_unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building_unit` (
  `SEQ_NO` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `UNIT_NO` int(10) unsigned NOT NULL COMMENT 'Apartment Unit number in a building',
  `BLDG_SEQ_NO` int(10) unsigned NOT NULL COMMENT 'Key of the building which this unit(room) belongs to',
  PRIMARY KEY (`SEQ_NO`),
  UNIQUE KEY `BLDG_SEQ_NO` (`BLDG_SEQ_NO`,`UNIT_NO`),
  CONSTRAINT `unit_in_a_building` FOREIGN KEY (`BLDG_SEQ_NO`) REFERENCES `building_table` (`SEQ_NO`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5825 DEFAULT CHARSET=utf8 COMMENT='One of the room numbers in a building';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building_unit`
--

LOCK TABLES `building_unit` WRITE;
/*!40000 ALTER TABLE `building_unit` DISABLE KEYS */;
/*!40000 ALTER TABLE `building_unit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `car_arrival`
--

DROP TABLE IF EXISTS `car_arrival`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `car_arrival` (
  `ArrSeqNo` bigint(15) unsigned NOT NULL AUTO_INCREMENT COMMENT 'arrival order number',
  `GateNo` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'Enterance Gate Number',
  `ArrivalTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Vehicle arrival date and time(second unit)',
  `TagRecognized` varchar(15) DEFAULT NULL COMMENT 'Recognized Car tag number',
  `TagEnteredAs` varchar(15) DEFAULT NULL,
  `ImageBlob` mediumblob COMMENT 'Image File Itself',
  `AttendantID` char(20) DEFAULT NULL COMMENT 'ID of the attendant at the time of entry',
  `unitSeqNo` int(10) unsigned DEFAULT NULL COMMENT 'Unit number where the visitor is heading for',
  `L2_No` smallint(5) unsigned DEFAULT NULL COMMENT 'Level 2 affiliation key value',
  `VisitReason` varchar(60) DEFAULT NULL COMMENT 'Why the visitor is entering',
  `BarOperation` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'Description of the bar operation',
  PRIMARY KEY (`ArrSeqNo`),
  KEY `valid_L2_affili_no` (`L2_No`),
  KEY `valid_unit_seq_no` (`unitSeqNo`),
  KEY `valid_enteredAs_tag` (`TagEnteredAs`),
  KEY `true_attendant` (`AttendantID`),
  CONSTRAINT `once_attendant` FOREIGN KEY (`AttendantID`) REFERENCES `users_osp` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2248 DEFAULT CHARSET=utf8 COMMENT='records vehicle arrival at a gate ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car_arrival`
--

LOCK TABLES `car_arrival` WRITE;
/*!40000 ALTER TABLE `car_arrival` DISABLE KEYS */;
/*!40000 ALTER TABLE `car_arrival` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cardriver`
--

DROP TABLE IF EXISTS `cardriver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cardriver` (
  `SEQ_NO` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `NAME` varchar(60) NOT NULL,
  `CELLPHONE` char(15) CHARACTER SET latin1 NOT NULL,
  `PHONE` char(15) DEFAULT NULL,
  `L2_NO` smallint(5) unsigned DEFAULT NULL COMMENT 'Level 2 Affiliation Key',
  `UNIT_SEQ_NO` int(10) unsigned DEFAULT NULL COMMENT 'RESIDENCE ROOM NO KEY',
  `CREATIONDATE` date NOT NULL COMMENT 'When the record was created',
  `LASTMODIDATE` date DEFAULT NULL COMMENT 'When the record was modified last',
  PRIMARY KEY (`SEQ_NO`),
  UNIQUE KEY `unique_driver` (`NAME`,`CELLPHONE`),
  KEY `EXISTING_UNIT_KEY` (`UNIT_SEQ_NO`),
  KEY `idx_on_driver_name` (`NAME`),
  KEY `idx_on_building_unit_key` (`UNIT_SEQ_NO`),
  KEY `idx_on_affiliation` (`L2_NO`),
  CONSTRAINT `EXISTING_L2_AFFILI_KEY` FOREIGN KEY (`L2_NO`) REFERENCES `l2_affiliation` (`L2_NO`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `EXISTING_UNIT_KEY` FOREIGN KEY (`UNIT_SEQ_NO`) REFERENCES `building_unit` (`SEQ_NO`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2022 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cardriver`
--

LOCK TABLES `cardriver` WRITE;
/*!40000 ALTER TABLE `cardriver` DISABLE KEYS */;
/*!40000 ALTER TABLE `cardriver` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eboard_lednotice`
--

DROP TABLE IF EXISTS `eboard_lednotice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eboard_lednotice` (
  `usage_row` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'default top(1), bottom(2), car top(3), bottom(4)',
  `row_used` tinyint(4) NOT NULL DEFAULT '1' COMMENT '사용 여부 기록(0: 비 사용, 1: 사용)',
  `verbatim_content` varchar(200) NOT NULL COMMENT 'textual infomation to be displayed exactly',
  `display_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '문구 자체(0), 등 (콤보 박스 인덱스)',
  `start_effect` tinyint(1) NOT NULL DEFAULT '0' COMMENT '왼쪽흐름(0), ...',
  `text_color` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'RED(0), Green(1), Orange(2)',
  `text_font` tinyint(1) NOT NULL DEFAULT '0' COMMENT '명조(0), 고딕(1)',
  `pause_time` tinyint(1) NOT NULL DEFAULT '0' COMMENT '중간멈춤시간 콤보박스 인덱스: 0(1초), 1(2), ... 9(10)',
  `finish_effect` tinyint(1) NOT NULL DEFAULT '0' COMMENT '표시 후반부 적용 효과: 효과없음(0), 좌로 흐름(1), ..',
  PRIMARY KEY (`usage_row`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eboard_lednotice`
--

LOCK TABLES `eboard_lednotice` WRITE;
/*!40000 ALTER TABLE `eboard_lednotice` DISABLE KEYS */;
INSERT INTO `eboard_lednotice` VALUES (0,0,'',5,11,1,0,0,0),(1,0,'어서오십시오',0,0,0,0,2,6),(2,1,'',0,4,0,1,2,0),(3,0,'',1,0,2,1,0,0);
/*!40000 ALTER TABLE `eboard_lednotice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eboard_settings`
--

DROP TABLE IF EXISTS `eboard_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eboard_settings` (
  `usage_row` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'default top(1), bottom(2), car top(3), bottom(4)',
  `verbatim_content` varchar(200) DEFAULT NULL COMMENT 'textual infomation to be displayed exactly',
  `content_type` tinyint(1) DEFAULT '0' COMMENT 'verbatim(0), vehicle tag(1), regi-stat-(2), ...',
  `display_pattern` tinyint(1) DEFAULT NULL COMMENT 'RtoL flow(0), LtoR flow(1), Stop(2), Blink(3)',
  `text_color` tinyint(1) DEFAULT NULL COMMENT 'RED, Orange, Green, Black, Blue : 0~4',
  `text_font` tinyint(1) DEFAULT NULL COMMENT 'dialog(0), ..., sans_serif(4)',
  PRIMARY KEY (`usage_row`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eboard_settings`
--

LOCK TABLES `eboard_settings` WRITE;
/*!40000 ALTER TABLE `eboard_settings` DISABLE KEYS */;
INSERT INTO `eboard_settings` VALUES (1,'어디론가 떠나요',0,0,0,1),(2,'',3,3,0,0),(3,'',1,0,0,0),(4,'',5,2,2,1);
/*!40000 ALTER TABLE `eboard_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gatedevices`
--

DROP TABLE IF EXISTS `gatedevices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gatedevices` (
  `GateID` tinyint(4) NOT NULL COMMENT 'ID number of a gate starts from 1',
  `gatename` varchar(50) DEFAULT NULL COMMENT 'title/name assigned to a gate',
  `cameraIP` char(15) CHARACTER SET latin1 DEFAULT NULL COMMENT 'IP address assigned to a camera',
  `cameraPort` char(5) CHARACTER SET latin1 DEFAULT '8080' COMMENT '4 digit port number',
  `cameraType` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'simulator(0), camera brand(1+)',
  `e_boardIP` char(15) CHARACTER SET latin1 DEFAULT NULL COMMENT 'IP address of Electronic Board',
  `e_boardPort` char(5) CHARACTER SET latin1 DEFAULT '8080',
  `e_boardType` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'sim(0), others(1+)- CBox index',
  `e_boardConnType` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'connection type: socket(0), RS-232(1)',
  `e_boardCOM_ID` char(1) CHARACTER SET latin1 NOT NULL DEFAULT '3' COMMENT 'COM port ID number: 1, 2, ... 8',
  `gatebarIP` char(15) CHARACTER SET latin1 DEFAULT NULL COMMENT 'gate bar IP address',
  `gatebarPort` char(5) CHARACTER SET latin1 DEFAULT '8080',
  `gateBarType` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'simulator(0), gate bar brand(1+)',
  `gateBarConnType` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'connection type: socket(0), RS-232(1)',
  `gatebarCOM_ID` char(1) CHARACTER SET latin1 NOT NULL DEFAULT '3' COMMENT 'COM port ID number: 1, 2, ... 8',
  `passingCountCurrent` int(11) DEFAULT NULL COMMENT 'number of cars passed thru this gate',
  `passingDelayCurrentTotalMs` int(11) DEFAULT NULL COMMENT 'accumulated passing delay time in milliseconds',
  `passingDelayPreviousAverageMs` float DEFAULT NULL COMMENT 'average passing delay time in ms(for the last some number of cars)',
  `passingDelayPreviousPopulation` int(11) DEFAULT NULL COMMENT 'Size of population used for PreviousAverageMs',
  `passingDelayCalculationTime` datetime DEFAULT NULL COMMENT 'time point when previousAverageMs calculated',
  PRIMARY KEY (`GateID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gatedevices`
--

LOCK TABLES `gatedevices` WRITE;
/*!40000 ALTER TABLE `gatedevices` DISABLE KEYS */;
INSERT INTO `gatedevices` VALUES (1,'남1문','127.0.0.1','8081',0,'127.0.0.1','55',0,1,'1','127.0.0.1','8082',0,1,'4',2,58,29.8,10,'2016-08-25 00:11:59'),(2,'강변후문','127.0.0.1','8083',0,'127.0.0.1','8084',0,1,'2','127.0.0.1','8085',0,1,'3',0,0,29.11,100,'2016-08-12 17:53:10'),(3,'(anonymous)','127.0.0.1','8080',0,'127.0.0.1','8080',0,1,'2','127.0.0.1','8080',0,1,'2',0,0,0,NULL,NULL),(4,'(anonymous)','127.0.0.1','8080',0,'127.0.0.1','8080',0,0,'','127.0.0.1','8080',0,0,'',0,0,0,NULL,NULL);
/*!40000 ALTER TABLE `gatedevices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `l1_affiliation`
--

DROP TABLE IF EXISTS `l1_affiliation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `l1_affiliation` (
  `L1_NO` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `PARTY_NAME` varchar(60) NOT NULL,
  PRIMARY KEY (`L1_NO`),
  UNIQUE KEY `PARTY_NAME` (`PARTY_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8 COMMENT='Higher Level Affiliation Names';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `l1_affiliation`
--

LOCK TABLES `l1_affiliation` WRITE;
/*!40000 ALTER TABLE `l1_affiliation` DISABLE KEYS */;
/*!40000 ALTER TABLE `l1_affiliation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `l2_affiliation`
--

DROP TABLE IF EXISTS `l2_affiliation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `l2_affiliation` (
  `L2_NO` smallint(5) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Lower Affiliation ID number',
  `L1_NO` smallint(5) unsigned NOT NULL COMMENT 'Higher Affiliation ID number',
  `PARTY_NAME` varchar(60) NOT NULL COMMENT 'Lower Affiliation English Name',
  PRIMARY KEY (`L2_NO`),
  UNIQUE KEY `L2_Name_Per_L1_Name` (`PARTY_NAME`,`L1_NO`),
  KEY `L2_AFF_NAME_IDX` (`PARTY_NAME`),
  KEY `good_Level1` (`L1_NO`),
  CONSTRAINT `good_Level1` FOREIGN KEY (`L1_NO`) REFERENCES `l1_affiliation` (`L1_NO`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8 COMMENT='Lower Level Affiliation Names';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `l2_affiliation`
--

LOCK TABLES `l2_affiliation` WRITE;
/*!40000 ALTER TABLE `l2_affiliation` DISABLE KEYS */;
/*!40000 ALTER TABLE `l2_affiliation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loginrecord`
--

DROP TABLE IF EXISTS `loginrecord`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `loginrecord` (
  `recNO` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userID` char(20) NOT NULL,
  `loginTS` timestamp NULL DEFAULT NULL,
  `logoutTS` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`recNO`),
  KEY `realAttendant` (`userID`),
  CONSTRAINT `once_attendant2` FOREIGN KEY (`userID`) REFERENCES `users_osp` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=238 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loginrecord`
--

LOCK TABLES `loginrecord` WRITE;
/*!40000 ALTER TABLE `loginrecord` DISABLE KEYS */;
/*!40000 ALTER TABLE `loginrecord` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `settingstable`
--

DROP TABLE IF EXISTS `settingstable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settingstable` (
  `Lot_Name` varchar(60) DEFAULT '주차장' COMMENT 'Parking Lot Name',
  `perfEvalNeeded` bit(1) DEFAULT b'0',
  `PWStrengthLevel` tinyint(4) DEFAULT '0',
  `OptnLoggingLevel` tinyint(4) DEFAULT '0',
  `languageCode` char(2) DEFAULT 'ko',
  `countryCode` char(2) DEFAULT 'KR',
  `localeIndex` smallint(6) DEFAULT NULL,
  `statCount` int(11) DEFAULT NULL,
  `MaxMessageLines` int(11) NOT NULL DEFAULT '100',
  `GateCount` smallint(6) DEFAULT NULL,
  `PictureWidth` smallint(6) DEFAULT NULL,
  `PictureHeight` smallint(6) DEFAULT NULL,
  `SearchPeriod` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `debug_seq` int(11) NOT NULL DEFAULT '0',
  `MAX_MAINTAIN_DATE` smallint(5) DEFAULT '90' COMMENT 'arrival record maintain date duration',
  `EBD_flow_cycle` int(11) DEFAULT '8000' COMMENT 'e-board message display cycle in mili-second',
  `EBD_blink_cycle` int(11) DEFAULT '500' COMMENT 'e-board message blink cycle in mili-seconds'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `settingstable`
--

LOCK TABLES `settingstable` WRITE;
/*!40000 ALTER TABLE `settingstable` DISABLE KEYS */;
INSERT INTO `settingstable` VALUES ('(주)오픈주차장','',1,1,'ko','KR',110,1,300,2,1280,960,2,3271307,120,8000,1000);
/*!40000 ALTER TABLE `settingstable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `systemrun`
--

DROP TABLE IF EXISTS `systemrun`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `systemrun` (
  `recNo` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `stopTm` timestamp NULL DEFAULT NULL,
  `startTm` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`recNo`)
) ENGINE=InnoDB AUTO_INCREMENT=896 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `systemrun`
--

LOCK TABLES `systemrun` WRITE;
/*!40000 ALTER TABLE `systemrun` DISABLE KEYS */;
/*!40000 ALTER TABLE `systemrun` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_osp`
--

DROP TABLE IF EXISTS `users_osp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users_osp` (
  `id` char(20) NOT NULL,
  `name` varchar(60) NOT NULL,
  `password` char(32) NOT NULL,
  `email` varchar(60) DEFAULT NULL,
  `isManager` tinyint(1) NOT NULL DEFAULT '0',
  `cellphone` char(15) DEFAULT NULL,
  `phone` char(15) DEFAULT NULL,
  `creationTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `lastModiTime` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_osp`
--

LOCK TABLES `users_osp` WRITE;
/*!40000 ALTER TABLE `users_osp` DISABLE KEYS */;
INSERT INTO `users_osp` VALUES ('admin','총괄자','81dc9bdb52d04dc20036dbd8313ed055','admin@osparking.com',1,'','02-858-9168','2016-08-11 11:53:14','2016-08-14 16:24:59'),('guest','김손님','81dc9bdb52d04dc20036dbd8313ed055',NULL,0,NULL,'02-858-9168','2016-08-25 08:23:25',NULL),('manager','운영자','81dc9bdb52d04dc20036dbd8313ed055',NULL,1,NULL,'02-858-9168','2016-08-25 08:23:25',NULL);
/*!40000 ALTER TABLE `users_osp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicles`
--

DROP TABLE IF EXISTS `vehicles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vehicles` (
  `PLATE_NUMBER` varchar(15) NOT NULL COMMENT 'Car tag number',
  `DRIVER_SEQ_NO` int(10) unsigned NOT NULL COMMENT 'Car Owner/Driver Information',
  `NOTI_REQUESTED` tinyint(1) unsigned DEFAULT '0' COMMENT 'If the Household Requested Car Entry Notification',
  `WHOLE_REQUIRED` tinyint(1) unsigned DEFAULT '0' COMMENT 'Whole number comparision required(1), not(0)',
  `OTHER_INFO` varchar(40) DEFAULT NULL COMMENT 'COLOR, EXTERIOR FEATURES, etc.',
  `PERMITTED` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT 'allowed(0), disallowed(1)',
  `CREATIONDATE` date NOT NULL COMMENT 'When the vehicle info is created',
  `LASTMODIDATE` date DEFAULT NULL COMMENT 'When the info modified last',
  `Remark` varchar(60) DEFAULT NULL COMMENT 'Why this can temporarily disallowed to park',
  PRIMARY KEY (`PLATE_NUMBER`),
  KEY `carDriverInfoRequired` (`DRIVER_SEQ_NO`),
  CONSTRAINT `carDriverInfoRequired` FOREIGN KEY (`DRIVER_SEQ_NO`) REFERENCES `cardriver` (`SEQ_NO`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Information on Registered Vehicles';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicles`
--

LOCK TABLES `vehicles` WRITE;
/*!40000 ALTER TABLE `vehicles` DISABLE KEYS */;
/*!40000 ALTER TABLE `vehicles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-08-25  9:26:56
