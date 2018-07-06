
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `t_access_stats`
-- ----------------------------
CREATE TABLE `t_access_stats` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `domainId` varchar(80) DEFAULT NULL,
  `client_ip` varchar(80) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `server_ip` varchar(80) DEFAULT NULL,
  `api_path` varchar(80) DEFAULT NULL,
  `userId` varchar(80) DEFAULT NULL,
  `account` varchar(80) DEFAULT NULL,
  `params` text,
  `result_status` varchar(10) DEFAULT NULL,
  `starttime` timestamp(3) NOT NULL DEFAULT '1990-01-01 00:00:00.000',
  `endtime` timestamp(3) NOT NULL DEFAULT '1990-01-01 00:00:00.000',
  `elapsedTime` int(11) DEFAULT NULL,
  `comCreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `comUpdateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=148042 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
