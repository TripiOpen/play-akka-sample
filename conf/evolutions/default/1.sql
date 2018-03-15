# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table booking_log (
  id                            integer auto_increment not null,
  content                       varchar(255),
  created_time                  datetime(6),
  constraint pk_booking_log primary key (id)
);

create table ticket_flight (
  id                            integer auto_increment not null,
  price                         double not null,
  created_time                  bigint not null,
  depart_date                   datetime(6),
  constraint pk_ticket_flight primary key (id)
);

create table ticket_hotel (
  id                            integer auto_increment not null,
  price                         double not null,
  created_time                  bigint not null,
  number_room                   integer not null,
  constraint pk_ticket_hotel primary key (id)
);


# --- !Downs

drop table if exists booking_log;

drop table if exists ticket_flight;

drop table if exists ticket_hotel;

