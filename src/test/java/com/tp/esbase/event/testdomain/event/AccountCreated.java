package com.tp.esbase.event.testdomain.event;

import com.tp.esbase.event.DomainEvent;
import com.tp.esbase.event.testdomain.AccountId;
import com.tp.esbase.event.testdomain.AccountNumber;
import com.tp.esbase.event.testdomain.Currency;

public record AccountCreated(
    DomainEventHeader<AccountId> header,
    AccountNumber number,
    Currency currency
) implements DomainEvent<AccountId> {

}
