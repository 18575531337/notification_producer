# 消息生产模块
***

## 初始化配置
resources/prop目录下
1. http.properties 配置http连接池信息，连接主机，链接服务（调用方法）
2. jdbc.properties 配置mysql和mongodb的相关连接池配置
3. scheduled.properties 配置爬虫投放定时时间，写入通知定时时间，写入通知缓存更新定时时间
***

## mysql:
1. 相关初始化建表语句参考sql/notification_producer.sql
2. 相关规则写入np_rule 表(规则需要手动插入)
相关规则初始化脚本 sql/init—rule.sql

说明：
insert into np_rule(
	mongodb_table_name,desc,operation_field,trigger_condition,
	push_title,push_title_field,push_content_type,rule_type,rule_push_type
)values(...)
mongodb_table_name字段是mongodb表名
desc是描述
operation_field是要操纵的字段（mongo表的字段）
trigger_condition是触发条件（目前仅支持两个 0，-1   0是包含触发，-1是特殊任务需要特殊对待）
push_title是推送的标题string
push_title_field是从mongo表中获取标题（push_title不为空时才有效）
push_content_type 是推送内容类型
rule_type是规则类型（目前就3个 0 风险规则、1 营销规则、-1 特殊情况-例如以上两者均包含）
rule_push_type是具体的规则推送类型具体类型可以查看

相应字段说明具体可以参考sql/conf下文件

3. 相关mysql 关注表需要写入np_version_manager 表
相关表初始化脚本 sql/init-np_version_manager.sql

4.增加过滤字段
2017-07-20 15：54 增加时间过滤字段.sql
直接运行脚本(如果数据库是安全模式，把第一行注释的sql去掉注释运行即可)

说明：
mysql表需要手动插入，mongo不需要。
insert into(
	table_name,notification_table_name,`desc`,last_time,table_type
)values(
	'follow_item_lanzhou','notification_lanzhou',null,null,'MYSQL'
);
table_name 是关注表表名，notification_table_name 是这个关注表的通知表
last_time为空时间默认两天前开始，表类型手动插入均为'MYSQL'

相应字段说明具体可以参考sql/conf下文件
***

## 部署：
1. mvn clean package -P{ENV}
2. tar -zcvf ENV.tgz project
3. scp 到目标机器，如：lanzhou3:/home/work/apps/notification-producer
4. cd /home/work/apps/notification-producer && sh startup.sh start即可
#scp -i /Users/haizhi/Desktop/test/web1.pem ENV.tgz work@web1:/home/work/apps/notification-producer
***