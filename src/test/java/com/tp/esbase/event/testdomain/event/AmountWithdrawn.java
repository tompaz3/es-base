package com.tp.esbase.event.testdomain.event;

import com.tp.esbase.event.DomainEvent;
import com.tp.esbase.event.testdomain.AccountId;
import com.tp.esbase.event.testdomain.Amount;

public record AmountWithdrawn(
    DomainEventHeader<AccountId> header,
    Amount amount
) implements DomainEvent<AccountId> {

}
