package com.tp.esbase.event.testdomain;

import com.tp.esbase.event.testdomain.Block.BlockId;
import java.util.HashMap;
import java.util.Map;

public final class AccountBlockedBalance {

  private final Map<BlockId, Amount> blocks = new HashMap<>();

  public void block(Block block) {
    this.blocks.merge(block.id(), block.amount(), (prev, curr) -> prev.add(curr.value()));
  }

  public void release(BlockId blockId) {
    this.blocks.remove(blockId);
  }

  public boolean doesNotContain(BlockId blockId) {
    return !this.blocks.containsKey(blockId);
  }
}
