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
) ENGINE=InnoDB;

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
  `host` varchar(50) NOT NULL DEFAULT 'localhost',
  `database` varchar(20) NOT NULL,
  `user` varchar(20) NOT NULL DEFAULT '',
  `password` varchar(20) NOT NULL DEFAULT '',
  `port` mediumint(8) unsigned NOT NULL DEFAULT '27017',
  `authdb` varchar(20) NOT NULL DEFAULT 'admin' COMMENT '验证数据库',
  `backuppath` varchar(200) NOT NULL DEFAULT 'backup',
  `filenum` smallint(1) unsigned NOT NULL DEFAULT '7' COMMENT '备份文件数量',
  `strategy` smallint(1) unsigned NOT NULL DEFAULT '1' COMMENT '0:getUnlockDB,1:getSplitTables',
  `backuptable` smallint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:不指定备份表,1:指定备份表',
  `ignoretable` smallint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:不指定忽略表,1:指定忽略表',
  `backupcmd` varchar(20) NOT NULL DEFAULT 'mongodump',
  `enable` smallint(1) unsigned NOT NULL DEFAULT '1' COMMENT '1：表示开启备份 0：表示关闭备份',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- ----------------------------
-- Records of dbs
-- ----------------------------
INSERT INTO `dbs` VALUES ('1', 'localhost', 'mymongo', '', '', '27017', 'admin', 'backup', '3', '1', '0', '0', 'mysqldump', '1');
INSERT INTO `dbs` VALUES ('2', 'localhost', 'mymongo1', '', '', '27017', 'admin', 'backup', '3', '1', '0', '0', 'mysqldump', '1');

-- ----------------------------
-- Table structure for ignoretables
-- ----------------------------
DROP TABLE IF EXISTS `ignoretables`;
CREATE TABLE `ignoretables` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `dbid` mediumint(4) unsigned DEFAULT NULL,
  `tablename` varchar(70) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- ----------------------------
-- Records of ignoretables
-- ----------------------------
INSERT INTO `ignoretables` VALUES ('1', '1', 'global_grants');
INSERT INTO `ignoretables` VALUES ('2', '1', 'help_relation');
INSERT INTO `ignoretables` VALUES ('3', '2', 'data_locks');
INSERT INTO `ignoretables` VALUES ('4', '2', 'events_statements_summary_by_account_by_event_name');
