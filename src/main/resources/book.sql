drop database if exists book;

create database book;

\connect book;

create table books (
  id serial not null primary key,
  data json not null
);

insert into books
  (data)
values
  ('{"title":"It","author":"Stephen King","genre":"Horror"}');

insert into books
  (data)
values
  ('{"title":"A Game of Thrones","author":"George R.R. Martin","genre":"Fantasy"}');

insert into books
  (data)
values
  ('{"title":"A Scanner Darkly","author":"Philip K. Dick","genre":"Science Fiction"}');
