package com.tp.esbase.event.testdomain;

import static com.tp.esbase.event.testdomain.error.AccountBalanceTooLowForBlockException.accountBalanceTooLowForBlockException;
import static com.tp.esbase.event.testdomain.error.AccountBalanceTooLowForWithdrawalException.accountBalanceTooLowForWithdrawalException;
import static com.tp.esbase.event.testdomain.error.AccountBlockDoesNotExistException.accountBlockDoesNotExistException;
import static com.tp.esbase.event.testdomain.error.AccountUnsupportedCurrencyException.accountUnsupportedCurrencyException;
import static com.tp.esbase.event.testdomain.error.BlockAlreadyExistsException.blockAlreadyExistsException;

import com.tp.esbase.event.AggregateRoot;
import com.tp.esbase.event.AggregateType;
import com.tp.esbase.event.DomainEvent;
import com.tp.esbase.event.DomainEvent.EventId;
import com.tp.esbase.event.Version;
import com.tp.esbase.event.testdomain.Block.BlockId;
import com.tp.esbase.event.testdomain.event.AccountCreated;
import com.tp.esbase.event.testdomain.event.AmountBlocked;
import com.tp.esbase.event.testdomain.event.AmountCaptured;
import com.tp.esbase.event.testdomain.event.AmountDeposited;
import com.tp.esbase.event.testdomain.event.AmountReleased;
import com.tp.esbase.event.testdomain.event.AmountWithdrawn;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class AccountAggregate extends AggregateRoot<AccountId> {

  private static final AggregateType AGGREGATE_TYPE = new AggregateType("account");

  private AccountNumber number;
  private AccountBalance availableBalance;
  private AccountBlockedBalance blockedBalance;

  public AccountAggregate(Version version, EventId latestEvent) {
    super(AGGREGATE_TYPE, version, latestEvent);
  }

  public AccountAggregate(AccountId id, AccountNumber number, Currency currency) {
    this(Version.initial(), EventId.initial());
    var eventHeader = nextDomainEventHeader(id);
    var createdEvent = new AccountCreated(eventHeader, number, currency);
    handleAndRegisterEvent(createdEvent);
  }

  public void deposit(Amount amount) {
    checkSupportedCurrency(amount.currency());
    var event = new AmountDeposited(nextDomainEventHeader(), amount);
    handleAndRegisterEvent(event);
  }

  public void withdraw(Amount amount) {
    checkSupportedCurrency(amount.currency());
    if (this.availableBalance.isLowerThan(amount)) {
      throw accountBalanceTooLowForWithdrawalException(
          this.id,
          this.availableBalance.amount(),
          amount
      );
    }
    var event = new AmountWithdrawn(nextDomainEventHeader(), amount);
    handleAndRegisterEvent(event);
  }

  public void block(Block block) {
    if (this.blockedBalance.contains(block.id())) {
      throw blockAlreadyExistsException(
          this.id,
          block
      );
    }
    checkSupportedCurrency(block.amount().currency());
    if (this.availableBalance.isLowerThan(block.amount())) {
      throw accountBalanceTooLowForBlockException(
          this.id,
          this.availableBalance.amount(),
          block
      );
    }
    var event = new AmountBlocked(nextDomainEventHeader(), block);
    handleAndRegisterEvent(event);
  }

  public void capture(BlockId blockId) {
    if (this.blockedBalance.doesNotContain(blockId)) {
      throw accountBlockDoesNotExistException(
          this.id,
          blockId
      );
    }
    var event = new AmountCaptured(nextDomainEventHeader(), blockId);
    handleAndRegisterEvent(event);
  }

  public void release(BlockId blockId) {
    if (this.blockedBalance.doesNotContain(blockId)) {
      throw accountBlockDoesNotExistException(
          this.id,
          blockId
      );
    }
    var event = new AmountReleased(nextDomainEventHeader(), blockId);
    handleAndRegisterEvent(event);
  }

  private void checkSupportedCurrency(Currency currency) {
    if (isUnsupportedCurrency(currency)) {
      throw accountUnsupportedCurrencyException(
          this.id,
          this.availableBalance.amount().currency(),
          currency
      );
    }
  }

  private boolean isUnsupportedCurrency(Currency currency) {
    return !this.availableBalance.amount().currency().equals(currency);
  }

  @Override
  protected void handle(DomainEvent<AccountId> event) {
    switch (event) {
      case AccountCreated it -> handle(it);
      case AmountDeposited it -> handle(it);
      case AmountWithdrawn it -> handle(it);
      case AmountBlocked it -> handle(it);
      case AmountCaptured it -> handle(it);
      case AmountReleased it -> handle(it);
      default -> {
        // ignore unsupported events
      }
    }
  }

  private void handle(AccountCreated event) {
    this.id = event.header().aggregateId();
    this.number = event.number();
    this.availableBalance = new AccountBalance(new Amount(event.currency(), BigDecimal.ZERO));
    this.blockedBalance = new AccountBlockedBalance();
    registerOutEvent(event);
  }

  private void handle(AmountDeposited event) {
    this.availableBalance = this.availableBalance.add(event.amount().value());
    registerOutEvent(event);
  }

  private void handle(AmountWithdrawn event) {
    this.availableBalance = this.availableBalance.subtract(event.amount().value());
    registerOutEvent(event);
  }

  private void handle(AmountBlocked event) {
    this.availableBalance = this.availableBalance.subtract(event.block().amount().value());
    this.blockedBalance.block(event.block());
    registerOutEvent(event);
  }

  private void handle(AmountCaptured event) {
    this.blockedBalance.release(event.blockId());
    registerOutEvent(event);
  }

  private void handle(AmountReleased event) {
    var block = this.blockedBalance.release(event.blockId());
    this.availableBalance = this.availableBalance.add(block.amount().value());
    registerOutEvent(event);
  }
}
