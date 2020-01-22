package net.bank.account;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String> {

    Optional<Account> findByHolder(String holder);

    void deleteByHolder(String holder);

}
