package com.tp.esbase.event.testdomain.error;

import com.tp.esbase.event.testdomain.AccountId;
import com.tp.esbase.event.testdomain.Amount;
import com.tp.esbase.event.testdomain.Block;

public class AccountBalanceTooLowForBlockException extends RuntimeException {

  private AccountBalanceTooLowForBlockException(String message) {
    super(message);
  }

  public static AccountBalanceTooLowForBlockException accountBalanceTooLowForBlockException(
      AccountId accountId,
      Amount availableBalance,
      Block block
  ) {
    return new AccountBalanceTooLowForBlockException(
        "Tried to block %s (%s), while account %s balance is %s".formatted(
            block.amount().value(),
            block.id().value(),
            accountId.value(),
            availableBalance.value().toString()
        )
    );
  }
}
