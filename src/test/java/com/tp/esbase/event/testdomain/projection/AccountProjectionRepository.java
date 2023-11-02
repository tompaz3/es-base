package com.tp.esbase.event.testdomain.projection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class AccountProjectionRepository {

  private final Map<String, Account> store = new HashMap<>();


  public Account save(Account account) {
    return store.put(account.id(), account);
  }

  public Optional<Account> findById(String id) {
    return Optional.ofNullable(store.get(id));
  }
}
