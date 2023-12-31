package com.tp.esbase.event;

import static com.tp.esbase.event.AccountAggregateFixtures.CURRENCY_EUR;
import static com.tp.esbase.event.AccountAggregateFixtures.emptyAccountAggregate;
import static com.tp.esbase.event.AccountAggregateFixtures.invalidAmount;
import static com.tp.esbase.event.AccountAggregateFixtures.newAccountNumber;
import static com.tp.esbase.event.AccountAggregateFixtures.newBlockId;
import static com.tp.esbase.event.AccountAggregateFixtures.nextDomainEventHeader;
import static com.tp.esbase.event.AccountAggregateFixtures.validAmount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.tp.esbase.event.DomainEvent.DomainEventHeader;
import com.tp.esbase.event.testdomain.AccountAggregate;
import com.tp.esbase.event.testdomain.AccountId;
import com.tp.esbase.event.testdomain.Block;
import com.tp.esbase.event.testdomain.error.AccountBalanceTooLowForBlockException;
import com.tp.esbase.event.testdomain.error.AccountBalanceTooLowForWithdrawalException;
import com.tp.esbase.event.testdomain.error.AccountBlockDoesNotExistException;
import com.tp.esbase.event.testdomain.error.AccountUnsupportedCurrencyException;
import com.tp.esbase.event.testdomain.error.BlockAlreadyExistsException;
import com.tp.esbase.event.testdomain.event.AccountCreated;
import com.tp.esbase.event.testdomain.event.AmountBlocked;
import com.tp.esbase.event.testdomain.event.AmountCaptured;
import com.tp.esbase.event.testdomain.event.AmountDeposited;
import com.tp.esbase.event.testdomain.event.AmountReleased;
import com.tp.esbase.event.testdomain.event.AmountWithdrawn;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AggregateRootTest {

  @Nested
  class Create {

    @Test
    void when_instantiate_account_aggregate_then_aggregate_created() {
      // given
      var accountId = new AccountId(UUID.randomUUID());
      var number = newAccountNumber();
      var currency = CURRENCY_EUR;

      // when
      var accountAggregate = new AccountAggregate(
          accountId,
          number,
          currency
      );

      // then
      // aggregate data as expected
      assertThat(accountAggregate.id()).isEqualTo(accountId);
      assertThat(accountAggregate.number()).isEqualTo(number);
      assertThat(accountAggregate.availableBalance().amount().currency()).isEqualTo(currency);
      assertThat(accountAggregate.availableBalance().amount().value()).isEqualByComparingTo(BigDecimal.ZERO);
      assertThat(accountAggregate.blockedBalance()).isNotNull();
      assertThat(accountAggregate.type().value()).isEqualTo("account");
      assertThat(accountAggregate.version().isInitial()).isTrue();
      var inEvents = accountAggregate.getAndClearInEvents();
      // and single in AccountCreated event
      assertThat(inEvents)
          .hasSize(1)
          .element(0)
          .satisfies(event -> {
            if (event instanceof AccountCreated accountCreated) {
              assertThat(accountCreated.header().id().value()).isOne();
              assertThat(accountCreated.header().aggregateId()).isEqualTo(accountId);
              assertThat(accountCreated.header().timestamp()).isNotNull();
              assertThat(accountCreated.currency()).isEqualTo(currency);
              assertThat(accountCreated.number()).isEqualTo(number);
            } else {
              Assertions.fail("Event must be of %s instance".formatted(AccountCreated.class.getSimpleName()));
            }
          });
      // and single out AccountCreated event
      var outEvents = accountAggregate.getAndClearOutEvents();
      assertThat(outEvents)
          .hasSize(1)
          .element(0)
          .satisfies(event -> {
            if (event instanceof AccountCreated accountCreated) {
              assertThat(accountCreated.header().id().value()).isOne();
              assertThat(accountCreated.header().aggregateId()).isEqualTo(accountId);
              assertThat(accountCreated.header().timestamp()).isNotNull();
              assertThat(accountCreated.currency()).isEqualTo(currency);
              assertThat(accountCreated.number()).isEqualTo(number);
            } else {
              Assertions.fail("Event must be of %s instance".formatted(AccountCreated.class.getSimpleName()));
            }
          });
    }
  }

  @Nested
  class Deposit {

    @Test
    void when_deposit_amount_then_amount_deposited() {
      // given
      var accountAggregate = emptyAccountAggregate();
      accountAggregate.getAndClearInEvents();
      accountAggregate.getAndClearOutEvents();
      var amount = validAmount(BigDecimal.TEN);

      // when
      accountAggregate.deposit(amount);

      // then
      // available balance increased by the amount
      assertThat(accountAggregate.availableBalance().amount()).isEqualTo(amount);
      // and single in AmountDeposited event
      var inEvents = accountAggregate.getAndClearInEvents();
      assertThat(inEvents)
          .hasSize(1)
          .element(0)
          .satisfies(event -> {
            if (event instanceof AmountDeposited amountDeposited) {
              assertThat(amountDeposited.header().id().value()).isEqualByComparingTo(2L);
              assertThat(amountDeposited.header().aggregateId()).isEqualTo(accountAggregate.id());
              assertThat(amountDeposited.header().timestamp()).isNotNull();
              assertThat(amountDeposited.amount()).isEqualTo(amount);
            } else {
              Assertions.fail("Event must be of %s instance".formatted(AmountDeposited.class.getSimpleName()));
            }
          });
      // and single out AmountDeposited event
      var outEvents = accountAggregate.getAndClearOutEvents();
      assertThat(outEvents)
          .hasSize(1)
          .element(0)
          .satisfies(event -> {
            if (event instanceof AmountDeposited amountDeposited) {
              assertThat(amountDeposited.header().id().value()).isEqualByComparingTo(2L);
              assertThat(amountDeposited.header().aggregateId()).isEqualTo(accountAggregate.id());
              assertThat(amountDeposited.header().timestamp()).isNotNull();
              assertThat(amountDeposited.amount()).isEqualTo(amount);
            } else {
              Assertions.fail("Event must be of %s instance".formatted(AmountDeposited.class.getSimpleName()));
            }
          });
    }

    @Test
    void when_deposit_different_currency_amount_then_fail() {
      // given
      var accountAggregate = emptyAccountAggregate();
      accountAggregate.getAndClearInEvents();
      accountAggregate.getAndClearOutEvents();
      var amount = invalidAmount(BigDecimal.TEN);

      // when
      var throwableAssert = assertThatCode(() -> accountAggregate.deposit(amount));

      // then
      // AccountUnsupportedCurrencyException thrown
      throwableAssert.isInstanceOf(AccountUnsupportedCurrencyException.class)
          .hasMessage("Unsupported currency for the account %s. Supported: %s. Got: %s".formatted(
              accountAggregate.id().value(),
              accountAggregate.availableBalance().amount().currency().name(),
              amount.currency().name()
          ));
      // no in event added
      assertThat(accountAggregate.getAndClearInEvents()).isEmpty();
      // no out event added
      assertThat(accountAggregate.getAndClearOutEvents()).isEmpty();
    }
  }

  @Nested
  class Withdraw {

    @Test
    void when_withdraw_then_success() {
      // given
      var accountAggregate = emptyAccountAggregate();
      accountAggregate.deposit(validAmount(BigDecimal.TEN));
      accountAggregate.getAndClearInEvents();
      accountAggregate.getAndClearOutEvents();
      var amount = validAmount(BigDecimal.ONE);

      // when
      accountAggregate.withdraw(amount);

      // then
      assertThat(accountAggregate.availableBalance().amount().value()).isEqualTo(BigDecimal.valueOf(9L));
      // and single in AmountWithdrawn event
      var inEvents = accountAggregate.getAndClearInEvents();
      assertThat(inEvents)
          .hasSize(1)
          .element(0)
          .satisfies(event -> {
            if (event instanceof AmountWithdrawn amountWithdrawn) {
              assertThat(amountWithdrawn.header().id().value()).isEqualByComparingTo(3L);
              assertThat(amountWithdrawn.header().aggregateId()).isEqualTo(accountAggregate.id());
              assertThat(amountWithdrawn.header().timestamp()).isNotNull();
              assertThat(amountWithdrawn.amount()).isEqualTo(amount);
            } else {
              Assertions.fail("Event must be of %s instance".formatted(AmountWithdrawn.class.getSimpleName()));
            }
          });
      // and single out AmountWithdrawn event
      var outEvents = accountAggregate.getAndClearOutEvents();
      assertThat(outEvents)
          .hasSize(1)
          .element(0)
          .satisfies(event -> {
            if (event instanceof AmountWithdrawn amountWithdrawn) {
              assertThat(amountWithdrawn.header().id().value()).isEqualByComparingTo(3L);
              assertThat(amountWithdrawn.header().aggregateId()).isEqualTo(accountAggregate.id());
              assertThat(amountWithdrawn.header().timestamp()).isNotNull();
              assertThat(amountWithdrawn.amount()).isEqualTo(amount);
            } else {
              Assertions.fail("Event must be of %s instance".formatted(AmountWithdrawn.class.getSimpleName()));
            }
          });
    }

    @Test
    void when_withdraw_more_than_available_then_fail() {
      // given
      var accountAggregate = emptyAccountAggregate();
      accountAggregate.deposit(validAmount(BigDecimal.ONE));
      accountAggregate.getAndClearInEvents();
      accountAggregate.getAndClearOutEvents();
      var amount = validAmount(BigDecimal.TEN);

      // when
      var throwableAssert = assertThatCode(() -> accountAggregate.withdraw(amount));

      // then
      // AccountBalanceTooLowForWithdrawalException thrown
      throwableAssert.isInstanceOf(AccountBalanceTooLowForWithdrawalException.class)
          .hasMessage("Tried to withdraw %s, while account %s balance is %s".formatted(
              amount.value(),
              accountAggregate.id().value(),
              accountAggregate.availableBalance().amount().value().toString()
          ));
      // no in event added
      assertThat(accountAggregate.getAndClearInEvents()).isEmpty();
      // no out event added
      assertThat(accountAggregate.getAndClearOutEvents()).isEmpty();
    }

    @Test
    void when_withdraw_different_currency_then_fail() {
      // given
      var accountAggregate = emptyAccountAggregate();
      accountAggregate.deposit(validAmount(BigDecimal.TEN));
      accountAggregate.getAndClearInEvents();
      accountAggregate.getAndClearOutEvents();
      var amount = invalidAmount(BigDecimal.ONE);

      // when
      var throwableAssert = assertThatCode(() -> accountAggregate.withdraw(amount));

      // then
      // AccountUnsupportedCurrencyException thrown
      throwableAssert.isInstanceOf(AccountUnsupportedCurrencyException.class)
          .hasMessage("Unsupported currency for the account %s. Supported: %s. Got: %s".formatted(
              accountAggregate.id().value(),
              accountAggregate.availableBalance().amount().currency().name(),
              amount.currency().name()
          ));
      // no in event added
      assertThat(accountAggregate.getAndClearInEvents()).isEmpty();
      // no out event added
      assertThat(accountAggregate.getAndClearOutEvents()).isEmpty();
    }
  }

  @Nested
  class Blocking {

    @Test
    void when_block_then_success() {
      // given
      var accountAggregate = emptyAccountAggregate();
      accountAggregate.deposit(validAmount(BigDecimal.TEN));
      accountAggregate.getAndClearInEvents();
      accountAggregate.getAndClearOutEvents();
      var amount = validAmount(BigDecimal.ONE);
      var block = new Block(newBlockId(), amount);

      // when
      accountAggregate.block(block);

      // then
      assertThat(accountAggregate.availableBalance().amount().value()).isEqualTo(BigDecimal.valueOf(9L));
      // and single in AmountBlocked event
      var inEvents = accountAggregate.getAndClearInEvents();
      assertThat(inEvents)
          .hasSize(1)
          .element(0)
          .satisfies(event -> {
            if (event instanceof AmountBlocked amountBlocked) {
              assertThat(amountBlocked.header().id().value()).isEqualByComparingTo(3L);
              assertThat(amountBlocked.header().aggregateId()).isEqualTo(accountAggregate.id());
              assertThat(amountBlocked.header().timestamp()).isNotNull();
              assertThat(amountBlocked.block().amount()).isEqualTo(amount);
              assertThat(amountBlocked.block().id()).isEqualTo(block.id());
            } else {
              Assertions.fail("Event must be of %s instance".formatted(AmountBlocked.class.getSimpleName()));
            }
          });
      // and single out AmountBlocked event
      var outEvents = accountAggregate.getAndClearOutEvents();
      assertThat(outEvents)
          .hasSize(1)
          .element(0)
          .satisfies(event -> {
            if (event instanceof AmountBlocked amountBlocked) {
              assertThat(amountBlocked.header().id().value()).isEqualByComparingTo(3L);
              assertThat(amountBlocked.header().aggregateId()).isEqualTo(accountAggregate.id());
              assertThat(amountBlocked.header().timestamp()).isNotNull();
              assertThat(amountBlocked.block().amount()).isEqualTo(amount);
              assertThat(amountBlocked.block().id()).isEqualTo(block.id());
            } else {
              Assertions.fail("Event must be of %s instance".formatted(AmountBlocked.class.getSimpleName()));
            }
          });
    }

    @Test
    void when_block_already_exists_then_fail() {
      // given
      var accountAggregate = emptyAccountAggregate();
      accountAggregate.deposit(validAmount(BigDecimal.TEN));
      var amount = validAmount(BigDecimal.ONE);
      var block = new Block(newBlockId(), amount);
      accountAggregate.block(block);
      accountAggregate.getAndClearInEvents();
      accountAggregate.getAndClearOutEvents();

      var newBlock = new Block(block.id(), validAmount(BigDecimal.TWO));

      // when
      var throwableAssert = assertThatCode(() -> accountAggregate.block(newBlock));

      // then
      // AccountBalanceTooLowForWithdrawalException thrown
      throwableAssert.isInstanceOf(BlockAlreadyExistsException.class)
          .hasMessage("Block %s already exists for account %s".formatted(
              block.id().value(),
              accountAggregate.id().value()
          ));
      // no in event added
      assertThat(accountAggregate.getAndClearInEvents()).isEmpty();
      // no out event added
      assertThat(accountAggregate.getAndClearOutEvents()).isEmpty();
    }

    @Test
    void when_block_more_than_available_then_fail() {
      // given
      var accountAggregate = emptyAccountAggregate();
      accountAggregate.deposit(validAmount(BigDecimal.ONE));
      accountAggregate.getAndClearInEvents();
      accountAggregate.getAndClearOutEvents();
      var amount = validAmount(BigDecimal.TEN);
      var block = new Block(newBlockId(), amount);

      // when
      var throwableAssert = assertThatCode(() -> accountAggregate.block(block));

      // then
      // AccountBalanceTooLowForWithdrawalException thrown
      throwableAssert.isInstanceOf(AccountBalanceTooLowForBlockException.class)
          .hasMessage("Tried to block %s (%s), while account %s balance is %s".formatted(
              block.amount().value(),
              block.id().value(),
              accountAggregate.id().value(),
              accountAggregate.availableBalance().amount().value().toString()
          ));
      // no in event added
      assertThat(accountAggregate.getAndClearInEvents()).isEmpty();
      // no out event added
      assertThat(accountAggregate.getAndClearOutEvents()).isEmpty();
    }

    @Test
    void when_withdraw_different_currency_then_fail() {
      // given
      var accountAggregate = emptyAccountAggregate();
      accountAggregate.deposit(validAmount(BigDecimal.TEN));
      accountAggregate.getAndClearInEvents();
      accountAggregate.getAndClearOutEvents();
      var amount = invalidAmount(BigDecimal.ONE);
      var block = new Block(newBlockId(), amount);

      // when
      var throwableAssert = assertThatCode(() -> accountAggregate.block(block));

      // then
      // AccountUnsupportedCurrencyException thrown
      throwableAssert.isInstanceOf(AccountUnsupportedCurrencyException.class)
          .hasMessage("Unsupported currency for the account %s. Supported: %s. Got: %s".formatted(
              accountAggregate.id().value(),
              accountAggregate.availableBalance().amount().currency().name(),
              amount.currency().name()
          ));
      // no in event added
      assertThat(accountAggregate.getAndClearInEvents()).isEmpty();
      // no out event added
      assertThat(accountAggregate.getAndClearOutEvents()).isEmpty();
    }
  }

  @Nested
  class Capture {

    @Test
    void when_capture_then_success() {
      // given
      var accountAggregate = emptyAccountAggregate();
      accountAggregate.deposit(validAmount(BigDecimal.TEN));
      var blockId = newBlockId();
      var block = new Block(
          blockId,
          validAmount(BigDecimal.ONE)
      );
      accountAggregate.block(block);
      accountAggregate.getAndClearInEvents();
      accountAggregate.getAndClearOutEvents();

      // when
      accountAggregate.capture(blockId);

      // then
      assertThat(accountAggregate.availableBalance().amount().value()).isEqualTo(BigDecimal.valueOf(9L));
      assertThat(accountAggregate.blockedBalance().doesNotContain(blockId)).isTrue();
      // and single in AmountCaptured event
      var inEvents = accountAggregate.getAndClearInEvents();
      assertThat(inEvents)
          .hasSize(1)
          .element(0)
          .satisfies(event -> {
            if (event instanceof AmountCaptured amountCaptured) {
              assertThat(amountCaptured.header().id().value()).isEqualByComparingTo(4L);
              assertThat(amountCaptured.header().aggregateId()).isEqualTo(accountAggregate.id());
              assertThat(amountCaptured.header().timestamp()).isNotNull();
              assertThat(amountCaptured.blockId()).isEqualTo(blockId);
            } else {
              Assertions.fail("Event must be of %s instance".formatted(AmountBlocked.class.getSimpleName()));
            }
          });
      // and single out AmountCaptured event
      var outEvents = accountAggregate.getAndClearOutEvents();
      assertThat(outEvents)
          .hasSize(1)
          .element(0)
          .satisfies(event -> {
            if (event instanceof AmountCaptured amountCaptured) {
              assertThat(amountCaptured.header().id().value()).isEqualByComparingTo(4L);
              assertThat(amountCaptured.header().aggregateId()).isEqualTo(accountAggregate.id());
              assertThat(amountCaptured.header().timestamp()).isNotNull();
              assertThat(amountCaptured.blockId()).isEqualTo(blockId);
            } else {
              Assertions.fail("Event must be of %s instance".formatted(AmountBlocked.class.getSimpleName()));
            }
          });
    }

    @Test
    void when_capture_non_existing_block_then_fail() {
      // given
      var accountAggregate = emptyAccountAggregate();
      accountAggregate.deposit(validAmount(BigDecimal.TEN));
      accountAggregate.getAndClearInEvents();
      accountAggregate.getAndClearOutEvents();
      var blockId = newBlockId();

      // when
      var throwableAssert = assertThatCode(() -> accountAggregate.capture(blockId));

      // then
      throwableAssert.isInstanceOf(AccountBlockDoesNotExistException.class)
          .hasMessage("Block %s does not exist for account %s.".formatted(
              blockId.value(),
              accountAggregate.id().value()
          ));
      // no in event added
      assertThat(accountAggregate.getAndClearInEvents()).isEmpty();
      // no out event added
      assertThat(accountAggregate.getAndClearOutEvents()).isEmpty();
    }
  }

  @Nested
  class Release {

    @Test
    void when_release_then_success() {
      // given
      var accountAggregate = emptyAccountAggregate();
      accountAggregate.deposit(validAmount(BigDecimal.TEN));
      var blockId = newBlockId();
      var block = new Block(
          blockId,
          validAmount(BigDecimal.ONE)
      );
      accountAggregate.block(block);
      accountAggregate.getAndClearInEvents();
      accountAggregate.getAndClearOutEvents();

      // when
      accountAggregate.release(blockId);

      // then
      assertThat(accountAggregate.availableBalance().amount().value()).isEqualTo(BigDecimal.TEN);
      assertThat(accountAggregate.blockedBalance().doesNotContain(blockId)).isTrue();
      // and single in AmountReleased event
      var inEvents = accountAggregate.getAndClearInEvents();
      assertThat(inEvents)
          .hasSize(1)
          .element(0)
          .satisfies(event -> {
            if (event instanceof AmountReleased amountReleased) {
              assertThat(amountReleased.header().id().value()).isEqualByComparingTo(4L);
              assertThat(amountReleased.header().aggregateId()).isEqualTo(accountAggregate.id());
              assertThat(amountReleased.header().timestamp()).isNotNull();
              assertThat(amountReleased.blockId()).isEqualTo(blockId);
            } else {
              Assertions.fail("Event must be of %s instance".formatted(AmountBlocked.class.getSimpleName()));
            }
          });
      // and single out AmountReleased event
      var outEvents = accountAggregate.getAndClearOutEvents();
      assertThat(outEvents)
          .hasSize(1)
          .element(0)
          .satisfies(event -> {
            if (event instanceof AmountReleased amountReleased) {
              assertThat(amountReleased.header().id().value()).isEqualByComparingTo(4L);
              assertThat(amountReleased.header().aggregateId()).isEqualTo(accountAggregate.id());
              assertThat(amountReleased.header().timestamp()).isNotNull();
              assertThat(amountReleased.blockId()).isEqualTo(blockId);
            } else {
              Assertions.fail("Event must be of %s instance".formatted(AmountBlocked.class.getSimpleName()));
            }
          });
    }

    @Test
    void when_release_non_existing_block_then_fail() {
      // given
      var accountAggregate = emptyAccountAggregate();
      accountAggregate.deposit(validAmount(BigDecimal.TEN));
      accountAggregate.getAndClearInEvents();
      accountAggregate.getAndClearOutEvents();
      var blockId = newBlockId();

      // when
      var throwableAssert = assertThatCode(() -> accountAggregate.release(blockId));

      // then
      throwableAssert.isInstanceOf(AccountBlockDoesNotExistException.class)
          .hasMessage("Block %s does not exist for account %s.".formatted(
              blockId.value(),
              accountAggregate.id().value()
          ));
      // no in event added
      assertThat(accountAggregate.getAndClearInEvents()).isEmpty();
      // no out event added
      assertThat(accountAggregate.getAndClearOutEvents()).isEmpty();
    }
  }

  @Nested
  class Restore {

    @Test
    void given_events_when_restore_then_success() {
      // given
      var accountId = new AccountId(UUID.randomUUID());
      var accountNumber = newAccountNumber();
      var domainEventHeader = DomainEventHeader.initial(accountId);
      var headerHolder = new AtomicReference<>(domainEventHeader);
      var block1 = newBlockId();
      var block2 = newBlockId();
      // account aggregate events
      var events = Stream.<DomainEventSupplier<AccountId>>of(
              DomainEventSupplier.of(header -> new AccountCreated(
                  header,
                  accountNumber,
                  CURRENCY_EUR
              )),
              DomainEventSupplier.of(header -> new AmountDeposited(
                  header,
                  validAmount(BigDecimal.ONE)
              )),
              DomainEventSupplier.of(header -> new AmountBlocked(
                  header,
                  new Block(
                      block1,
                      validAmount(BigDecimal.ONE)
                  )
              )),
              DomainEventSupplier.of(header -> new AmountDeposited(
                  header,
                  validAmount(BigDecimal.TEN)
              )),
              DomainEventSupplier.of(header -> new AmountBlocked(
                  header,
                  new Block(
                      block2,
                      validAmount(BigDecimal.valueOf(7))
                  )
              )),
              DomainEventSupplier.of(header -> new AmountWithdrawn(
                  header,
                  validAmount(BigDecimal.valueOf(2))
              )),
              DomainEventSupplier.of(header -> new AmountReleased(
                  header,
                  block1
              )),
              DomainEventSupplier.of(header -> new AmountCaptured(
                  header,
                  block2
              ))
          )
          .map(it -> {
            var header = headerHolder.get();
            headerHolder.set(nextDomainEventHeader(header));
            return it.supply(header);
          })
          .toList();

      // when
      var accountAggregate = AccountAggregate.restore(events, Version.specified(5L), AccountAggregate::new);

      // then
      assertThat(accountAggregate.id()).isEqualTo(accountId);
      assertThat(accountAggregate.number()).isEqualTo(accountNumber);
      assertThat(accountAggregate.availableBalance().amount().value()).isEqualByComparingTo(BigDecimal.valueOf(2));
      assertThat(accountAggregate.availableBalance().amount().currency()).isEqualTo(CURRENCY_EUR);
      assertThat(accountAggregate.blockedBalance().doesNotContain(block1)).isTrue();
      assertThat(accountAggregate.blockedBalance().doesNotContain(block2)).isTrue();
      // and no in event added
      var inEvents = accountAggregate.getAndClearInEvents();
      assertThat(inEvents).isEmpty();
      // and no out event added
      var outEvents = accountAggregate.getAndClearOutEvents();
      assertThat(outEvents).isEmpty();
    }
  }

  interface DomainEventSupplier<ID extends AggregateId> {

    DomainEvent<ID> supply(DomainEventHeader<ID> header);

    static <ID extends AggregateId> DomainEventSupplier<ID> of(DomainEventSupplier<ID> supplier) {
      return supplier;
    }
  }


}