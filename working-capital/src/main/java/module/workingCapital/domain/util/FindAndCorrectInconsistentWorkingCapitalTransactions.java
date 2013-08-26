/*
 * @(#)FindAndCorrectInconsistentWorkingCapitalTransactions.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Working Capital Module.
 *
 *   The Working Capital Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Working Capital Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Working Capital Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.workingCapital.domain.util;

import java.util.HashMap;

import module.workingCapital.domain.WorkingCapital;
import module.workingCapital.domain.WorkingCapitalAcquisitionSubmission;
import module.workingCapital.domain.WorkingCapitalSystem;
import module.workingCapital.domain.WorkingCapitalTransaction;
import pt.ist.bennu.core.domain.util.Money;
import pt.ist.bennu.scheduler.custom.CustomTask;

/**
 * 
 * @author João Neves
 * 
 */
public class FindAndCorrectInconsistentWorkingCapitalTransactions extends CustomTask {

    private final boolean PERFORM_CORRECTIONS = false;

    private boolean isNewWorkingCapital = false;

    private HashMap<Integer, WorkingCapitalTransaction> transactionList;

    @Override
    public void runTask() {
        taskLog("\n");
        taskLog("################################################################################################");
        taskLog("\n");
        taskLog("Processing " + WorkingCapitalSystem.getInstanceForCurrentHost().getWorkingCapitals().size()
                + " working capitals.");
        for (WorkingCapital workingCapital : WorkingCapitalSystem.getInstanceForCurrentHost().getWorkingCapitals()) {
            isNewWorkingCapital = true;

            if (workingCapital.getWorkingCapitalTransactions().size() == 0) {
                continue;
            }

            transactionList = new HashMap<Integer, WorkingCapitalTransaction>();
            for (WorkingCapitalTransaction transaction : workingCapital.getWorkingCapitalTransactions()) {
                transactionList.put(transaction.getNumber(), transaction);
            }

            WorkingCapitalTransaction transaction = transactionList.get(1);
            checkFirstTransaction(transaction);

            WorkingCapitalTransaction previousTransaction = transaction;
            Integer i = 2;
            transaction = transactionList.get(i);

            while (transaction != null) {
                checkTransactionByType(transaction, previousTransaction);
                checkBalanceEqualsDebt(transaction);
                checkPositiveValues(transaction);

                previousTransaction = transaction;
                i++;
                transaction = transactionList.get(i);
            }
        }
    }

    private void checkFirstTransaction(WorkingCapitalTransaction transaction) {
        if (!transaction.isPayment()) {
            printTransaction(transaction);
            taskLog("WARNING - First transaction is not a payment!");
        }

        if (!transaction.getAccumulatedValue().isZero()) {
            printTransaction(transaction);
            taskLog("WARNING - First transaction ACCUMULATED VALUE is not zero!");
        }

        if (!transaction.getValue().equals(transaction.getBalance())) {
            printTransaction(transaction);
            taskLog("WARNING - First transaction VALUE is DIFFERENT from the BALANCE!");
        }
        checkPositiveValues(transaction);
        checkBalanceEqualsDebt(transaction);
    }

