package net.bank.account;

import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Qualifier
public class Account implements Serializable {

    @Id
    private String id;

    @Indexed(unique=true)
    @Pattern(regexp = "([A-Z][a-z]+)", message = "Holder must be a single word in camel-case")
    private String holder;

    private Double amount;
}
