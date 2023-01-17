-- creates "MYDATA" table --
create table MYDATA
(ID int not null primary key generated always as identity (start with 1, increment by 1),
TEXTDATA varchar(255),
DOUBLEDATA double)
