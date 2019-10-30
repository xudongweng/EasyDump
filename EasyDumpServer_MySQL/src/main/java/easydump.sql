/*
Navicat MySQL Data Transfer

Source Server         : 10.236.72.34
Source Server Version : 50641
Source Host           : 10.236.72.34:3306
Source Database       : easydump

Target Server Type    : MYSQL
Target Server Version : 50641
File Encoding         : 65001

Date: 2019-09-09 14:10:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for backuptables
-- ----------------------------
DROP TABLE IF EXISTS `backuptables`;
CREATE TABLE `backuptables` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `dbid` mediumint(4) unsigned DEFAULT NULL,
  `tablename` varchar(70) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
DROP TABLE IF EXISTS `dbs`;
CREATE TABLE `dbs` (
  `id` mediumint(5) unsigned NOT NULL AUTO_INCREMENT,
  `host` varchar(50) NOT NULL,
  `database` varchar(20) NOT NULL,
  `user` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  `port` mediumint(5) unsigned NOT NULL,
  `backuppath` varchar(200) NOT NULL DEFAULT 'backup',
  `filenum` smallint(1) unsigned NOT NULL DEFAULT '7' COMMENT '备份文件数量',
  `strategy` smallint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:getLockDB,1:getUnlockDB,2:getOnlyStruct,3:getOnlyData,4:getSplitTables',
  `backuptable` smallint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:不指定备份表,1:指定备份表',
  `ignoretable` smallint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:不指定忽略表,1:指定忽略表',
  `code` varchar(20) NOT NULL DEFAULT 'utf8',
  `backupcmd` enum('mysqlpump','mysqldump') NOT NULL DEFAULT 'mysqldump',
  `enable` smallint(1) unsigned NOT NULL DEFAULT '1' COMMENT '1：表示开启备份 0：表示关闭备份',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of databases
-- ----------------------------
INSERT INTO `dbs` VALUES (1, 'localhost', 'mysql', 'root', '123456', '3306', 'backup', '3', '4', '0', '0', 'utf8', 'mysqldump', '1');
INSERT INTO `dbs` VALUES (2, 'localhost', 'performance_schema', 'root', '123456', '3306', 'backup', '3', '4', '0', '0', 'utf8', 'mysqldump', '1');

-- ----------------------------
-- Table structure for ignoretables
-- ----------------------------
DROP TABLE IF EXISTS `ignoretables`;
CREATE TABLE `ignoretables` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `dbid` mediumint(4) unsigned DEFAULT NULL,
  `tablename` varchar(70) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ignoretables
-- ----------------------------
INSERT INTO `ignoretables` VALUES ('1', '1', 'global_grants');
INSERT INTO `ignoretables` VALUES ('2', '1', 'help_relation');
INSERT INTO `ignoretables` VALUES ('3', '2', 'data_locks');
INSERT INTO `ignoretables` VALUES ('4', '2', 'events_statements_summary_by_account_by_event_name');
