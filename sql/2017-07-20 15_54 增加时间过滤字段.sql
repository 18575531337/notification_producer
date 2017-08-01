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