    private void checkTransactionByType(WorkingCapitalTransaction transaction, WorkingCapitalTransaction previousTransaction) {
        if (transaction.isPayment()) {
            if (!transaction.getAccumulatedValue().equals(previousTransaction.getAccumulatedValue())) {
                printTransaction(transaction);
                taskLog("WARNING - Payment transaction changes the ACCUMULATED VALUE!");
            }

            if (!transaction.getBalance().equals(transaction.getValue().add(previousTransaction.getBalance()))) {
                printTransaction(transaction);
                taskLog("WARNING - Payment transaction does not update the BALANCE correctly!");
            }
            return;
        }

        if (transaction.isAcquisition()) {
            if (transaction.isCanceledOrRejected()) {
                if (!transaction.getAccumulatedValue().equals(previousTransaction.getAccumulatedValue())) {
                    printTransaction(transaction);
                    taskLog("WARNING - Canceled acquisition transaction changes the ACCUMULATED VALUE!");
                    if (PERFORM_CORRECTIONS) {
                        taskLog("-> correcting the problem...");
                        final Money correctAccumulatedValue = previousTransaction.getAccumulatedValue();
                        final Money diffAccumulatedValue = correctAccumulatedValue.subtract(transaction.getAccumulatedValue());
                        correctAccumulatedValueOfFollowingTransactions(transaction, diffAccumulatedValue);
                    } else {
                        taskLog("-> to correct the problem, set PERFORM_CORRECTIONS to TRUE.");
                    }
                }

                if (!transaction.getBalance().equals(previousTransaction.getBalance())) {
                    printTransaction(transaction);
                    taskLog("WARNING - Canceled acquisition transaction changes the BALANCE!");
                    if (PERFORM_CORRECTIONS) {
                        taskLog("-> correcting the problem...");
                        final Money correctBalance = previousTransaction.getBalance();
                        final Money diffBalance = correctBalance.subtract(transaction.getBalance());
                        correctBalanceOfFollowingTransactions(transaction, diffBalance);
                        correctDebtOfFollowingTransactions(transaction, diffBalance);
                    } else {
                        taskLog("-> to correct the problem, set PERFORM_CORRECTIONS to TRUE.");
                    }
                }
            } else {
                if (!transaction.getAccumulatedValue().equals(
                        transaction.getValue().add(previousTransaction.getAccumulatedValue()))) {
                    printTransaction(transaction);
                    taskLog("Acquisition transaction does not update the ACCUMULATED VALUE correctly!");
                    if (PERFORM_CORRECTIONS) {
                        taskLog("-> correcting the problem...");
                        final Money correctAccumulatedValue =
                                transaction.getValue().add(previousTransaction.getAccumulatedValue());
                        final Money diffAccumulatedValue = correctAccumulatedValue.subtract(transaction.getAccumulatedValue());
                        correctAccumulatedValueOfFollowingTransactions(transaction, diffAccumulatedValue);
                    } else {
                        taskLog("-> to correct the problem, set PERFORM_CORRECTIONS to TRUE.");
                    }
                }

                if (!transaction.getBalance().equals(previousTransaction.getBalance().subtract(transaction.getValue()))) {
                    printTransaction(transaction);
                    taskLog("Acquisition transaction does not update the BALANCE correctly!");
                    if (PERFORM_CORRECTIONS) {
                        taskLog("-> correcting the problem...");
                        final Money correctBalance = previousTransaction.getBalance().subtract(transaction.getValue());
                        final Money diffBalance = correctBalance.subtract(transaction.getBalance());
                        correctBalanceOfFollowingTransactions(transaction, diffBalance);
                        correctDebtOfFollowingTransactions(transaction, diffBalance);
                    } else {
                        taskLog("-> to correct the problem, set PERFORM_CORRECTIONS to TRUE.");
                    }
                }
            }
            return;
        }

        if (transaction instanceof WorkingCapitalAcquisitionSubmission) {
            if (!transaction.getValue().equals(previousTransaction.getAccumulatedValue())) {
                printTransaction(transaction);
                taskLog("AcquisitionSubmission transaction VALUE is DIFFERENT from the previous ACCUMULATED VALUE!");
                if (PERFORM_CORRECTIONS) {
                    taskLog("-> correcting the problem...");
                    final Money correctValue = previousTransaction.getAccumulatedValue();
                    final Money diffValue = correctValue.subtract(transaction.getValue());
                    correctValue(transaction, diffValue);
                } else {
                    taskLog("-> to correct the problem, set PERFORM_CORRECTIONS to TRUE.");
                }
            }

            if (!transaction.getAccumulatedValue().isZero()) {
                printTransaction(transaction);
                taskLog("WARNING - AcquisitionSubmission transaction ACCUMULATED VALUE is not zero!");
            }

            if (!transaction.getBalance().equals(previousTransaction.getBalance())) {
                printTransaction(transaction);
                taskLog("WARNING - AcquisitionSubmission transaction changes the BALANCE!");
            }
            return;
        }

        if (transaction.isRefund()) {
            if (!previousTransaction.getAccumulatedValue().isZero()) {
                printTransaction(transaction);
                taskLog("WARNING - Refund transaction is not performed after a acquisition submission!");
            }

            if (!transaction.getAccumulatedValue().equals(previousTransaction.getAccumulatedValue())) {
                printTransaction(transaction);
                taskLog("WARNING - Refund transaction changes the ACCUMULATED VALUE!");
            }

            if (!transaction.getValue().equals(previousTransaction.getBalance())) {
                printTransaction(transaction);
                taskLog("WARNING - Refund transaction VALUE is DIFFERENT from the previous BALANCE!");
            }

            if (!transaction.getBalance().isZero()) {
                printTransaction(transaction);
                taskLog("WARNING - Refund transaction does not set the BALANCE to zero!");
            }
            return;
        }
    }

