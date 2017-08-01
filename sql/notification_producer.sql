CREATE TABLE `np_version_manager` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `table_name` varchar(40) DEFAULT NULL,
  `notification_table_name` varchar(40) DEFAULT NULL,
  `desc` varchar(30) DEFAULT NULL,
  `last_time` datetime DEFAULT NULL,
  `table_type` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uidx_table_name` (`table_name`)
) ENGINE=InnoDB AUTO_INCREMENT=143 DEFAULT CHARSET=utf8;

CREATE TABLE `np_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mongodb_table_name` varchar(40) DEFAULT NULL,
  `desc` varchar(30) DEFAULT NULL COMMENT '规则相关的信息',
  `operation_field` varchar(60) DEFAULT NULL,
  `trigger_condition` varchar(60) NOT NULL COMMENT '规则触发条件',
  `push_title` varchar(30) DEFAULT NULL COMMENT '推送标题',
  `push_title_field` varchar(20) DEFAULT NULL COMMENT '推送标题根据字段',
  `push_content_type` varchar(20) DEFAULT NULL,
  `push_content_field` varchar(20) DEFAULT NULL COMMENT '推送内容',
  `rule_type` int(11) DEFAULT NULL COMMENT '规则类型 例如：风险规则',
  `rule_push_type` int(11) DEFAULT NULL COMMENT '规则推送类型 隶属于 规则类型的 子类型  例如 ：开庭公告',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;


