package com.example.product.infrastructure.persistence.mapper;

import com.example.product.domain.model.Account;
import com.example.product.infrastructure.persistence.entity.AccountJpaEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountEntityMapper {
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    AccountJpaEntity toAccountJpaEntity(Account account);
    @AfterMapping
    default void transferEvents(Account account, @MappingTarget AccountJpaEntity accountJpaEntity) {
        if(account.getDomainEvents() != null && !account.getDomainEvents().isEmpty()){
            account.getDomainEvents().forEach(accountJpaEntity::addDomainEvent);
        }

        account.clearAllDomainEvents();
    }

    Account toAccountDomain (AccountJpaEntity accountJpaEntity);

    @Mapping(target = "id", ignore = true)
    void updateAccountDomain(Account account, @MappingTarget AccountJpaEntity accountJpaEntity);
}
