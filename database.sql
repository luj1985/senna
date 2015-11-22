create database senna character set 'utf8' collate 'utf8_general_ci';

set character_set_client='utf8';
set character_set_connection='utf8';
set character_set_results='utf8';
set character_set_server='utf8';
-- use senna;
-- drop table questions;
-- drop table users;
-- drop table results;

create table if not exists questions (
  id int not null auto_increment,
  question varchar(1024) not null,
  option1 varchar(200) not null,
  option2 varchar(200) not null,
  option3 varchar(200) not null,
  answer int not null,
  primary  key (id)
) engine=innodb default charset=utf8;

create table if not exists users (
 id int not null auto_increment,
 name varchar(200),
 primary key (id)
) engine=innodb default charset=utf8;

create table if not exists results (
  id int not null auto_increment,
  user_id int not null,
  result float not null,
  primary key (id)
) engine=innodb default charset=utf8;
