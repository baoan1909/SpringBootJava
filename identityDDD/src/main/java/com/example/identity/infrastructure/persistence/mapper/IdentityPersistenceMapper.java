package com.example.identity.infrastructure.persistence.mapper;

import com.example.identity.domain.model.Identity;
import com.example.identity.infrastructure.persistence.entity.IdentityJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class IdentityPersistenceMapper {
    public IdentityJpaEntity toEntity(Identity identity) {
        return new IdentityJpaEntity(
                identity.getId(),
                identity.getUsername(),
                identity.getPassword(),
                identity.getFirstname(),
                identity.getLastname(),
                identity.getDob()
        );
    }

    public Identity toDomain(IdentityJpaEntity identityJpaEntity){
        return new Identity(
                identityJpaEntity.getId(),
                identityJpaEntity.getUsername(),
                identityJpaEntity.getPassword(),
                identityJpaEntity.getFirstname(),
                identityJpaEntity.getLastname(),
                identityJpaEntity.getDob()
        );
    }
}
