-- Sql Schema for loading data into mysql

drop table if exists LogFil;
create table LogFil(id int primary key auto_increment not null,Dat timestamp(3) not null,Ip varchar(20) not null,Request varchar(20) not null,Status int not null,Agent varchar(200) not null);


--  MySQL Schema with comments on why it's blocked.


Drop table if exists whyItsBlocked;
create Table whyItsBlocked(id int primary key auto_increment not null,IP varchar(20) comment 'The IP is blocked' not null,threshold int not null);