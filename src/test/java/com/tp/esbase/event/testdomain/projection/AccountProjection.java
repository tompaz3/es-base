package com.tp.esbase.event.testdomain.projection;

import static lombok.AccessLevel.PACKAGE;

import com.tp.esbase.event.DomainEvent;
import com.tp.esbase.event.Projection;
import com.tp.esbase.event.ProjectionResult;
import com.tp.esbase.event.testdomain.AccountId;
import com.tp.esbase.event.testdomain.event.AccountCreated;
import com.tp.esbase.event.testdomain.event.AmountBlocked;
import com.tp.esbase.event.testdomain.event.AmountCaptured;
import com.tp.esbase.event.testdomain.event.AmountDeposited;
import com.tp.esbase.event.testdomain.event.AmountReleased;
import com.tp.esbase.event.testdomain.event.AmountWithdrawn;
import com.tp.esbase.event.testdomain.projection.Account.Block;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = PACKAGE)
class AccountProjection implements Projection<ProjectionResult> {

  private final AccountProjectionRepository accountProjectionRepository;

  @Override
  public List<Class<? extends DomainEvent<?>>> supportedEvents() {
    return List.of(
        AccountCreated.class,
        AmountBlocked.class,
        AmountCaptured.class,
        AmountDeposited.class,
        AmountReleased.class,
        AmountWithdrawn.class
    );
  }

  @Override
  public ProjectionResult handle(DomainEvent<?> event) {
    try {
      switch (event) {
        case AccountCreated it -> handle(it);
        case AmountBlocked it -> handle(it);
        case AmountCaptured it -> handle(it);
        case AmountDeposited it -> handle(it);
        case AmountReleased it -> handle(it);
        case AmountWithdrawn it -> handle(it);
        default -> log.info("Ignored unsupported event {}", event.getClass().getSimpleName());
      }
      return ProjectionResult.success();
    } catch (Exception exception) {
      log.error("Failed to handle event {}", event, exception);
      return ProjectionResult.failure(exception, "Failed to handle event");
    }
  }

  private void handle(AccountCreated event) {
    var account = new Account(
        event.header().aggregateId().value(),
        event.number().iban().value(),
        event.currency().name(),
        BigDecimal.ZERO
    );
    accountProjectionRepository.save(account);
    log.info("Account stored {}", account.id());
  }

  private void handle(AmountBlocked event) {
    findById(event)
        .map(account ->
            account
                .subtractBalance(event.block().amount().value())
                .addBlock(new Block(
                    event.block().id().value(),
                    event.block().amount().value()
                ))
        )
        .map(accountProjectionRepository::save)
        .ifPresent(account ->
            log.info(
                "Amount blocked {}: {} for account {}",
                event.block().id().value(),
                event.block().amount().value(),
                account.id()
            )
        );
  }

  private void handle(AmountCaptured event) {
    findById(event)
        .map(account ->
            account.removeBlock(event.blockId().value())
        )
        .map(accountProjectionRepository::save)
        .ifPresent(account ->
            log.info(
                "Amount captured from block {} for account {}",
                event.blockId().value(),
                account.id()
            )
        );
  }

  private void handle(AmountDeposited event) {
    findById(event)
        .map(account ->
            account.addBalance(event.amount().value())
        )
        .map(accountProjectionRepository::save)
        .ifPresent(account ->
            log.info(
                "Amount {} deposited to account {}",
                event.amount().value(),
                account.id()
            )
        );
  }

  private void handle(AmountReleased event) {
    findById(event)
        .map(account ->
            account.getBlock(event.blockId())
                .map(block -> account
                    .addBalance(block.amount())
                    .removeBlock(block.id())
                )
                .orElse(account)
        )
        .map(accountProjectionRepository::save)
        .ifPresent(account ->
            log.info(
                "Amount released from block {} for account {}",
                event.blockId().value(),
                account.id()
            )
        );
  }

  private void handle(AmountWithdrawn event) {
    findById(event)
        .map(account -> account.subtractBalance(event.amount().value()))
        .map(accountProjectionRepository::save)
        .ifPresent(account -> log.info(
            "Amount {} withdrawn from account {}",
            event.amount().value(),
            account.id()
        ));
  }

  private Optional<Account> findById(DomainEvent<AccountId> event) {
    return accountProjectionRepository.findById(event.header().aggregateId().value());
  }
}
