package com.tp.esbase.event.testdomain.error;

import com.tp.esbase.event.testdomain.AccountId;
import com.tp.esbase.event.testdomain.Currency;

public class AccountUnsupportedCurrencyException extends RuntimeException {

  private AccountUnsupportedCurrencyException(String message) {
    super(message);
  }

  public static AccountUnsupportedCurrencyException accountUnsupportedCurrencyException(
      AccountId accountId,
      Currency accountCurrency,
      Currency unsupportedCurrency
  ) {
    return new AccountUnsupportedCurrencyException(
        "Unsupported currency for the account %s. Supported: %s. Got: %s".formatted(
            accountId.value(),
            accountCurrency.name(),
            unsupportedCurrency.name()
        )
    );
  }
}
