package com.tp.esbase.event.testdomain.projection;

import com.tp.esbase.event.testdomain.Block.BlockId;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Account {

  private final String id;
  private final String number;
  private final String currency;
  private BigDecimal balance;
  private final Map<String, BigDecimal> blocks;

  public Account(
      String id,
      String number,
      String currency,
      BigDecimal balance
  ) {
    this.id = id;
    this.number = number;
    this.currency = currency;
    this.balance = balance;
    this.blocks = new HashMap<>();
  }

  public Account addBlock(Block block) {
    this.balance = this.balance.subtract(block.amount());
    return this;
  }

  public Account removeBlock(String blockId) {
    this.blocks.remove(blockId);
    return this;
  }

  public Account addBalance(BigDecimal amount) {
    this.balance = this.balance.add(amount);
    return this;
  }

  public Map<String, BigDecimal> blocks() {
    return Collections.unmodifiableMap(this.blocks);
  }

  public Account subtractBalance(BigDecimal amount) {
    this.balance = this.balance.subtract(amount);
    return this;
  }

  public Optional<Block> getBlock(BlockId blockId) {
    return Optional.ofNullable(this.blocks.get(blockId.value()))
        .map(amount -> new Block(blockId.value(), amount));
  }

  public record Block(
      String id,
      BigDecimal amount
  ) {

  }

}
