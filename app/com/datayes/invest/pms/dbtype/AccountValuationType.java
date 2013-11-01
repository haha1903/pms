package com.datayes.invest.pms.dbtype;

public enum AccountValuationType implements DbValue<Long> {

    SECURITY(1L),                               // 股票总资产
    CASH(2L),                                   // 现金总资产
    COMMISSION(3L),                             // 总应付佣金
    FUTURE_ASSET(4L),                           // 期货总资产
    FUTURE_VALUE(5L),                           // 期货市值
    PAYABLE_SETTLEMENT(6L),                     // 总应付清算款
    RECEIVABLE_SETTLEMENT(7L),                  // 总应收清算款
    ASSET(8L),                                  // 总资产
    LIABILITY(9L),                              // 总负债
    NET_WORTH(10L),                             // 净值
    UNIT_NET(11L),                              // 单位净值
    SHARE(12L),                                 // 份额
    DAILY_RETURN(13L),                          // 每日回报率
    PROFIT_LOSS(14L),                           // 每日总盈亏
    FUTURE_LONG_VALUE(15L),                     // 多头期货总资产
    FUTURE_SHORT_VALUE(16L),                    // 空头期货总资产
    REPO_PRINCIPAL_ASSET_VALUE(17L),            // 回购应收本金
    REPO_PRINCIPAL_LIABILITY_VALUE(18L),        // 回购应付本金
    REPO_INTEREST_ASSET_VALUE(19L),             // 回购应收利息
    REPO_INTEREST_LIABILITY_VALUE(20L);         // 回购应付利息
    
    private final Long dbValue;
    
    private AccountValuationType(Long dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public Long getDbValue() {
        return dbValue;
    }
    
    public static AccountValuationType fromDbValue(Long v) {
        for (AccountValuationType t : values()) {
            if (t.getDbValue().equals(v)) {
                return t;
            }
        }
        return null;
    }
}
