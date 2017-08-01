
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