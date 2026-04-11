package com.example.product.infrastructure.persistence.mapper;

import com.example.product.domain.model.Account;
import com.example.product.infrastructure.persistence.entity.AccountJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountEntityMapper {

    AccountJpaEntity toAccountJpaEntity(Account account);
    Account toAccountDomain (AccountJpaEntity accountJpaEntity);

    @Mapping(target = "id", ignore = true)
    void updateAccountDomain(Account account, @MappingTarget AccountJpaEntity accountJpaEntity);
}
