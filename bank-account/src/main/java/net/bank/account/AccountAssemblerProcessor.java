package net.bank.account;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * https://docs.spring.io/spring-hateoas/docs/current/reference/html/
 */
@Slf4j
@Component
public class AccountAssemblerProcessor extends RepresentationModelAssemblerSupport<Account, AccountRepresentation>
    implements RepresentationModelProcessor<AccountRepresentation> {

    @Autowired
    private Validator validator;

    public AccountAssemblerProcessor() {
        super(AccountController.class, AccountRepresentation.class);
    }

    @Override
    public AccountRepresentation toModel(Account account) {

        AccountRepresentation accountRepresentation =
                AccountRepresentation.builder()
                        .holder(account.getHolder())
                        .amount(account.getAmount()).build();

        return accountRepresentation;
    }

    public Account copyData(Account to, Account from) {
        to.setAmount(from.getAmount());
        return to;
    }

    @Override
    public AccountRepresentation process(AccountRepresentation model) {
        boolean fitToProcess = model != null && model.getHolder() != null;
        if (fitToProcess) {
            model.add(linkTo(methodOn(AccountController.class).one(model.getHolder())).withSelfRel());
        }
        return model;
    }

    public void validate(Account account) {
        Set<ConstraintViolation<Account>> ruleViolations = validator.validate(account);
        boolean encounteredRuleViolations = ruleViolations != null && ruleViolations.size() > 0;
        if (encounteredRuleViolations) {
            StringBuilder sb = new StringBuilder();
            sb.append("Rule violations encountered while creating an Account");
            for (ConstraintViolation constraintViolation: ruleViolations) {
                sb.append("; ");
                sb.append(constraintViolation.getMessage());
            }
            sb.append(".");
            log.info("Error: {} | Object: {}", sb.toString(), account);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, sb.toString());
        }
    }
}
