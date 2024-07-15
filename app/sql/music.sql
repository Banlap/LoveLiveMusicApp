/*
 Navicat Premium Data Transfer
 Date: 11/07/2024 16:14:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for music
-- ----------------------------
DROP TABLE IF EXISTS `music`;
CREATE TABLE `music` (
  `music_id` bigint(20) NOT NULL,
  `music_type` varchar(20) DEFAULT NULL,
  `music_name` varchar(50) DEFAULT NULL,
  `music_singer` varchar(30) DEFAULT NULL,
  `music_favorite` int(11) DEFAULT NULL,
  `music_img` varchar(400) DEFAULT NULL,
  `music_url` varchar(400) DEFAULT NULL,
  `music_lyric` varchar(400) DEFAULT NULL,
  `music_flac` varchar(400) DEFAULT NULL,
  PRIMARY KEY (`music_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
