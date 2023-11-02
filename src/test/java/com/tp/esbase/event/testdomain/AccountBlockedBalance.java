package com.tp.esbase.event.testdomain;

import com.tp.esbase.event.testdomain.Block.BlockId;
import java.util.HashMap;
import java.util.Map;

public final class AccountBlockedBalance {

  private final Map<BlockId, Amount> blocks = new HashMap<>();

  public void block(Block block) {
    this.blocks.merge(block.id(), block.amount(), (prev, curr) -> prev.add(curr.value()));
  }

  public Block release(BlockId blockId) {
    var amount = this.blocks.remove(blockId);
    return new Block(blockId, amount);
  }

  public boolean doesNotContain(BlockId blockId) {
    return !contains(blockId);
  }

  public boolean contains(BlockId blockId) {
    return this.blocks.containsKey(blockId);
  }
}
