package com.tp.esbase.event;

import com.tp.esbase.event.DomainEvent.DomainEventHeader;
import com.tp.esbase.event.DomainEvent.EventTimestamp;
import com.tp.esbase.event.testdomain.AccountAggregate;
import com.tp.esbase.event.testdomain.AccountId;
import com.tp.esbase.event.testdomain.AccountNumber;
import com.tp.esbase.event.testdomain.AccountNumber.Iban;
import com.tp.esbase.event.testdomain.Amount;
import com.tp.esbase.event.testdomain.Block.BlockId;
import com.tp.esbase.event.testdomain.Currency;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;
import net.datafaker.Faker;

public class AccountAggregateFixtures {

  public static final Currency CURRENCY_EUR = Currency.of("EUR");
  public static final Currency CURRENCY_USD = Currency.of("USD");
  public static final Faker FAKER = new Faker(Locale.ENGLISH);

  public static AccountAggregate emptyAccountAggregate() {
    var accountId = new AccountId(UUID.randomUUID());
    var number = new AccountNumber(new Iban(FAKER.finance().iban()));
    return new AccountAggregate(
        accountId,
        number,
        CURRENCY_EUR
    );
  }

  public static <ID extends AggregateId> DomainEventHeader<ID> nextDomainEventHeader(DomainEventHeader<ID> previous) {
    return new DomainEventHeader<>(
        previous.id().next(),
        previous.aggregateId(),
        EventTimestamp.now()
    );
  }

  public static Amount validAmount(BigDecimal value) {
    return new Amount(CURRENCY_EUR, value);
  }

  public static Amount invalidAmount(BigDecimal value) {
    return new Amount(CURRENCY_USD, value);
  }

  public static BlockId newBlockId() {
    return new BlockId(UUID.randomUUID().toString());
  }

  public static AccountNumber newAccountNumber() {
    return new AccountNumber(new Iban(FAKER.finance().iban()));
  }
}
