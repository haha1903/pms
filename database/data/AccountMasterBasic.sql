INSERT INTO `VERSION` (NAME, VERSION, DESCRIPTION) VALUES ('ACCOUNT_MASTER', '1.2', '');

INSERT INTO `ACCOUNT_CLASS` VALUES ('CASH', '');
INSERT INTO `ACCOUNT_CLASS` VALUES ('CREDIT', '');
INSERT INTO `ACCOUNT_CLASS` VALUES ('INVESTMENT', '');

INSERT INTO `ACCOUNT_TYPE` VALUES (1, 'EQUITY', '权益');
INSERT INTO `ACCOUNT_TYPE` VALUES (2, 'FIXED_INCOME', '固定收益');
INSERT INTO `ACCOUNT_TYPE` VALUES (3, 'ABSOLUTE_INCOME', '绝对收益');
INSERT INTO `ACCOUNT_TYPE` VALUES (4, 'CASH_INCOME', '现金收益');


INSERT INTO `LEDGER_TYPE` VALUES ('1', 'Asset', null);
INSERT INTO `LEDGER_TYPE` VALUES ('2', 'Liability', null);
INSERT INTO `LEDGER_TYPE` VALUES ('3', '共同类', null);
INSERT INTO `LEDGER_TYPE` VALUES ('4', '所有者权益', null);

INSERT INTO `LEDGER` VALUES ('1', '1', 'L', '股票投资-流通股票', null, null, null, null);
INSERT INTO `LEDGER` VALUES ('2', '1', 'L', '现金', null, null, null, null);
INSERT INTO `LEDGER` VALUES ('3', '1', 'S', '佣金', null, null, null, null);
INSERT INTO `LEDGER` VALUES ('4', '1', 'L', '期货保证金', null, null, null, null);
INSERT INTO `LEDGER` VALUES ('5', '1', 'L', '多头股指期货', null, null, null, null);
INSERT INTO `LEDGER` VALUES ('6', '1', 'S', '空头股指期货', null, null, null, null);
insert into `LEDGER` values (7, 1, 'S', '应付清算款', null, null, null, null);
insert into `LEDGER` values (8, 1, 'L', '应收清算款', null, null, null, null);
insert into `LEDGER` values (9, 1, 'L', '份额', null, null, null, null);
INSERT INTO `LEDGER` VALUES (10, '1', 'S', '应付回购资金', null, null, null, null);
INSERT INTO `LEDGER` VALUES (11, '1', 'L', '应收回购资金', null, null, null, null);
INSERT INTO `LEDGER` VALUES (12, '1', 'S', '应付回购利息', null, null, null, null);
INSERT INTO `LEDGER` VALUES (13, '1', 'L', '应收回购利息', null, null, null, null);
INSERT INTO `LEDGER` VALUES (14, '1', 'L', '卖出逆回购', null, null, null, null);
INSERT INTO `LEDGER` VALUES (15, '1', 'L', '买入逆回购', null, null, null, null);

INSERT INTO `POSITION_CLASS` VALUES ('CASH', 'CASH');
INSERT INTO `POSITION_CLASS` VALUES ('SECURITY', 'SECURITY');

INSERT INTO `TRANSACTION_CLASS` VALUES ('CASH', 'Cash transactions');
INSERT INTO `TRANSACTION_CLASS` VALUES ('CORPACT', 'Corporate action triggered transactions');
INSERT INTO `TRANSACTION_CLASS` VALUES ('TRADE', 'Security trade');

INSERT INTO `TRANSACTION_SOURCE` VALUES ('1', 'OMS', null, null, null, null);
INSERT INTO `TRANSACTION_SOURCE` VALUES ('2', 'PMS', null, null, null, null);

INSERT INTO `TRADE_SIDE` VALUES ('BUY', 'BUY');
INSERT INTO `TRADE_SIDE` VALUES ('SELL', 'SELL');
INSERT INTO `TRADE_SIDE` VALUES ('SHORT', 'SHORT');
INSERT INTO `TRADE_SIDE` VALUES ('COVER', 'COVER');

INSERT INTO POSITION_VALUATION_TYPE VALUES ('1', 'MARKET', 'MARKET');

INSERT INTO CARRYING_VALUE_TYPE VALUES('1', 'TEST', 'TEST');

INSERT INTO RATE_TYPE VALUES ('1', 'Commission', '佣金', 'XSHE', null, now());
INSERT INTO RATE_TYPE VALUES ('2', 'Stamp', '印花税', 'XSHE', null, now());
INSERT INTO RATE_TYPE VALUES ('3', 'FutureCommission', '期货佣金', null, null, now());
INSERT INTO RATE_TYPE VALUES ('4', 'FutureTransactionFee', '期货交易费', null, null, now());
INSERT INTO RATE_TYPE VALUES ('5', 'FutureDeliveryCharges', '期货交割费', null, null, now());
INSERT INTO RATE_TYPE VALUES ('6', 'FutureMinMarginRatio', '期货最低保证金', null, null, now());
INSERT INTO RATE_TYPE VALUES ('7', 'RepoCommission', '逆回购佣金', null, null, now());

INSERT INTO `CASH_TRAN_METHOD` VALUES ('TRANSFER', '');

INSERT INTO `CASH_TRAN_REASON` VALUES ('ACCTFEE', '');
INSERT INTO `CASH_TRAN_REASON` VALUES ('ACCTINTEREST', '');
INSERT INTO `CASH_TRAN_REASON` VALUES ('CAPMOVE', '');
INSERT INTO `CASH_TRAN_REASON` VALUES ('CORPACTION', '');
INSERT INTO `CASH_TRAN_REASON` VALUES ('SECTRADE', '');

INSERT INTO `CASH_TRAN_TYPE` VALUES ('CREDIT', '');
INSERT INTO `CASH_TRAN_TYPE` VALUES ('DEBIT', '');

INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(1, "Security", "股票总资产");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(2, "Cash", "现金总资产");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(3, "Commission", "总应付佣金");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(4, "FutureAsset", "期货总资产");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(5, "FutureValue", "期货市值");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(6, "PayableSettlementCash", "总应付清算款");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(7, "ReceivableSettlementCash", "总应收清算款");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(8, "Asset", "总资产");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(9, "Liability", "总负债");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(10, "NetWorth", "净值");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(11, "UnitNet", "单位净值");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(12, "Share", "份额");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(13, "DailyReturn", "每日回报率");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(14, "DailyPnL", "每日总盈亏");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(15, "FutureLongValue", "多头期货总资产");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(16, "FutureShortValue", "空头期货总资产");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(17, "RepoPrincipalAssetValue", "回购应收本金");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(18, "RepoPrincipalLiabilityValue", "回购应付本金");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(19, "RepoInterestAssetValue", "回购应收利息");
INSERT INTO ACCOUNT_VALUATION_TYPE VALUES(20, "RepoInterestLiabilityValue", "回购应付利息");
