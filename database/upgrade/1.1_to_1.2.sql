start TRANSACTION;
create table VERSION
(
   VERSION              varchar(50),
   IS_CURRENT           char(1),
   DESCRIPTION          varchar(100),
   LAST_UPDATE          datetime
) DEFAULT CHARACTER SET=utf8;
LOCK TABLES `VERSION` WRITE;
insert into VERSION values ("1.2","1","",now()); 
UNLOCK TABLES;
alter table TRANSACTION add AS_OF_DATE date;
LOCK TABLES `TRANSACTION` WRITE;
update TRANSACTION set AS_OF_DATE = date(SOURCE_TRAN_DT);
UNLOCK TABLES;
commit;