    private void checkBalanceEqualsDebt(WorkingCapitalTransaction transaction) {
        if (!transaction.getBalance().equals(transaction.getDebt())) {
            printTransaction(transaction);
            taskLog("DEBT is DIFFERENT from the BALANCE!");
            if (PERFORM_CORRECTIONS) {
                taskLog("-> correcting the problem...");
                final Money correctDebt = transaction.getBalance();
                final Money diffDebt = correctDebt.subtract(transaction.getDebt());
                correctDebt(transaction, diffDebt);
            } else {
                taskLog("-> to correct the problem, set PERFORM_CORRECTIONS to TRUE.");
            }
        }
    }

    private void checkPositiveValues(WorkingCapitalTransaction transaction) {
        if (transaction.getValue().isNegative() || transaction.getAccumulatedValue().isNegative()
                || transaction.getBalance().isNegative() || transaction.getDebt().isNegative()) {
            printTransaction(transaction);
            taskLog("WARNING - Negative values found!");
        }
    }

    private void correctAccumulatedValueOfFollowingTransactions(WorkingCapitalTransaction transaction, Money diffAccumulatedValue) {
        transaction.setAccumulatedValue(transaction.getAccumulatedValue().add(diffAccumulatedValue));
        taskLog("AccumulatedValue of transaction " + transaction.getNumber() + " set to: "
                + transaction.getAccumulatedValue().toFormatString());
        if ((transaction.getNext() != null) && !(transaction.getNext() instanceof WorkingCapitalAcquisitionSubmission)) {
            correctAccumulatedValueOfFollowingTransactions(transaction.getNext(), diffAccumulatedValue);
        }
    }

    private void correctBalanceOfFollowingTransactions(WorkingCapitalTransaction transaction, Money diffBalance) {
        transaction.setBalance(transaction.getBalance().add(diffBalance));
        taskLog("Balance of transaction " + transaction.getNumber() + " set to: " + transaction.getBalance().toFormatString());
        if (transaction.getNext() != null) {
            correctBalanceOfFollowingTransactions(transaction.getNext(), diffBalance);
        }
    }

    private void correctDebtOfFollowingTransactions(WorkingCapitalTransaction transaction, Money diffDebt) {
        correctDebt(transaction, diffDebt);
        if (transaction.getNext() != null) {
            correctDebtOfFollowingTransactions(transaction.getNext(), diffDebt);
        }
    }

    private void correctDebt(WorkingCapitalTransaction transaction, Money diffDebt) {
        transaction.setDebt(transaction.getDebt().add(diffDebt));
        taskLog("Debt of transaction " + transaction.getNumber() + " set to: " + transaction.getDebt().toFormatString());
    }

    private void correctValue(WorkingCapitalTransaction transaction, Money diffValue) {
        transaction.setValue(transaction.getValue().add(diffValue));
        taskLog("Value of transaction " + transaction.getNumber() + " set to: " + transaction.getValue().toFormatString());
    }

    private void printTransaction(WorkingCapitalTransaction transaction) {
        printWorkingCapitalIfNew(transaction.getWorkingCapital());
        taskLog("\n");
        taskLog("Transaction: " + transaction.getNumber() + ". " + transaction.getDescription() + " ["
                + transaction.getExternalId() + "]");
    }

    private void printWorkingCapitalIfNew(WorkingCapital workingCapital) {
        if (!isNewWorkingCapital) {
            return;
        }

        taskLog("\n");
        taskLog("\n");
        taskLog("\n");
        taskLog("\n");
        taskLog("\n");
        taskLog("Problems found in working capital: " + workingCapital.getUnit().getPresentationName() + " ["
                + workingCapital.getExternalId() + "]");
        taskLog(workingCapital.getWorkingCapitalTransactions().size() + " transactions found.");

        isNewWorkingCapital = false;
    }
}
