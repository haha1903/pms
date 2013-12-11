start TRANSACTION;
/*      add VERSION */
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
/*      alter TRANSACTION */
LOCK TABLES `TRANSACTION` WRITE;
alter table TRANSACTION add AS_OF_DATE date;
update TRANSACTION set AS_OF_DATE = date(SOURCE_TRAN_DT);
UNLOCK TABLES;
/*      alter SOURCE_TRANSACTION */
LOCK TABLES `SOURCE_TRANSACTION` WRITE;
alter table SOURCE_TRANSACTION add ORDER_ID bigint;
UNLOCK TABLES;
/*      drop ORDERS */
alter table ORDERS drop foreign key FK_ORDER_POSITION;
alter table ORDERS drop foreign key FK_ORDER_ORDER_CLASS;
alter table ORDERS drop foreign key FK_ORDER_SOURCE;
alter table TRADE_ORDER drop foreign key FK_TRADE_ORDER_ORDER;
alter table CASH_ORDER drop foreign key FK_CASH_ORDER_ORDER;
alter table CORPACT_ORDER drop foreign key FK_CORPACT_ORDER_ORDER;
drop table if exists ORDERS;
/*      create ORDER_BASKET */
create table ORDER_BASKET
(
   ORDER_BASKET_ID      bigint not null AUTO_INCREMENT comment "主键",
   LAST_UPDATE            timestamp not null default current_timestamp on update current_timestamp,
   primary key (ORDER_BASKET_ID)
) DEFAULT CHARACTER SET=utf8;
/*      create ORDER_STATUS */
create table ORDER_STATUS
(
   ORDER_STATUS_CD       varchar(20) not null,
   ORDER_STATUS_DESC     varchar(200),
   primary key (ORDER_STATUS_CD)
) DEFAULT CHARACTER SET=utf8;
INSERT INTO `ORDER_STATUS` VALUES ('CREATED', '已创建');
INSERT INTO `ORDER_STATUS` VALUES ('PLACED', '已下单');
INSERT INTO `ORDER_STATUS` VALUES ('REJECTED', '被拒绝');
INSERT INTO `ORDER_STATUS` VALUES ('CANCELLED', '已取消');
/*      create ORDERS */
create table ORDERS
(
   ORDER_ID             bigint not null comment "订单ID",
   SEQ_NO               bigint not null comment "订单序号, 用于update订单。同一个订单ID下，大的SEQ_NO表示新的记录",
   IS_CURRENT           char(1) not null comment "是否是当前的记录，或是旧的记录",
   ORDER_BASKET_ID      bigint not null comment "ORDER_BASKET ID",
   ACCOUNT_ID           bigint not null comment "组合ID",
   SUB_ACCOUNT_ID       bigint comment "子组合ID，预留",
   SECURITY_ID          bigint not null comment "证券ID",
   AS_OF_DATE           date not null comment "下单的日期",
   AS_OF_TIME           datetime not null comment "下单的时间",
   AMOUNT               decimal(31, 11) not null comment "下单数量",
   TRADE_SIDE_CD        varchar(10) not null comment "交易方向",
   PRICE_LIMIT          decimal(31, 11) comment "价格限制",
   PRICE_GUIDELINE      decimal(31, 11) comment "价格guideline",
   STP_FLAG             char(1) comment "是否使用自动交易 (STP: Straight Through Process)",
   STP_ALGORITHM        varchar(30) comment "自动交易算法",
   STP_START_TIME       datetime comment "自动交易开始时间",
   STP_END_TIME         datetime comment "自动交易结束时间",
   STP_BROKER_CAPACITY  varchar(20) comment "不知道是什么，等Xingen的解释",
   PARTY_ID             bigint comment "未使用",
   ORDER_CLASS_CD       varchar(20) comment "订单类型，未使用",
   SOURCE_ORDER_ID      varchar(40) comment "未使用",
   TRAN_SOURCE_ID       int comment "未使用",
   ORDER_STATUS_CD      varchar(20) comment "订单状态: CREATED, PLACED, REJECTED, CANCELLED",
   STATUS_CHANGE_DATE   datetime comment "订单状态更改时间",
   COMMENTS             varchar(200) comment "注释",
   AUTHORIZER_ID        bigint comment "未使用",
   LAST_UPDATE            timestamp not null default current_timestamp on update current_timestamp,
   primary key (ORDER_ID, SEQ_NO)
) DEFAULT CHARACTER SET=utf8;
alter table ORDERS add constraint FK_ORDER_ACCOUNT foreign key (ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table ORDERS add constraint FK_ORDER_ORDER_BASKET foreign key (ORDER_BASKET_ID)
      references ORDER_BASKET (ORDER_BASKET_ID) on delete restrict on update restrict;

alter table ORDERS add constraint FK_ORDER_ORDER_STATUS_CD foreign key (ORDER_STATUS_CD)
      references ORDER_STATUS (ORDER_STATUS_CD) on delete restrict on update restrict;

alter table ORDERS add constraint FK_ORDER_TRADE_SIDE foreign key (TRADE_SIDE_CD)
      references TRADE_SIDE (TRADE_SIDE_CD) on delete restrict on update restrict;
alter table CASH_ORDER add constraint FK_CASH_ORDER_ORDER foreign key (ORDER_ID)
      references ORDERS (ORDER_ID) on delete restrict on update restrict;
alter table CORPACT_ORDER add constraint FK_CORPACT_ORDER_ORDER foreign key (ORDER_ID)
      references ORDERS (ORDER_ID) on delete restrict on update restrict;
alter table TRADE_ORDER add constraint FK_TRADE_ORDER_ORDER foreign key (ORDER_ID)
      references ORDERS (ORDER_ID) on delete restrict on update restrict;
commit;
