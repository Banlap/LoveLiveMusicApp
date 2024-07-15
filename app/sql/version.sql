/*
 Navicat Premium Data Transfer
 Date: 11/07/2024 16:15:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for version
-- ----------------------------
DROP TABLE IF EXISTS `version`;
CREATE TABLE `version` (
  `version_id` int(8) NOT NULL,
  `version_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `version_code` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `version_type` varchar(1) CHARACTER SET utf8 DEFAULT NULL,
  `version_title` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `version_content` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `version_url` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `version_date` datetime DEFAULT NULL,
  PRIMARY KEY (`version_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

SET FOREIGN_KEY_CHECKS = 1;
