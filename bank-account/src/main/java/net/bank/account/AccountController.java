package net.bank.account;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * https://restfulapi.net/http-status-codes/
 */
@Slf4j
@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private AccountAssemblerProcessor assembler;

    @GetMapping
    public CollectionModel<AccountRepresentation> all() {
        return assembler.toCollectionModel(repository.findAll());
    }

    @GetMapping("/{holder}")
    public HttpEntity<AccountRepresentation> one(@PathVariable String holder) {
        return repository.findByHolder(holder).map(
                found -> {
                    return new ResponseEntity<>(assembler.toModel(found), HttpStatus.OK);
                }).orElseGet(() -> {
                    log.info("Account for holder {} was not found.", holder);
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
        );
    }

    @PostMapping
    public HttpEntity<AccountRepresentation> add(@RequestBody Account account) {
        assembler.validate(account);

        Account model = null;
        try {
            model = repository.save(account);
        } catch (DuplicateKeyException e) {
            log.info("Account for {} already exists.", account.getHolder(), e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account for this holder already exists.",e);
        }
        return new ResponseEntity<>(assembler.toModel(model), HttpStatus.CREATED);
    }

    @PutMapping("/{holder}")
    public HttpEntity<AccountRepresentation> update(@PathVariable String holder, @RequestBody Account account) {
        assembler.validate(account);
        Account found = repository.findByHolder(holder)
                .map(
                        accountFound -> {
                            accountFound = assembler.copyData(accountFound, account);
                            return accountFound;
                        }).orElseGet(() -> {
                            log.info("Failed to save {}: Account for holder {} was not found.", account, holder);
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                        }
                );
        Account saved = repository.save(found);
        log.info("Searched for holder {}, updated Account {}.", holder, saved);
        return new ResponseEntity<>(assembler.toModel(saved), HttpStatus.OK);
    }

    @DeleteMapping("/{holder}")
    public void delete(@PathVariable String holder) {
        if (repository.findByHolder(holder).isPresent()) {
            repository.deleteByHolder(holder);
        } else {
            log.info("Failed to delete Account: Account for holder {} was not found.", holder);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}
