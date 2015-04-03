# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table applicant_model (
  applicant_id              bigint auto_increment not null,
  applicant_email           varchar(255),
  applicant_title           varchar(255),
  applicant_first_name      varchar(255),
  applicant_last_name       varchar(255),
  applicant_city            varchar(255),
  applicant_password        varchar(255),
  cv_file_path              varchar(255),
  intro_video_path          varchar(255),
  processed                 integer,
  date_of_signup            datetime,
  constraint pk_applicant_model primary key (applicant_id))
;

create table job_listing_model (
  job_id                    bigint auto_increment not null,
  job_sector                varchar(255),
  job_title                 varchar(255),
  job_type                  varchar(255),
  job_location              varchar(255),
  job_salary                double,
  job_description           longtext,
  job_criteria              longtext,
  constraint pk_job_listing_model primary key (job_id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table applicant_model;

drop table job_listing_model;

SET FOREIGN_KEY_CHECKS=1;

