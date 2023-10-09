package com.tp.esbase.event.testdomain.error;

import com.tp.esbase.event.testdomain.AccountId;
import com.tp.esbase.event.testdomain.Block.BlockId;

public class AccountBlockDoesNotExistException extends RuntimeException {

  private AccountBlockDoesNotExistException(String message) {
    super(message);
  }

  public static AccountBlockDoesNotExistException accountBlockDoesNotExistException(
      AccountId accountId,
      BlockId blockId
  ) {
    return new AccountBlockDoesNotExistException(
        "Block %s does not exist for account %s.".formatted(
            blockId.value(),
            accountId.value()
        )
    );
  }
}
