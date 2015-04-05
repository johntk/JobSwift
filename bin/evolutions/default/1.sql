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
  applicant_password_confirmation varchar(255),
  cv_file_path              varchar(255),
  cv_file_name              varchar(255),
  profile_image             varchar(255),
  intro_video_path          varchar(255),
  date_of_signup            datetime,
  constraint pk_applicant_model primary key (applicant_id))
;

create table job_application_model (
  job_application_id        bigint auto_increment not null,
  processed                 integer,
  app_applicant_id          bigint,
  job_job_id                bigint,
  constraint pk_job_application_model primary key (job_application_id))
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

alter table job_application_model add constraint fk_job_application_model_app_1 foreign key (app_applicant_id) references applicant_model (applicant_id) on delete restrict on update restrict;
create index ix_job_application_model_app_1 on job_application_model (app_applicant_id);
alter table job_application_model add constraint fk_job_application_model_job_2 foreign key (job_job_id) references job_listing_model (job_id) on delete restrict on update restrict;
create index ix_job_application_model_job_2 on job_application_model (job_job_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table applicant_model;

drop table job_application_model;

drop table job_listing_model;

SET FOREIGN_KEY_CHECKS=1;

