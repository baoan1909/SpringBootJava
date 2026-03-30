package com.example.identity.infrastructure.persistence.repository;

import com.example.identity.domain.model.Identity;
import com.example.identity.domain.repository.IdentityRepository;
import com.example.identity.infrastructure.persistence.entity.IdentityJpaEntity;
import com.example.identity.infrastructure.persistence.mapper.IdentityPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class IdentityRepositoryImpl implements IdentityRepository {

    private final IdentityPersistenceMapper identityPersistenceMapper;
    private final SpringDataIdentityRepository springDataIdentityRepository;

    public IdentityRepositoryImpl(IdentityPersistenceMapper identityPersistenceMapper, SpringDataIdentityRepository springDataIdentityRepository) {
        this.identityPersistenceMapper = identityPersistenceMapper;
        this.springDataIdentityRepository = springDataIdentityRepository;
    }

    @Override
    public Identity save(Identity identity) {
        IdentityJpaEntity identityJpaEntity = identityPersistenceMapper.toEntity(identity);
        IdentityJpaEntity identitySaved = springDataIdentityRepository.save(identityJpaEntity);
        return identityPersistenceMapper.toDomain(identitySaved);
    }

    @Override
    public List<Identity> findAll() {
        return springDataIdentityRepository.findAll()
                .stream()
                .map(identityPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Identity> findById(String id) {
        return springDataIdentityRepository.findById(id).map(identityPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Identity> findByUsername(String username) {
        return springDataIdentityRepository.findByUsername(username).map(identityPersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        springDataIdentityRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return springDataIdentityRepository.existsById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return springDataIdentityRepository.existsByUsername(username);
    }
}
