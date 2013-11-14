package com.datayes.invest.pms.web.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datayes.invest.pms.dao.account.AccountDao;
import com.datayes.invest.pms.dao.account.AccountValuationHistDao;
import com.datayes.invest.pms.dao.account.AccountValuationInitDao;
import com.datayes.invest.pms.dao.account.CarryingValueHistDao;
import com.datayes.invest.pms.dao.account.FeeDao;
import com.datayes.invest.pms.dao.account.PositionDao;
import com.datayes.invest.pms.dao.account.PositionHistDao;
import com.datayes.invest.pms.dao.account.PositionInitDao;
import com.datayes.invest.pms.dao.account.PositionValuationHistDao;
import com.datayes.invest.pms.dao.account.SourceTransactionDao;
import com.datayes.invest.pms.dao.account.TransactionDao;
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
    private PositionDao positionDao;

    @Inject
    private PositionInitDao positionInitDao;

    @Inject
    private PositionHistDao positionHistDao;

    @Inject
    private PositionValuationHistDao positionValuationHistDao;

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
        deleteCarryingValueHists(accountId);
        deletePositions(accountId);
        deletePositionInits(accountId);
        deleteAccountValuationHists(accountId);
        deleteAccountValuationInits(accountId);
        deleteFees(accountId);
        accountDao.delete(account);
        LOGGER.info("Account #" + accountId + " deleted");
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

    private void deletePositionInits(Long accountId) {

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
