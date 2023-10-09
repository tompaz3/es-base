package com.tp.esbase.event.testdomain.event;

import com.tp.esbase.event.DomainEvent;
import com.tp.esbase.event.testdomain.AccountId;
import com.tp.esbase.event.testdomain.Block;

public record AmountBlocked(
    DomainEventHeader<AccountId> header,
    Block block
) implements DomainEvent<AccountId> {

}
