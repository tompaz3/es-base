package com.tp.esbase.event.testdomain.error;

import com.tp.esbase.event.testdomain.AccountId;
import com.tp.esbase.event.testdomain.Amount;

public class AccountBalanceTooLowForWithdrawalException extends RuntimeException {

  private AccountBalanceTooLowForWithdrawalException(String message) {
    super(message);
  }

  public static AccountBalanceTooLowForWithdrawalException accountBalanceTooLowForWithdrawalException(
      AccountId accountId,
      Amount availableBalance,
      Amount withdrawal
  ) {
    return new AccountBalanceTooLowForWithdrawalException(
        "Tried to withdraw %s, while account %s balance is %s".formatted(
            withdrawal.value(),
            accountId.value(),
            availableBalance.value().toString()
        )
    );
  }
}
