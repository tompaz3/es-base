package com.tp.esbase.event.testdomain.event;

import com.tp.esbase.event.DomainEvent;
import com.tp.esbase.event.testdomain.AccountId;
import com.tp.esbase.event.testdomain.Block.BlockId;

public record AmountReleased(
    DomainEventHeader<AccountId> header,
    BlockId blockId
) implements DomainEvent<AccountId> {

}
