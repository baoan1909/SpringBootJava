package com.example.product.infrastructure.persistence.repository;

import com.example.product.domain.model.Account;
import com.example.product.domain.repository.AccountRepository;
import com.example.product.infrastructure.persistence.entity.AccountJpaEntity;
import com.example.product.infrastructure.persistence.entity.RoleJpaEntity;
import com.example.product.infrastructure.persistence.mapper.AccountEntityMapper;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountEntityMapper accountEntityMapper;
    private final SpringDataAccountRepository springDataAccountRepository;
    private final EntityManager entityManager;

    public AccountRepositoryImpl(AccountEntityMapper accountEntityMapper, SpringDataAccountRepository springDataAccountRepository, EntityManager entityManager) {
        this.accountEntityMapper = accountEntityMapper;
        this.springDataAccountRepository = springDataAccountRepository;

        this.entityManager = entityManager;
    }

    @Override
    public Account save(Account account) {
        AccountJpaEntity accountJpaEntity = accountEntityMapper.toAccountJpaEntity(account);

        if(accountJpaEntity.getRoles() != null){
            Set<RoleJpaEntity> managedRoles = accountJpaEntity.getRoles()
                    .stream().map(role -> entityManager.getReference(RoleJpaEntity.class, role.getId()))
                    .collect(Collectors.toSet());
            accountJpaEntity.setRoles(managedRoles);
        }
        AccountJpaEntity accountSaved = springDataAccountRepository.save(accountJpaEntity);

        return accountEntityMapper.toAccountDomain(accountSaved);
    }

    @Override
    public boolean exitsEmail(String email) {
        return springDataAccountRepository.existsByEmail(email);
    }
}
