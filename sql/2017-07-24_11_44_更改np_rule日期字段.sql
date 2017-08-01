update np_rule set field_enddate = 'year' where mongodb_table_name = 'tax_payer_level_A';
update np_rule set field_enddate = 'publish_time' where mongodb_table_name = 'penalty';
update np_rule set field_enddate = null where mongodb_table_name = 'enterprise_data_gov_change_info';
update np_rule set field_enddate = 'filing_date' where mongodb_table_name = 'judge_process';