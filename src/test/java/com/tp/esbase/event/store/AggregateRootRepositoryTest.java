package com.tp.esbase.event.store;

import static com.tp.esbase.event.AccountAggregateFixtures.emptyAccountAggregate;
import static com.tp.esbase.event.AccountAggregateFixtures.newBlockId;
import static com.tp.esbase.event.AccountAggregateFixtures.validAmount;
import static org.assertj.core.api.Assertions.assertThat;

import com.tp.esbase.event.testdomain.AccountAggregate;
import com.tp.esbase.event.testdomain.Block;
import com.tp.esbase.event.testdomain.event.AccountCreated;
import com.tp.esbase.event.testdomain.event.AmountBlocked;
import com.tp.esbase.event.testdomain.event.AmountDeposited;
import com.tp.esbase.event.testdomain.event.AmountWithdrawn;
import java.math.BigDecimal;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AggregateRootRepositoryTest {

  private final SimpleEventSerde eventSerde = new SimpleEventSerde();
  private final InMemoryEventsRepository eventsRepository = new InMemoryEventsRepository();
  private final InMemoryListEventPublisher eventPublisher = new InMemoryListEventPublisher();
  private final AggregateRootRepository<Object> aggregateRootRepository = new AggregateRootRepository<>(
      new EventStore<>(
          eventSerde,
          eventsRepository,
          eventSerde
      ),
      eventPublisher
  );

  @Test
  void given_aggregate_when_save_then_saved_and_found() {
    // given
    var accountAggregate = emptyAccountAggregate();
    var depositedAmount1 = validAmount(BigDecimal.TEN);
    accountAggregate.deposit(depositedAmount1);
    var block = new Block(newBlockId(), validAmount(BigDecimal.ONE));
    accountAggregate.block(block);
    var depositedAmount2 = validAmount(BigDecimal.valueOf(3));
    accountAggregate.deposit(depositedAmount2);
    var withdrawnAmount = validAmount(BigDecimal.valueOf(5));
    accountAggregate.withdraw(withdrawnAmount);

    // when
    aggregateRootRepository.save(accountAggregate);

    // then
    // aggregate can be found
    var aggregate = aggregateRootRepository.findById(
        accountAggregate.type(),
        accountAggregate.id(),
        AccountAggregate::new
    );
    // and has expected state
    assertThat(aggregate.id()).isEqualTo(accountAggregate.id());
    assertThat(aggregate.number()).isEqualTo(accountAggregate.number());
    assertThat(aggregate.availableBalance().amount()).isEqualTo(accountAggregate.availableBalance().amount());
    assertThat(aggregate.blockedBalance().doesNotContain(block.id())).isFalse();
    // and has no in events
    assertThat(aggregate.getAndClearInEvents()).isEmpty();
    // and has no out events
    assertThat(aggregate.getAndClearOutEvents()).isEmpty();
    // and events have been published
    assertThat(eventPublisher.eventsStore())
        .hasSize(5);
    assertThat(eventPublisher.eventsStore())
        .element(0)
        .satisfies(it -> {
          if (it instanceof AccountCreated event) {
            assertThat(event.header().id().value()).isEqualByComparingTo(1L);
            assertThat(event.header().aggregateId()).isEqualTo(accountAggregate.id());
            assertThat(event.header().timestamp()).isNotNull();
            assertThat(event.currency()).isEqualTo(accountAggregate.availableBalance().amount().currency());
            assertThat(event.number()).isEqualTo(accountAggregate.number());
          } else {
            Assertions.fail("Expected event %s, got %s".formatted(
                AccountCreated.class.getSimpleName(),
                it.getClass().getSimpleName()
            ));
          }
        });
    assertThat(eventPublisher.eventsStore())
        .element(1)
        .satisfies(it -> {
          if (it instanceof AmountDeposited event) {
            assertThat(event.header().id().value()).isEqualByComparingTo(2L);
            assertThat(event.header().aggregateId()).isEqualTo(accountAggregate.id());
            assertThat(event.header().timestamp()).isNotNull();
            assertThat(event.amount()).isEqualTo(depositedAmount1);
          } else {
            Assertions.fail("Expected event %s, got %s".formatted(
                AccountCreated.class.getSimpleName(),
                it.getClass().getSimpleName()
            ));
          }
        });
    assertThat(eventPublisher.eventsStore())
        .element(2)
        .satisfies(it -> {
          if (it instanceof AmountBlocked event) {
            assertThat(event.header().id().value()).isEqualByComparingTo(3L);
            assertThat(event.header().aggregateId()).isEqualTo(accountAggregate.id());
            assertThat(event.header().timestamp()).isNotNull();
            assertThat(event.block()).isEqualTo(block);
          } else {
            Assertions.fail("Expected event %s, got %s".formatted(
                AccountCreated.class.getSimpleName(),
                it.getClass().getSimpleName()
            ));
          }
        });
    assertThat(eventPublisher.eventsStore())
        .element(3)
        .satisfies(it -> {
          if (it instanceof AmountDeposited event) {
            assertThat(event.header().id().value()).isEqualByComparingTo(4L);
            assertThat(event.header().aggregateId()).isEqualTo(accountAggregate.id());
            assertThat(event.header().timestamp()).isNotNull();
            assertThat(event.amount()).isEqualTo(depositedAmount2);
          } else {
            Assertions.fail("Expected event %s, got %s".formatted(
                AccountCreated.class.getSimpleName(),
                it.getClass().getSimpleName()
            ));
          }
        });
    assertThat(eventPublisher.eventsStore())
        .element(4)
        .satisfies(it -> {
          if (it instanceof AmountWithdrawn event) {
            assertThat(event.header().id().value()).isEqualByComparingTo(5L);
            assertThat(event.header().aggregateId()).isEqualTo(accountAggregate.id());
            assertThat(event.header().timestamp()).isNotNull();
            assertThat(event.amount()).isEqualTo(withdrawnAmount);
          } else {
            Assertions.fail("Expected event %s, got %s".formatted(
                AccountCreated.class.getSimpleName(),
                it.getClass().getSimpleName()
            ));
          }
        });
  }
}