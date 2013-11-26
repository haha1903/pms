alter table ACCOUNT add unique (ACCOUNT_NO);
drop table if exists MARKET_DATA;
create table MARKET_DATA
(
   SECURITY_ID          bigint not null PRIMARY KEY,
   AS_OF_DATE           date,
   TIMESTAMP            datetime,
   PRICE                decimal(31, 11),
   PREVIOUS_PRICE       decimal(31, 11),
   RECEIVED_TIME        datetime,
   SOURCE               varchar(30),
   LAST_UPDATE      timestamp not null default current_timestamp on update current_timestamp
)DEFAULT CHARACTER SET=utf8;

/*
 * 1. Add new column TRADE_EARN_CAMT into POSITION_YIELD
 * 2. Add foreign keys
 */
alter table POSITION_YIELD add TRADE_EARN_CAMT decimal(31,11) default 0 not null comment "交易盈亏";
