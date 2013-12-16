package com.datayes.invest.pms.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.datayes.invest.pms.dao.account.*;
import com.datayes.invest.pms.entity.account.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datayes.invest.pms.entity.account.Account;
import com.datayes.invest.pms.entity.account.Position;
import com.datayes.invest.pms.entity.account.PositionInit;
import com.google.inject.Inject;

public class AccountDeleteHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDeleteHelper.class);

    @Inject
    private AccountDao accountDao;

    @Inject
    private AccountValuationInitDao accountValuationInitDao;

    @Inject
    private AccountValuationHistDao accountValuationHistDao;

    @Inject
    private CarryingValueHistDao carryingValueHistDao;

    @Inject
    private FeeDao feeDao;

    @Inject
    private OrderDao orderDao;

    @Inject
    private OrderBasketDao orderBasketDao;

    @Inject
    private PositionDao positionDao;

    @Inject
    private PositionInitDao positionInitDao;

    @Inject
    private PositionHistDao positionHistDao;

    @Inject
    private PositionValuationHistDao positionValuationHistDao;
    
    @Inject
    private PositionYieldDao positionYieldDao;

    @Inject
    private SourceTransactionDao sourceTransactionDao;


    @Inject
    private TransactionDao transactionDao;

    public void deleteAccount(Long accountId) {
        Account account = accountDao.findById(accountId);
        if (account == null) {
            throw new RuntimeException("Failed to load account by id " + accountId);
        }
        deleteAccount(account);
    }

    public void deleteAccount(Account account) {
        Long accountId = account.getId();
        LOGGER.info("Start to deleting account #" + accountId);
        deleteTransactions(accountId);
        deleteSourceTransactions(accountId);
        deleteOrdersAndOrderBaskets(accountId);
        deleteCarryingValueHists(accountId);
        deletePositionYield(accountId);
        deletePositions(accountId);
        deleteAccountValuationHists(accountId);
        deleteAccountValuationInits(accountId);
        deleteFees(accountId);
        accountDao.delete(account);
        LOGGER.info("Account #" + accountId + " deleted");
    }

    private void deleteOrdersAndOrderBaskets(Long accountId) {
        // delete orders
        List<Order> orders = orderDao.findByAccountId(accountId);
        Set<Long> basketIds = new HashSet<>();
        for (Order ord : orders) {
            basketIds.add(ord.getBasketId());
            orderDao.delete(ord);
        }

        // delete order baskets

    }

    private void deletePositionYield(Long accountId) {
        positionYieldDao.deleteByAccountId(accountId);
    }

    private void deleteSourceTransactions(Long accountId) {
        sourceTransactionDao.deleteByAccountId(accountId);
    }

    private void deleteAccountValuationInits(Long accountId) {
        accountValuationInitDao.deleteByAccountId(accountId);
    }

    private void deleteFees(Long accountId) {
        feeDao.deleteByAccountId(accountId);
    }

    private void deleteTransactions(Long accountId) {
        transactionDao.deleteByAccountId(accountId);
    }

    private void deleteCarryingValueHists(Long accountId) {
        carryingValueHistDao.deleteByAccountId(accountId);
    }

    private void deletePositions(Long accountId) {
        List<Position> positions = positionDao.findByAccountId(accountId);
        for (Position p : positions) {
            deletePosition(p);
        }
    }

    private void deletePosition(Position position) {
        Long positionId = position.getId();
        positionHistDao.deleteByPositionId(positionId);
        positionValuationHistDao.deleteByPositionId(positionId);

        PositionInit positionInit = positionInitDao.findById(positionId);
        if (positionInit != null) {
            positionInitDao.delete(positionInit);
        }

        positionDao.delete(position);
    }

    private void deleteAccountValuationHists(Long accountId) {
        accountValuationHistDao.deleteByAccountId(accountId);
    }
}
