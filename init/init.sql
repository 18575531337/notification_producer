CREATE TABLE `np_version_manager` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `table_name` varchar(40) DEFAULT NULL,
  `notification_table_name` varchar(40) DEFAULT NULL,
  `desc` varchar(30) DEFAULT NULL,
  `last_time` datetime DEFAULT NULL,
  `table_type` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uidx_table_name` (`table_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



insert into np_rule(
  mongodb_table_name, `desc`, operation_field,trigger_condition,push_title,push_title_field,push_content_type,rule_type,rule_push_type
)values(
  'court_ktgg','企业涉诉','litigant_list',0,null,'case_cause','ALL',0,0
);

insert into np_rule(
  mongodb_table_name,`desc`,operation_field,trigger_condition,push_title,push_title_field,push_content_type,rule_type,rule_push_type
)values(
  'bulletin','企业涉诉','litigant_list',0,null,'case_cause','ALL',0,0
);

insert into np_rule(
  mongodb_table_name,`desc`,operation_field,trigger_condition,push_title,push_title_field,push_content_type,rule_type,rule_push_type
)values(
  'judge_process','企业涉诉','litigant_list',0,null,'case_id,status','ALL',0,0
);

insert into np_rule(
  mongodb_table_name,`desc`,operation_field,trigger_condition,push_title,push_title_field,push_content_type,rule_type,rule_push_type
)values(
  'judgement_wenshu','企业涉诉','litigant_list',0,null,'case_cause','ALL',0,0
);

insert into np_rule(
  mongodb_table_name,`desc`,operation_field,trigger_condition,push_title,push_title_field,push_content_type,rule_type,rule_push_type
)values(
  'shixin_info','企业失信被执行','i_name',0,'失信被执行',null,'ALL',0,1
);

insert into np_rule(
  mongodb_table_name,`desc`,operation_field,trigger_condition,push_title,push_title_field,push_content_type,rule_type,rule_push_type
)values(
  'enterprise_owing_tax','企业欠税被披露','company',0,'欠税披露',null,'ALL',0,2
);

insert into np_rule(
  mongodb_table_name,`desc`,operation_field,trigger_condition,push_title,push_title_field,push_content_type,rule_type,rule_push_type
)values(
  'penalty','企业被行政处罚','accused_name',0,null,'title','ALL',0,3
);

insert into np_rule(
  mongodb_table_name,`desc`,operation_field,trigger_condition,push_title,push_title_field,push_content_type,rule_type,rule_push_type
)values(
  'enterprise_data_gov_change_info','企业特殊情况-需要特殊处理','field',-1,null,null,'CUSTOM',-1,4
);

insert into np_rule(
  mongodb_table_name,`desc`,operation_field,trigger_condition,push_title,push_title_field,push_content_type,rule_type,rule_push_type
)values(
  'tax_payer_level_A','企业被公布纳税等级为A','taxpayer_name',0,'企业被公布纳税等级为A',null,'ALL',1,6
);

insert into np_rule(
  mongodb_table_name,`desc`,operation_field,trigger_condition,push_title,push_title_field,push_content_type,rule_type,rule_push_type
)values(
  'bid_detail','企业中标','win_bid_company,public_bid_company',0,null,'title','ALL',1,7
);

insert into np_version_manager(
  `table_name`,notification_table_name,`desc`,last_time,table_type
)values(
  'iap.follow_item','iap.notification','兰州',null,'MYSQL'
);

#SET SQL_SAFE_UPDATES = 0;
#增加字段 时间字段 此表对应字段是处理日期(保证处理日期在一个月内)
alter table np_rule add field_enddate varchar(40);

update  np_rule  set field_enddate = 'court_time' where mongodb_table_name = 'court_ktgg';
update  np_rule  set field_enddate = 'change_date' where mongodb_table_name = 'enterprise_data_gov_change_info';
update  np_rule  set field_enddate = 'filling_date' where mongodb_table_name = 'judge_process';
update  np_rule  set field_enddate = 'case_date' where mongodb_table_name = 'judgement_wenshu';
update  np_rule  set field_enddate = 'bulletin_date' where mongodb_table_name = 'bulletin';
update  np_rule  set field_enddate = 'publish_date' where mongodb_table_name = 'shixin_info';
update  np_rule  set field_enddate = 'bid_date' where mongodb_table_name = 'bid_detail';

#SET SQL_SAFE_UPDATES = 1;