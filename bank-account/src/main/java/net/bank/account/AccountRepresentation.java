package net.bank.account;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountRepresentation extends RepresentationModel<AccountRepresentation> {

    private String holder;

    private Double amount;
}
