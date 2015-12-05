-- drop database senna;
-- drop table questions;
-- drop table users;
-- drop table results;

create database senna character set 'utf8' collate 'utf8_general_ci';

use senna;
set character_set_client='utf8';
set character_set_connection='utf8';
set character_set_results='utf8';
set character_set_server='utf8';

create table if not exists questions (
  id int not null auto_increment,
  brand varchar(100) not null,
  question varchar(1024) not null,
  option1 varchar(200) not null,
  option2 varchar(200) not null,
  option3 varchar(200) not null,
  answer int not null,
  primary  key (id)
) engine=innodb default charset=utf8;

create table if not exists users (
 id int not null auto_increment,
 uid varchar(200),
 name varchar(200),
 primary key (id)
) engine=innodb default charset=utf8;

create table if not exists results (
  id int not null auto_increment,
  uid varchar(200) not null,
  result float not null,
  primary key (id)
) engine=innodb default charset=utf8;


alter table users add column mobile varchar(200);
alter table questions add column rank float not null;


create table if not exists views (
  id int not null auto_increment,
  name varchar(200) not null,
  count int not null,
  primary key (id)
) engine=innodb default charset=utf8;
