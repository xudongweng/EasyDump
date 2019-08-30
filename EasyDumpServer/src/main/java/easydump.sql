/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 80012
Source Host           : localhost:3306
Source Database       : easydump

Target Server Type    : MYSQL
Target Server Version : 80012
File Encoding         : 65001

Date: 2019-08-30 17:01:04
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for backuptables
-- ----------------------------
DROP TABLE IF EXISTS `backuptables`;
CREATE TABLE `backuptables` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `databaseid` mediumint(4) unsigned DEFAULT NULL,
  `tablename` varchar(70) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of backuptables
-- ----------------------------
INSERT INTO `backuptables` VALUES ('1', '1', 'global_grants');
INSERT INTO `backuptables` VALUES ('2', '1', 'help_relation');
INSERT INTO `backuptables` VALUES ('3', '2', 'data_locks');
INSERT INTO `backuptables` VALUES ('4', '2', 'events_statements_summary_by_account_by_event_name');

-- ----------------------------
-- Table structure for databases
-- ----------------------------
DROP TABLE IF EXISTS `databases`;
CREATE TABLE `databases` (
  `id` mediumint(5) unsigned NOT NULL AUTO_INCREMENT,
  `ip` varchar(50) NOT NULL,
  `database` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  `port` mediumint(5) unsigned NOT NULL,
  `backuppath` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'backup',
  `strategy` smallint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:getLockDB,1:getUnlockDB,2:getOnlyStruct,3:getOnlyData,4:getSplitTables',
  `backuptable` smallint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:不指定备份表,1:指定备份表',
  `ignoretable` smallint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:不指定忽略表,1:指定忽略表',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'utf8',
  `backupcmd` enum('mysqlpump','mysqldump') NOT NULL DEFAULT 'mysqldump',
  `enable` smallint(1) unsigned NOT NULL DEFAULT '1' COMMENT '1：表示开启备份 0：表示关闭备份',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of databases
-- ----------------------------
INSERT INTO `databases` VALUES ('1', 'localhost', 'mysql', 'root', '123456', '3306', 'backup', '4', '1', '0', 'utf8', 'mysqldump', '1');
INSERT INTO `databases` VALUES ('2', 'localhost', 'performance_schema', 'root', '123456', '3306', 'backup', '3', '0', '1', 'utf8', 'mysqldump', '1');

-- ----------------------------
-- Table structure for ignoretables
-- ----------------------------
DROP TABLE IF EXISTS `ignoretables`;
CREATE TABLE `ignoretables` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `databaseid` mediumint(4) unsigned DEFAULT NULL,
  `tablename` varchar(70) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of ignoretables
-- ----------------------------
INSERT INTO `ignoretables` VALUES ('1', '1', 'global_grants');
INSERT INTO `ignoretables` VALUES ('2', '1', 'help_relation');
INSERT INTO `ignoretables` VALUES ('3', '2', 'data_locks');
INSERT INTO `ignoretables` VALUES ('4', '2', 'events_statements_summary_by_account_by_event_name');
