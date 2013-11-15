/*==============================================================*/
/* Table: ACCOUNT                                               */
/*==============================================================*/
create table ACCOUNT
(
   ACCOUNT_ID           bigint not null AUTO_INCREMENT,
   PARTY_ID             bigint,
   PARENT_ACCOUNT_ID    bigint,
   COUNTRY_CD           char(2) not null,
   CURRENCY_CD          char(3) not null,
   ACCOUNT_CLASS_CD     varchar(20) not null,
   ACCOUNT_TYPE_ID      bigint not null,
   ACCOUNT_NO           varchar(20),
   ACCOUNT_NAME         varchar(80) not null,
   OPEN_DATE            datetime not null,
   STATUS_CHANGE_DATE   datetime,
   ACCOUNT_STATUS       varchar(20),
   CASH_AVAILABLE       decimal(31,11),
   SECURITY_VALUE       decimal(31,11),
   CREDIT_AVAILABLE     decimal(31,11),
   CREDIT_USED          decimal(31,11),
   BENCHMARK_MARKET_INDEX_ID    bigint,
   LAST_UPDATE          timestamp not null default current_timestamp on update current_timestamp,
   primary key (ACCOUNT_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: ACCOUNT_ASSOCIATION                                   */
/*==============================================================*/
create table ACCOUNT_ASSOCIATION
(
   PRINCIPLE_ACCOUNT_ID bigint not null,
   ASSOCIATE_ACCOUNT_ID bigint not null,
   AA_TYPE              varchar(20),
   AA_EFFECTIVE_DATE    datetime,
   AA_STATUS            varchar(20),
   STATUS_CHANGE_DATE   datetime,
   primary key (PRINCIPLE_ACCOUNT_ID, ASSOCIATE_ACCOUNT_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: ACCOUNT_TYPE                                          */
/*==============================================================*/
create table ACCOUNT_TYPE
(
   ACCOUNT_TYPE_ID      bigint not null,
   ACCOUNT_TYPE_NAME    varchar(30),
   ACCOUNT_TYPE_DESC    varchar(200),
   primary key (ACCOUNT_TYPE_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: ACCOUNT_CLASS                                         */
/*==============================================================*/
create table ACCOUNT_CLASS
(
   ACCOUNT_CLASS_CD     varchar(20) not null,
   ACCOUNT_CLASS_DESC   varchar(200),
   primary key (ACCOUNT_CLASS_CD)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: ACCOUNT_GROUP                                         */
/*==============================================================*/
create table ACCOUNT_GROUP
(
   ACCT_GROUP_ID        bigint not null,
   ACCT_GROUP_TYPE_CD   varchar(20) not null,
   PARTY_ID             bigint,
   CURRENCY_CD          char(3) not null,
   ACCT_GROUP_NAME      varchar(40),
   GROUP_EFF_DATE       datetime,
   GROUP_STATUS         varchar(20),
   STATUS_CHANGE_DATE   datetime,
   CASH_AVAILABLE       decimal(31,11),
   SECURITY_VALUE       decimal(31,11),
   CREDIT_AVAILABLE     decimal(31,11),
   CREDIT_USED          decimal(31,11),
   primary key (ACCT_GROUP_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: ACCT_GROUP_TYPE                                       */
/*==============================================================*/
create table ACCT_GROUP_TYPE
(
   ACCT_GROUP_TYPE_CD   varchar(20) not null,
   ACCT_GROUP_TYPE_DESC varchar(200),
   primary key (ACCT_GROUP_TYPE_CD)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: BANK_ACCOUNT                                          */
/*==============================================================*/
create table BANK_ACCOUNT
(
   BANK_ACCOUNT_ID      bigint not null,
   PARTY_ID             bigint not null,
   CURRENCY_CD          char(3) not null,
   BANK_ACCOUNT_CD      varchar(20),
   BANK_ACCOUNT_NAME    varchar(80),
   ACCOUNT_BALANCE      decimal(31,11),
   primary key (BANK_ACCOUNT_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: BROKER                                                */
/*==============================================================*/
create table BROKER
(
   PARTY_ID             bigint not null,
   BROKER_CD            varchar(40),
   BROKER_STATUS        varchar(20),
   STATUS_CHANGE_DATE   datetime,
   primary key (PARTY_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: BROKER_ASSET_CLASS                                    */
/*==============================================================*/
create table BROKER_ASSET_CLASS
(
   PARTY_ID             bigint not null,
   ASSET_CLASS_CD       varchar(10) not null,
   EFFECTIVE_DATE       datetime,
   END_DATE             datetime,
   primary key (ASSET_CLASS_CD, PARTY_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: BROKER_EXCHANGE                                       */
/*==============================================================*/
create table BROKER_EXCHANGE
(
   PARTY_ID             bigint not null,
   EXCHANGE_CD          char(4) not null,
   EFFECTIVE_DATE       datetime,
   END_DATE             datetime,
   primary key (EXCHANGE_CD, PARTY_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CAPITAL_POOL                                          */
/*==============================================================*/
create table CAPITAL_POOL
(
   BANK_ACCOUNT_ID      bigint not null,
   ACCOUNT_ID           bigint not null,
   POOL_EFFECTIVE_DATE  datetime,
   POOL_STATUS          varchar(20),
   POLL_STATUS_CHANGE_DATE datetime,
   primary key (BANK_ACCOUNT_ID, ACCOUNT_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CASH_ORDER                                            */
/*==============================================================*/
create table CASH_ORDER
(
   ORDER_ID             bigint not null,
   CASH_ORDER_TYPE_CD   varchar(20) not null,
   ACCOUNT_ID           bigint,
   TRADE_TYPE           varchar(20),
   EXT_BANK_ID          bigint,
   EXT_ACCT_ID          varchar(40),
   QUANTITY             decimal(31,11),
   EXT_CURR_CD          char(3),
   primary key (ORDER_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CASH_ORDER_QUEUE                                      */
/*==============================================================*/
create table CASH_ORDER_QUEUE
(
   ORDER_SOURCE_ID      int,
   SOURCE_ORDER_ID      varchar(40),
   ORDER_OPEN_DATE      datetime,
   ORDER_STATUS         varchar(20),
   STATUS_CHANGE_DATE   datetime,
   ORDER_PARTY_ID       bigint,
   AUTHORIZER_ID        bigint,
   ORDER_POSITION_ID    bigint,
   COMMENT              varchar(200),
   CASH_ORDER_TYPE      varchar(20),
   INT_ACCT_ID          bigint,
   EXT_BANK_ID          bigint,
   EXT_ACCT_ID          varchar(40),
   QUANTITY             decimal(31,11),
   EXT_CURR_CD          char(3),
   PROC_DT              datetime,
   PROC_STATUS          varchar(20),
   PROC_NOTE            varchar(200)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CASH_ORDER_TYPE                                       */
/*==============================================================*/
create table CASH_ORDER_TYPE
(
   CASH_ORDER_TYPE_CD   varchar(20) not null,
   CASH_ORDER_TYPE_DESC varchar(200),
   primary key (CASH_ORDER_TYPE_CD)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CASH_TRANSACTION                                      */
/*==============================================================*/
create table CASH_TRANSACTION
(
   TRANSACTION_ID       bigint not null,
   CASH_TRAN_TYPE_CD    varchar(20) not null,
   CASH_TRAN_METH_CD    varchar(20) not null,
   CASH_TRAN_REASON_CD  varchar(20) not null,
   PARTY_ID             bigint,
   INT_ACCT_ID          bigint,
   EXT_ACCT_CD          varchar(40),
   AMOUNT               decimal(31,11),
   CURRENCY2_CD         char(3),
   FX_RATE1             decimal(31,11),
   primary key (TRANSACTION_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CASH_TRAN_METHOD                                      */
/*==============================================================*/
create table CASH_TRAN_METHOD
(
   CASH_TRAN_METH_CD    varchar(20) not null,
   CASH_TRAN_METH_DESC  varchar(200),
   primary key (CASH_TRAN_METH_CD)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CASH_TRAN_QUEUE                                       */
/*==============================================================*/
create table CASH_TRAN_QUEUE
(
   TRAN_SRC_ID          int,
   SOURCE_TRAN_ID       varchar(40),
   ORDER_SRC_ID         int,
   SOURCE_ORDER_ID      varchar(40),
   SOURCE_TRAN_DT       datetime,
   QUEUE_TIMESTAMP      timestamp,
   POSI_ID              bigint,
   TRAN_TYPE            varchar(20),
   TRAN_METHOD          varchar(20),
   COUNTER_PARTY_ID     bigint,
   COUNTER_ACCT_ID      bigint,
   EXT_ACCT_CD          varchar(40),
   AMOUNT               decimal(31,11),
   COUNTER_CURR_CD      char(3),
   FX_RATE1             decimal(31,11),
   FEES                 decimal(31,11),
   TRAN_REASON          varchar(100),
   PROC_DT              datetime,
   PROC_STATUS          varchar(20),
   PROC_NOTE            varchar(200)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CASH_TRAN_REASON                                      */
/*==============================================================*/
create table CASH_TRAN_REASON
(
   CASH_TRAN_REASON_CD  varchar(20) not null,
   CASH_TRAN_REASON_DESC varchar(200),
   primary key (CASH_TRAN_REASON_CD)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CASH_TRAN_TYPE                                        */
/*==============================================================*/
create table CASH_TRAN_TYPE
(
   CASH_TRAN_TYPE_CD    varchar(20) not null,
   CASH_TRAN_TYPE_DESC  varchar(200),
   primary key (CASH_TRAN_TYPE_CD)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CORPACT_ORDER                                         */
/*==============================================================*/
create table CORPACT_ORDER
(
   ORDER_ID             bigint not null,
   CORP_ACT_TYPE_CD     varchar(20) not null,
   FACTOR_PER_SHARE     decimal(31,11),
   VALUE_PER_SHARE      decimal(31,11),
   primary key (ORDER_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CORP_ACT_TRANSACTION                                  */
/*==============================================================*/
create table CORP_ACT_TRANSACTION
(
   TRANSACTION_ID       bigint not null,
   PARTY_ID             bigint,
   SECURITY_ID          bigint not null,
   CORP_ACT_TYPE_CD     varchar(20) not null,
   SHARE_FACTOR         decimal(31,11),
   VALUE_PER_SHARE      decimal(31,11),
   CURRENCY2_CD         char(3),
   FX_RATE1             decimal(31,11),
   primary key (TRANSACTION_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CORP_ACT_TYPE                                         */
/*==============================================================*/
create table CORP_ACT_TYPE
(
   CORP_ACT_TYPE_CD     varchar(20) not null,
   CORP_ACT_TYPE_DESC   varchar(200),
   primary key (CORP_ACT_TYPE_CD)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CPAC_ORDER_QUEUE                                      */
/*==============================================================*/
create table CPAC_ORDER_QUEUE
(
   ORDER_SOURCE_ID      int,
   SOURCE_ORDER_ID      varchar(40),
   ORDER_OPEN_DATE      datetime,
   ORDER_STATUS         varchar(20),
   STATUS_CHANGE_DATE   datetime,
   ORDER_PARTY_ID       bigint,
   AUTHORIZER_ID        bigint,
   ORDER_POSITION_ID    bigint,
   COMMENT              varchar(200),
   CORP_ACT_TYPE        varchar(20),
   FACTOR_PER_SHARE     decimal(31,11),
   VALUE_PER_SHARE      decimal(31,11),
   PROC_DT              datetime,
   PROC_STATUS          varchar(20),
   PROC_NOTE            varchar(200)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CPAC_TRAN_QUEUE                                       */
/*==============================================================*/
create table CPAC_TRAN_QUEUE
(
   TRAN_SRC_ID          int,
   SOURCE_TRAN_ID       varchar(40),
   ORDER_SRC_ID         int,
   SOURCE_ORDER_ID      varchar(40),
   SOURCE_TRAN_DT       datetime,
   QUEUE_TIMESTAMP      timestamp,
   POSI_ID              bigint,
   BROKER_ID            bigint,
   SEC_ID               bigint,
   SHARE_FACTOR         decimal(31,11),
   VALUE_PER_SHARE      decimal(31,11),
   FX_RATE2             decimal(31,11),
   PROC_DT              datetime,
   PROC_STATUS          varchar(20),
   PROC_NOTE            varchar(200)
) DEFAULT CHARACTER SET=utf8;


/*==============================================================*/
/* Table: GROUP_ACCT                                            */
/*==============================================================*/
create table GROUP_ACCT
(
   ACCT_GROUP_ID        bigint not null,
   ACCOUNT_ID           bigint not null,
   EFFECTIVE_DATE       datetime,
   GROUPING_STATUS      varchar(20),
   STATUS_CHANGE_DATE   datetime,
   primary key (ACCT_GROUP_ID, ACCOUNT_ID)
) DEFAULT CHARACTER SET=utf8;


/*==============================================================*/
/* Table: LEDGER                                                */
/*==============================================================*/
create table LEDGER
(
   LEDGER_ID             bigint not null,
   LEDGER_TYPE_ID        bigint not null,
   LONG_SHORT            char(1) not null,
   LEDGER_NAME           varchar(100) not null,
   LEDGER_DESC           varchar(200),
   START_DATE            date,
   END_DATE              date,
   LAST_UPDATE           timestamp not null default current_timestamp on update current_timestamp,
   primary key (LEDGER_ID),
   unique (LEDGER_NAME)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: LEDGER_TYPE                                           */
/*==============================================================*/
create table LEDGER_TYPE
(
   LEDGER_TYPE_ID         bigint not null,
   LEDGER_TYPE_DESC       varchar(100),
   LAST_UPDATE            timestamp not null default current_timestamp on update current_timestamp,
   primary key (LEDGER_TYPE_ID)
) DEFAULT CHARACTER SET=utf8;


/*==============================================================*/
/* Table: ORDERS                                                */
/*==============================================================*/
create table ORDERS
(
   ORDER_ID             bigint not null,
   PARTY_ID             bigint not null,
   POSITION_ID          bigint not null,
   ORDER_CLASS_CD       varchar(20) not null,
   SOURCE_ORDER_ID      varchar(40),
   TRAN_SOURCE_ID       int,
   OPEN_DATE            datetime,
   ORDER_STATUS         varchar(20),
   STATUS_CHANGE_DATE   datetime,
   COMMENTS             varchar(200),
   AUTHORIZER_ID        bigint,
   primary key (ORDER_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: ORDER_CLASS                                           */
/*==============================================================*/
create table ORDER_CLASS
(
   ORDER_CLASS_CD       varchar(20) not null,
   ORDER_CLASS_DESC     varchar(200),
   primary key (ORDER_CLASS_CD)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: PORTFOLIO_MANAGER                                     */
/*==============================================================*/
create table PORTFOLIO_MANAGER
(
   PARTY_ID             bigint not null,
   PM_CD                varchar(32),
   PM_STATUS            varchar(20),
   STATUS_CHANGE_DATE   datetime,
   primary key (PARTY_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: POSITION                                              */
/*==============================================================*/
create table POSITION
(
   POSITION_ID          bigint not null AUTO_INCREMENT,
   ACCOUNT_ID           bigint not null,
   SECURITY_ID          bigint,
   CURRENCY_CD          char(3) not null,
   POSITION_CLASS_CD    varchar(20) not null,
   LEDGER_ID            bigint not null,
   EXCHANGE_CD          char(4) not null,
   OPEN_DATE            datetime,
   POSITION_STATUS      varchar(20),
   STATUS_CHANGE_DATE   datetime,
   LAST_UPDATE          timestamp not null default current_timestamp on update current_timestamp,
   primary key (POSITION_ID),
   index (ACCOUNT_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: POSITION_CLASS                                        */
/*==============================================================*/
create table POSITION_CLASS
(
   POSITION_CLASS_CD    varchar(20) not null,
   POSITION_CLASS_DESC  varchar(200),
   primary key (POSITION_CLASS_CD)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: POSITION_HIST                                         */
/*==============================================================*/
create table POSITION_HIST
(
   POSITION_ID          bigint not null,
   AS_OF_DATE           date not null,
   QUANTITY             decimal(31, 11) not null,
   SETTLE_QTY           decimal(31, 11),
   LAST_UPDATE          timestamp not null default current_timestamp on update current_timestamp,
   primary key (POSITION_ID, AS_OF_DATE)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: SEC_ORDER_QUEUE                                       */
/*==============================================================*/
create table SEC_ORDER_QUEUE
(
   SEC_ORDER_QID		int auto_increment primary key,
   ORDER_SOURCE_ID      int,
   SOURCE_ORDER_ID      varchar(40),
   ORDER_OPEN_DATE      datetime,
   ORDER_STATUS         varchar(20),
   ORDER_PARTY_ID       bigint,
   AUTHORIZER_ID        bigint,
   ORDER_POSITION_ID    bigint,
   COMMENT              varchar(200),
   TRADE_ORDER_TYPE      varchar(20),
   TRADE_SIDE           varchar(10),
   TRADE_TYPE           varchar(20),
   PRICE_LIMIT          decimal(31,11),
   PRICE_AVG            decimal(31,11),
   AMOUNT_OPEN          decimal(31,11),
   AMOUNT_FILLED        decimal(31,11),
   PROC_DT              datetime,
   PROC_STATUS          varchar(20),
   PROC_NOTE            varchar(200)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: SEC_TRANSACTION                                       */
/*==============================================================*/
create table SEC_TRANSACTION
(
   TRANSACTION_ID       bigint not null,
   SECURITY_ID          bigint not null,
   AMOUNT               decimal(31,11),
   INTEREST             decimal(31,11),
   ASSET_CLASS_ID       int,
   AVG_PRICE            decimal(31,11),
   SETTLE_CURR_CD       char(3),
   TRADER_ID            bigint,
   BROKER_ID            bigint,
   TRADE_SIDE_CD        varchar(10) not null,
   FX_RATE2             decimal(31,11),
   COMMISSIONS          decimal(31,11),
   FEES                 decimal(31,11),
   EXECUTION_DATE       datetime,
   SETTLEMENT_DATE      date,
   RETURN_DATE          date,
   TRAN_REASON          varchar(100),
   primary key (TRANSACTION_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: SEC_TRAN_QUEUE                                        */
/*==============================================================*/
create table SEC_TRAN_QUEUE
(
   SEC_TRAN_QID		int auto_increment primary key,
   TRAN_SRC_ID          int,
   SOURCE_TRAN_ID       varchar(40),
   ORDER_SRC_ID         int,
   SOURCE_ORDER_ID      varchar(40),
   SOURCE_TRAN_DT       datetime,
   QUEUE_TIMESTAMP      timestamp,
   POSI_ID              bigint,
   BROKER_ID            bigint,
   TRADER_ID            bigint,
   SIDE                 varchar(20),
   AMOUNT               decimal(31,11),
   AVG_PRICE            decimal(31,11),
   SETTLE_CURR_CD       char(3),
   FX_RATE2             decimal(31,11),
   COMMISSIONS          decimal(31,11),
   FEES                 decimal(31,11),
   EXECUTION_DT         datetime,
   SETTLEMENT_DT        datetime,
   TRAN_REASON          varchar(100),
   PROC_DT              datetime,
   PROC_STATUS          varchar(20),
   PROC_NOTE            varchar(200)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: TRADER                                                */
/*==============================================================*/
create table TRADER
(
   PARTY_ID             bigint not null,
   TRADER_STATUS        varchar(20),
   STATUS_CHANGE_DATE   datetime,
   primary key (PARTY_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: TRADE_ORDER                                           */
/*==============================================================*/
create table TRADE_ORDER
(
   ORDER_ID             bigint not null,
   TRADE_ORDER_TYPE_CD  varchar(20) not null,
   TRADE_TYPE_CD        varchar(20) not null,
   TRADE_SIDE_CD        varchar(10) not null,
   PRICE_LIMIT          decimal(31,11),
   PRICE_AVG            decimal(31,11),
   AMOUNT_OPEN          decimal(31,11),
   AMOUNT_FILLED        decimal(31,11),
   primary key (ORDER_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: TRADE_ORDER_TYPE                                      */
/*==============================================================*/
create table TRADE_ORDER_TYPE
(
   TRADE_ORDER_TYPE_CD  varchar(20) not null,
   TRADE_ORDER_TYPE_DESC varchar(200),
   primary key (TRADE_ORDER_TYPE_CD)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: TRADE_SIDE                                            */
/*==============================================================*/
create table TRADE_SIDE
(
   TRADE_SIDE_CD        varchar(10) not null,
   TRADE_SIDE_DESC      varchar(100),
   primary key (TRADE_SIDE_CD)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: TRADE_TYPE                                            */
/*==============================================================*/
create table TRADE_TYPE
(
   TRADE_TYPE_CD        varchar(20) not null,
   TRADE_TYPE_DESC      varchar(200),
   primary key (TRADE_TYPE_CD)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: TRANSACTION                                           */
/*==============================================================*/
create table TRANSACTION
(
   TRANSACTION_ID       bigint not null AUTO_INCREMENT,
   ACCOUNT_ID           bigint not null,
   TRAN_SOURCE_ID       int not null,
   TRANSACTION_CLASS_CD varchar(20) not null,
   ORDER_ID             bigint,
   SOURCE_TRAN_ID       varchar(40),
   SOURCE_TRAN_DT       datetime,
   TRAN_STATUS          varchar(20),
   STATUS_CHANGE_DATE   datetime,
   LAST_UPDATE          timestamp not null default current_timestamp on update current_timestamp,
   primary key (TRANSACTION_ID),
   index (ACCOUNT_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: TRANSACTION_CLASS                                     */
/*==============================================================*/
create table TRANSACTION_CLASS
(
   TRANSACTION_CLASS_CD varchar(20) not null,
   TRANSACTION_CLASS_DESC varchar(200),
   primary key (TRANSACTION_CLASS_CD)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: TRANSACTION_FUNCTION                                  */
/*==============================================================*/
create table TRANSACTION_FUNCTION
(
   TRAN_FUNCTION_ID     int not null,
   TRAN_FUNCTION_NAME   varchar(40),
   TRAN_FUNCTION_DESC   varchar(200),
   CREATE_DATE          datetime,
   primary key (TRAN_FUNCTION_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: TRANSACTION_QUEUE                                     */
/*==============================================================*/
create table TRANSACTION_QUEUE
(
   QUEUE_ID             int not null,
   TRAN_FUNCTION_ID     int not null,
   QUEUE_NAME           varchar(40),
   QUEUE_TYPE           varchar(20),
   QUEUE_DESC           varchar(200),
   QUEUE_STATUS         varchar(20),
   STATUS_CHANGE_DATE   datetime,
   primary key (QUEUE_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: TRANSACTION_SOURCE                                    */
/*==============================================================*/
create table TRANSACTION_SOURCE
(
   TRAN_SOURCE_ID       int not null,
   TRAN_SOURCE_NAME     varchar(40),
   TRAN_SOURCE_DESC     varchar(200),
   REGISTER_DATE        date,
   REGISTER_STATUS      varchar(20),
   STATUS_CHANGE_DATE   datetime,
   primary key (TRAN_SOURCE_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: TRAN_AUTHORIZATION                                    */
/*==============================================================*/
create table TRAN_AUTHORIZATION
(
   TRAN_SOURCE_ID       int not null,
   TRAN_FUNCTION_ID     int not null,
   GRANT_DATE           datetime,
   REVOKE_DATE          datetime,
   primary key (TRAN_SOURCE_ID, TRAN_FUNCTION_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: POSITION_VALUATION_HIST                                    */
/*==============================================================*/
create table POSITION_VALUATION_HIST (
    POSITION_ID bigint not null,
    POS_VAL_TYPE_ID bigint not null,
    AS_OF_DATE date not null,
	CURRENCY_CD char(3) not null,
    VALUE_AMOUNT decimal(31, 11) not null,
	MARKET_PRICE decimal(31, 11) not null,
    ADJUST_TS datetime,
    LAST_UPDATE timestamp not null default current_timestamp on update current_timestamp,
    primary key (POSITION_ID, POS_VAL_TYPE_ID, AS_OF_DATE)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: POSITION_VALUATION_TYPE                               */
/*==============================================================*/
create table POSITION_VALUATION_TYPE (
    TYPE_ID bigint not null,
    TYPE_NAME varchar(30) not null,
    TYPE_DESC varchar(200),
    primary key (TYPE_ID)
)DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: ACCOUNT_VALUATION_HIST                                    */
/*==============================================================*/
create table ACCOUNT_VALUATION_HIST (
    ACCOUNT_ID bigint not null,
    ACC_VAL_TYPE_ID bigint not null,
    AS_OF_DATE date not null,
    CURRENCY_CD char(3) not null,
    VALUE_AMOUNT decimal(31, 11) not null,
    ADJUST_TS datetime,
    LAST_UPDATE timestamp not null default current_timestamp on update current_timestamp,
    primary key (ACCOUNT_ID, ACC_VAL_TYPE_ID, AS_OF_DATE)
)DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: ACCOUNT_VALUATION_TYPE                                */
/*==============================================================*/
create table ACCOUNT_VALUATION_TYPE (
    TYPE_ID bigint not null,
    TYPE_NAME varchar(30),
    TYPE_DESC varchar(200),
    primary key (TYPE_ID)
)DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CARRYING_VALUE_HIST                                   */
/*==============================================================*/
create table CARRYING_VALUE_HIST (
    POSITION_ID bigint not null,
    CAR_VAL_TYPE_ID bigint not null,
    AS_OF_DATE date not null,
	ACCOUNT_ID bigint not null,
    CURRENCY_CD char(3) not null,
    VALUE_AMOUNT decimal(31, 11) not null,
    ADJUST_TS datetime,
    LAST_UPDATE timestamp not null default current_timestamp on update current_timestamp,
    primary key (POSITION_ID, CAR_VAL_TYPE_ID, AS_OF_DATE)
)DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: CARRYING_VALUE_TYPE                                   */
/*==============================================================*/
create table CARRYING_VALUE_TYPE (
    TYPE_ID bigint not null,
    TYPE_NAME varchar(30),
    TYPE_DESC varchar(200),
    primary key (TYPE_ID)
)DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: FEE                                                   */
/*==============================================================*/
create table FEE (
   FEE_ID               bigint not null AUTO_INCREMENT,
   ACCOUNT_ID           bigint not null,
   RATE_TYPE_ID         bigint not null,
   START_DATE           datetime not null,
   END_DATE             datetime,
   TRADE_SIDE_CD        varchar(10),
   SECURITY_ID          bigint,
   RATES                decimal(31, 11),
   REMARK               varchar(200),
   LAST_UPDATE          timestamp not null default current_timestamp on update current_timestamp,
   primary key (FEE_ID),
   index (ACCOUNT_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: RATE_TYPE                                             */
/*==============================================================*/
create table RATE_TYPE
(
   RATE_TYPE_ID          bigint not null,
   RATE_TYPE_NAME        varchar(40),
   RATE_TYPE_DESC        varchar(40),
   EXCHANGE_CD           varchar(4),
   BASEDAYS             INT,
   LAST_UPDATE          timestamp not null default current_timestamp on update current_timestamp,
   primary key (RATE_TYPE_ID)
) DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: SYSTEM_ID_MAPPING                                     */
/*==============================================================*/
create table SYSTEM_ID_MAPPING
(
   PMS_ID bigint not null,
   OTHER_SYSTEM_ID varchar(40) not null,
   ID_NAME varchar(40) not null,
   OTHER_SYSTEM_NAME varchar(40) not null,
   LAST_UPDATE timestamp not null default current_timestamp on update current_timestamp
)DEFAULT CHARACTER SET=utf8;

/*==============================================================*/
/* Table: SOURCE_TRANSACTION
/* Used to store all transaction records, when encounter system
   problems can redo these transactions to recover system data  */
/*==============================================================*/
create table SOURCE_TRANSACTION
(
    ID bigint not null AUTO_INCREMENT,
	ACCOUNT_ID bigint not null,
	SECURITY_ID bigint not null,
	SOURCE_TRANSACTION_ID varchar(40) not null,
	TRADER_ID bigint,
	BROKER_ID bigint,
	EXECUTION_DATE datetime,
	SETTLEMENT_DATE date,
	TRADE_SIDE_CD varchar(10) not null,
	PRICE decimal(31,11) not null,
	AMOUNT decimal(31,11) not null,
	TRANSACTION_SOURCE_ID int not null,
	TRANSACTION_CLASS_CD varchar(20) not null,
	LAST_UPDATE timestamp not null default current_timestamp on update current_timestamp,
	primary key (ID)
)DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: POSITION_YIELD                                        */
/*==============================================================*/
create table POSITION_YIELD
(
   ID                       bigint not null AUTO_INCREMENT comment "本表主键",
   AS_OF_DATE               date not null comment "日期",
   POSITION_ID              bigint not null comment "持仓ID",
   ACCOUNT_ID               bigint not null comment "账户代码",
   SUB_ACCOUNT_ID           varchar(40) comment "子账户代码",
   SECURITY_ID              bigint not null comment "证券代码",
   CURRENCY_TYPE_CD         char(1) comment "L:原币,B:本位币,A本位币和原币一致",
   CURRENCY_CD              char(3) comment "交易币种",
   POSITION_CARRYING_VALUE  decimal(31,11) comment "position成本",
   SECURITY_CARRYING_VALUE  decimal(31,11) comment "position基金成本",
   DAILY_INTEREST_CAMT      decimal(31,11) default 0 not null comment "利息收入",
   DIVIDEND_CAMT            decimal(31,11) default 0 not null comment "红利收入",
   INCREMENT_CAMT           decimal(31,11) default 0 not null comment "估值增值",
   PRICE_DIFF_EARN_CAMT     decimal(31,11) default 0 not null comment "差价收入",
   BEGIN_VALUE_CAMT         decimal(31,11) default 0 not null comment "起初市值,昨日收盘市值",
   END_VALUE_CAMT           decimal(31,11) default 0 not null comment "期末市值,当日收盘市值",
   IN_CAMT                  decimal(31,11) default 0 not null comment "当日流入金额",
   OUT_CAMT                 decimal(31,11) default 0 not null comment "当日流出金额",
   EARN_LOSS_CAMT           decimal(31,11) default 0 not null comment "当日盈亏=期末市值+当日流出金额-起初市值-当日流入金额",
   LAST_CHANGE_USER_ID      varchar(40) comment "最后修改用户",
   LAST_UPDATE              timestamp not null default current_timestamp on update current_timestamp comment "最后修改日期",
   IS_LOCKED                char(1) not null comment "记录锁定标记",
   primary key (ID)
) DEFAULT CHARACTER SET=utf8;
/*==============================================================*/
/* Table: ACCOUNT_YIELD                                         */
/*==============================================================*/
create table ACCOUNT_YIELD
(
   ID                       bigint not null AUTO_INCREMENT comment "本表主键",
   AS_OF_DATE               date not null comment "日期",
   ACCOUNT_ID               bigint not null comment "账户代码",
   ACCOUNT_YIELD_ITEM_ID    bigint not null comment "账户收益科目：目前只有净值",
   CURRENCY_TYPE_CD         char(1) comment "L:原币,B:本位币,A本位币和原币一致",
   BEGIN_VALUE_CAMT         decimal(31,11) default 0 not null comment "起初市值,昨日收盘市值",
   END_VALUE_CAMT           decimal(31,11) default 0 not null comment "期末市值,当日收盘市值",
   IN_CAMT                  decimal(31,11) default 0 not null comment "当日流入金额",
   OUT_CAMT                 decimal(31,11) default 0 not null comment "当日流出金额",
   EARN_LOSS_CAMT           decimal(31,11) default 0 not null comment "当日盈亏=期末市值+当日流出金额-起初市值-当日流入金额",
   YIELD_RATIO              decimal(31,11) default 0 not null comment "收益率",
   LAST_CHG_USER_ID         varchar(40) comment "最后修改用户",
   LAST_UPDATE              timestamp not null default current_timestamp on update current_timestamp comment "最后修改日期",
   IS_LOCKED                char(1) comment "记录锁定标记",
   primary key (ID)
) DEFAULT CHARACTER SET=utf8;
/*==============================================================*/
/* Table: ACCOUNT_YIELD_ITEM                                            */
/*==============================================================*/
create table ACCOUNT_YIELD_ITEM
(
   ACCOUNT_YIELD_ITEM_ID            bigint not null,
   ACCOUNT_YIELD_ITEM_DESC          varchar(20),
   primary key (ACCOUNT_YIELD_ITEM_ID)
) DEFAULT CHARACTER SET=utf8;

/*
 *  ACCOUNT_VALUATION_INIT
 */
create table ACCOUNT_VALUATION_INIT
(
   ACCOUNT_ID     		bigint not null PRIMARY KEY,
   TYPE_ID              bigint not null,
   VALUE_AMOUNT         decimal(31, 11),
   LAST_UPDATE          timestamp not null default current_timestamp on update current_timestamp
)DEFAULT CHARACTER SET=utf8;

/*
 *  POSITION_INIT
 */
create table POSITION_INIT
(
   POSITION_ID          bigint not null PRIMARY KEY,
   QUANTITY             decimal(31, 11),
   CARRYING_VALUE       decimal(31, 11),
   LAST_UPDATE          timestamp not null default current_timestamp on update current_timestamp
)DEFAULT CHARACTER SET=utf8;

/*
 *  MARKET_DATA
 */
create table MARKET_DATA
(
   SECURITY_ID          bigint not null PRIMARY KEY,
   AS_OF_DATE           date,
   TIMESTAMP            datetime,
   PRICE                decimal(31, 11),
   PREVIOUS_PRICE       decimal(31, 11),
   RECEIVED_TIME        datetime,
   SOURCE               varchar(30),
   LAST_UPDATE		timestamp not null default current_timestamp on update current_timestamp
)DEFAULT CHARACTER SET=utf8;

alter table ACCOUNT_VALUATION_INIT add constraint fk_AccountId foreign key (ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table ACCOUNT_VALUATION_INIT add constraint fk_AccValType foreign key (TYPE_ID)
      references ACCOUNT_VALUATION_TYPE(TYPE_ID) on delete restrict on update restrict;

alter table POSITION_INIT add constraint fk_PositionId foreign key (POSITION_ID)
      references POSITION (POSITION_ID) on delete restrict on update restrict;

alter table ACCOUNT add constraint FK_ACCOUNT_ACCOUNT_CLASS foreign key (ACCOUNT_CLASS_CD)
      references ACCOUNT_CLASS (ACCOUNT_CLASS_CD) on delete restrict on update restrict;

alter table ACCOUNT add constraint FK_ACCOUNT_ACCOUNT_TYPE foreign key (ACCOUNT_TYPE_ID)
      references ACCOUNT_TYPE (ACCOUNT_TYPE_ID) on delete restrict on update restrict;

alter table ACCOUNT add constraint FK_PARENT_ACCOUNT foreign key (PARENT_ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table ACCOUNT_ASSOCIATION add constraint FK_ACCOUNT_ASSOCIATION foreign key (PRINCIPLE_ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table ACCOUNT_ASSOCIATION add constraint FK_ACCOUNT_ASSOCIATION2 foreign key (ASSOCIATE_ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table ACCOUNT_GROUP add constraint FK_ACCOUNT_GROUP_TYPE foreign key (ACCT_GROUP_TYPE_CD)
      references ACCT_GROUP_TYPE (ACCT_GROUP_TYPE_CD) on delete restrict on update restrict;

alter table ACCOUNT_GROUP add constraint FK_ACCOUT_GROUP_PM foreign key (PARTY_ID)
      references PORTFOLIO_MANAGER (PARTY_ID) on delete restrict on update restrict;

alter table BROKER_ASSET_CLASS add constraint FK_BROKER_ASSET_CLASS2 foreign key (PARTY_ID)
      references BROKER (PARTY_ID) on delete restrict on update restrict;

alter table BROKER_EXCHANGE add constraint FK_BROKER_EXCHANGE2 foreign key (PARTY_ID)
      references BROKER (PARTY_ID) on delete restrict on update restrict;

alter table CAPITAL_POOL add constraint FK_CAPITAL_POOL foreign key (BANK_ACCOUNT_ID)
      references BANK_ACCOUNT (BANK_ACCOUNT_ID) on delete restrict on update restrict;

alter table CAPITAL_POOL add constraint FK_CAPITAL_POOL2 foreign key (ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table CASH_ORDER add constraint FK_CAHS_ORDER_TYPE foreign key (CASH_ORDER_TYPE_CD)
      references CASH_ORDER_TYPE (CASH_ORDER_TYPE_CD) on delete restrict on update restrict;

alter table CASH_ORDER add constraint FK_CASH_ORDER_INT_ACCT foreign key (ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table CASH_ORDER add constraint FK_CASH_ORDER_ORDER foreign key (ORDER_ID)
      references ORDERS (ORDER_ID) on delete restrict on update restrict;

alter table CASH_TRANSACTION add constraint FK_CASH_TRANACTION_REASON foreign key (CASH_TRAN_REASON_CD)
      references CASH_TRAN_REASON (CASH_TRAN_REASON_CD) on delete restrict on update restrict;

alter table CASH_TRANSACTION add constraint FK_CASH_TRANSACTION_METHOD foreign key (CASH_TRAN_METH_CD)
      references CASH_TRAN_METHOD (CASH_TRAN_METH_CD) on delete restrict on update restrict;

alter table CASH_TRANSACTION add constraint FK_CASH_TRANSACTION_TRAN foreign key (TRANSACTION_ID)
      references TRANSACTION (TRANSACTION_ID) on delete restrict on update restrict;

alter table CASH_TRANSACTION add constraint FK_CASH_TRANSACTION_TYPE foreign key (CASH_TRAN_TYPE_CD)
      references CASH_TRAN_TYPE (CASH_TRAN_TYPE_CD) on delete restrict on update restrict;

alter table CASH_TRANSACTION add constraint FK_INTERNAL_ACCOUNT foreign key (INT_ACCT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table CORPACT_ORDER add constraint FK_CORPACT_ORDER_ORDER foreign key (ORDER_ID)
      references ORDERS (ORDER_ID) on delete restrict on update restrict;

alter table CORPACT_ORDER add constraint FK_CORPACT_TYPE foreign key (CORP_ACT_TYPE_CD)
      references CORP_ACT_TYPE (CORP_ACT_TYPE_CD) on delete restrict on update restrict;

alter table CORP_ACT_TRANSACTION add constraint FK_ACTION_TYPE foreign key (CORP_ACT_TYPE_CD)
      references CORP_ACT_TYPE (CORP_ACT_TYPE_CD) on delete restrict on update restrict;

alter table CORP_ACT_TRANSACTION add constraint FK_ACT_TRANSACTION_TRAN foreign key (TRANSACTION_ID)
      references TRANSACTION (TRANSACTION_ID) on delete restrict on update restrict;

alter table GROUP_ACCT add constraint FK_GROUP_ACCT foreign key (ACCT_GROUP_ID)
      references ACCOUNT_GROUP (ACCT_GROUP_ID) on delete restrict on update restrict;

alter table GROUP_ACCT add constraint FK_GROUP_ACCT2 foreign key (ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table LEDGER add constraint FK_LEDGER_LEDGER_TYPE foreign key (LEDGER_TYPE_ID)
      references LEDGER_TYPE (LEDGER_TYPE_ID) on delete restrict on update restrict;

alter table ORDERS add constraint FK_ORDER_ORDER_CLASS foreign key (ORDER_CLASS_CD)
      references ORDER_CLASS (ORDER_CLASS_CD) on delete restrict on update restrict;

alter table ORDERS add constraint FK_ORDER_POSITION foreign key (POSITION_ID)
      references POSITION (POSITION_ID) on delete restrict on update restrict;

alter table ORDERS add constraint FK_ORDER_SOURCE foreign key (TRAN_SOURCE_ID)
      references TRANSACTION_SOURCE (TRAN_SOURCE_ID) on delete restrict on update restrict;

alter table POSITION add constraint FK_POSITION_ACCOUNT foreign key (ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table POSITION add constraint FK_POSITION_POSITION_CLASS foreign key (POSITION_CLASS_CD)
      references POSITION_CLASS (POSITION_CLASS_CD) on delete restrict on update restrict;

alter table POSITION add constraint FK_POSITION_LEDGER foreign key (LEDGER_ID)
      references LEDGER (LEDGER_ID) on delete restrict on update restrict;

alter table POSITION_HIST add constraint FK_POSITION_HIST_POSITION foreign key (POSITION_ID)
      references POSITION (POSITION_ID) on delete restrict on update restrict;

alter table SEC_TRANSACTION add constraint FK_SEC_TRANSACTION_TRAN foreign key (TRANSACTION_ID)
      references TRANSACTION (TRANSACTION_ID) on delete restrict on update restrict;

alter table SEC_TRANSACTION add constraint FK_TRANSACTION_SIDE foreign key (TRADE_SIDE_CD)
      references TRADE_SIDE (TRADE_SIDE_CD) on delete restrict on update restrict;

alter table SEC_TRANSACTION add constraint FK_TRAN_BROKER foreign key (BROKER_ID)
      references BROKER (PARTY_ID) on delete restrict on update restrict;

alter table SEC_TRANSACTION add constraint FK_TRAN_TRADER foreign key (TRADER_ID)
      references TRADER (PARTY_ID) on delete restrict on update restrict;

alter table TRADE_ORDER add constraint FK_ORDER_SIDE foreign key (TRADE_SIDE_CD)
      references TRADE_SIDE (TRADE_SIDE_CD) on delete restrict on update restrict;

alter table TRADE_ORDER add constraint FK_ORDER_TRADE_TYPE foreign key (TRADE_TYPE_CD)
      references TRADE_TYPE (TRADE_TYPE_CD) on delete restrict on update restrict;

alter table TRADE_ORDER add constraint FK_TRADE_ORDER_ORDER foreign key (ORDER_ID)
      references ORDERS (ORDER_ID) on delete restrict on update restrict;

alter table TRADE_ORDER add constraint FK_TRADE_ORDER_TYPE foreign key (TRADE_ORDER_TYPE_CD)
      references TRADE_ORDER_TYPE (TRADE_ORDER_TYPE_CD) on delete restrict on update restrict;

alter table TRANSACTION add constraint FK_TRANSACTION_ACCOUNT foreign key (ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table TRANSACTION add constraint FK_TRAN_SOURCE foreign key (TRAN_SOURCE_ID)
      references TRANSACTION_SOURCE (TRAN_SOURCE_ID) on delete restrict on update restrict;

alter table TRANSACTION add constraint FK_TRAN_TRAN_CLASS foreign key (TRANSACTION_CLASS_CD)
      references TRANSACTION_CLASS (TRANSACTION_CLASS_CD) on delete restrict on update restrict;

alter table TRANSACTION_QUEUE add constraint FK_QUEUE_TRAN_FUNCTION foreign key (TRAN_FUNCTION_ID)
      references TRANSACTION_FUNCTION (TRAN_FUNCTION_ID) on delete restrict on update restrict;

alter table TRAN_AUTHORIZATION add constraint FK_TRAN_AUTHORIZATION foreign key (TRAN_SOURCE_ID)
      references TRANSACTION_SOURCE (TRAN_SOURCE_ID) on delete restrict on update restrict;

alter table TRAN_AUTHORIZATION add constraint FK_TRAN_AUTHORIZATION2 foreign key (TRAN_FUNCTION_ID)
      references TRANSACTION_FUNCTION (TRAN_FUNCTION_ID) on delete restrict on update restrict;

alter table POSITION_VALUATION_HIST add constraint FK_POSITION_VALUATION_HIST_POSITION foreign key (POSITION_ID)
      references POSITION (POSITION_ID) on delete restrict on update restrict;

alter table POSITION_VALUATION_HIST add constraint FK_POSITION_VALUATION_HIST_POSITION_VALUATION_TYPE foreign key (POS_VAL_TYPE_ID)
      references POSITION_VALUATION_TYPE (TYPE_ID) on delete restrict on update restrict;

alter table ACCOUNT_VALUATION_HIST add constraint FK_ACCOUNT_VALUATION_HIST_ACCOUNT foreign key (ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table ACCOUNT_VALUATION_HIST add constraint FK_ACCOUNT_VALUATION_HIST_ACCOUNT_VALUATION_TYPE foreign key (ACC_VAL_TYPE_ID)
      references ACCOUNT_VALUATION_TYPE (TYPE_ID) on delete restrict on update restrict;

alter table CARRYING_VALUE_HIST add constraint FK_CARRYING_VALUE_HIST_POSITION foreign key (POSITION_ID)
      references POSITION (POSITION_ID) on delete restrict on update restrict;

alter table CARRYING_VALUE_HIST add constraint FK_CARRYING_VALUE_HIST_CARRYING_VALUE_TYPE foreign key (CAR_VAL_TYPE_ID)
      references CARRYING_VALUE_TYPE (TYPE_ID) on delete restrict on update restrict;

alter table FEE add constraint FK_FEE_RATE_TYPE foreign key (RATE_TYPE_ID)
      references RATE_TYPE (RATE_TYPE_ID) on delete restrict on update restrict;

alter table FEE add constraint FK_FEE_TRADE_SIDE foreign key (TRADE_SIDE_CD)
      references TRADE_SIDE (TRADE_SIDE_CD) on delete restrict on update restrict;

alter table SOURCE_TRANSACTION add constraint FK_TSOURCE_TRANSACTION_ACCOUNT foreign key (ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table SOURCE_TRANSACTION add constraint FK_SOURCE_TRANSACTION_TRADER foreign key (TRADER_ID)
      references TRADER (PARTY_ID) on delete restrict on update restrict;

alter table SOURCE_TRANSACTION add constraint FK_SOURCE_TRANSACTION_BROKER foreign key (BROKER_ID)
      references BROKER (PARTY_ID) on delete restrict on update restrict;

alter table SOURCE_TRANSACTION add constraint FK_SOURCE_TRANSACTION_TRADE_SIDE foreign key (TRADE_SIDE_CD)
      references TRADE_SIDE (TRADE_SIDE_CD) on delete restrict on update restrict;

alter table SOURCE_TRANSACTION add constraint FK_SOURCE_TRANSACTION_TRAN_SOURCE foreign key (TRANSACTION_SOURCE_ID)
      references TRANSACTION_SOURCE (TRAN_SOURCE_ID) on delete restrict on update restrict;

alter table SOURCE_TRANSACTION add constraint FK_SOURCE_TRANSACTION_TRAN_CLASS foreign key (TRANSACTION_CLASS_CD)
      references TRANSACTION_CLASS (TRANSACTION_CLASS_CD) on delete restrict on update restrict;

alter table POSITION_YIELD add constraint FK_POSITION_YIELD_ACCOUNT foreign key (ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table POSITION_YIELD add constraint FK_POSITION_YIELD_POSITION foreign key (POSITION_ID)
      references POSITION (POSITION_ID) on delete restrict on update restrict;

alter table ACCOUNT_YIELD add constraint FK_ACCOUNT_YIELD_ACCOUNT foreign key (ACCOUNT_ID)
      references ACCOUNT (ACCOUNT_ID) on delete restrict on update restrict;

alter table ACCOUNT_YIELD add constraint FK_ACCOUNT_YIELD_ACCOUNT_YIELD_ITEM foreign key (ACCOUNT_YIELD_ITEM_ID)
      references ACCOUNT_YIELD_ITEM (ACCOUNT_YIELD_ITEM_ID) on delete restrict on update restrict;
