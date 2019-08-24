/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 80016
Source Host           : localhost:3306
Source Database       : easydump

Target Server Type    : MYSQL
Target Server Version : 80016
File Encoding         : 65001

Date: 2019-08-24 23:03:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for backuptables
-- ----------------------------
DROP TABLE IF EXISTS `backuptables`;
CREATE TABLE `backuptables` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `databaseid` mediumint(4) unsigned DEFAULT NULL,
  `tablename` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of backuptables
-- ----------------------------

-- ----------------------------
-- Table structure for databases
-- ----------------------------
DROP TABLE IF EXISTS `databases`;
CREATE TABLE `databases` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `ip` varchar(50) NOT NULL,
  `database` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  `port` mediumint(5) unsigned NOT NULL,
  `backuppath` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'backup',
  `strategy` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:getLockDB,1:getUnlockDB,2:getOnlyStruct,3:getOnlyData',
  `backuptable` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:不指定备份表,1:指定备份表',
  `igonretable` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:不指定忽略表,1:指定忽略表',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of databases
-- ----------------------------

-- ----------------------------
-- Table structure for ingoretables
-- ----------------------------
DROP TABLE IF EXISTS `ingoretables`;
CREATE TABLE `ingoretables` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `databaseid` mediumint(4) unsigned DEFAULT NULL,
  `tablename` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of ingoretables
-- ----------------------------
