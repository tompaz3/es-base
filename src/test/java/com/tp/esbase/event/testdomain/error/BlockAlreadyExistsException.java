package com.tp.esbase.event.testdomain.error;

import com.tp.esbase.event.testdomain.AccountId;
import com.tp.esbase.event.testdomain.Block;

public class BlockAlreadyExistsException extends RuntimeException {

  private BlockAlreadyExistsException(String message) {
    super(message);
  }

  public static BlockAlreadyExistsException blockAlreadyExistsException(
      AccountId accountId,
      Block block
  ) {
    return new BlockAlreadyExistsException(
        "Block %s already exists for account %s".formatted(
            block.id().value(),
            accountId.value()
        )
    );
  }

}
