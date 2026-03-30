package com.example.identity.application.service;

import com.example.identity.application.dto.CreateIdentityCommand;
import com.example.identity.application.dto.IdentityResponse;
import com.example.identity.application.dto.UpdateIdentityCommand;
import com.example.identity.domain.exception.IdentityNotFoundException;
import com.example.identity.domain.model.Identity;
import com.example.identity.domain.repository.IdentityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class IdentityApplicationService {
    private final IdentityRepository identityRepository;

    public IdentityApplicationService(IdentityRepository identityRepository) {
        this.identityRepository = identityRepository;
    }

    public IdentityResponse create(CreateIdentityCommand command) {
        identityRepository.findByUsername(command.username()).ifPresent(identity -> {
            throw new IllegalArgumentException("Username đã tồn tại");
        });

        Identity identity = new Identity(
                null,
                command.username(),
                command.password(),
                command.firstname(),
                command.lastname(),
                command.dob()
        );
        identityRepository.save(identity);
        return toResponse(identity);
    }

    @Transactional(readOnly = true)
    public List<IdentityResponse> getAll() {
        return identityRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public IdentityResponse getById(String id) {
        Identity identity = identityRepository.findById(id)
                .orElseThrow(() -> new IdentityNotFoundException(id));
        return toResponse(identity);
    }

    public IdentityResponse update(String id, UpdateIdentityCommand command) {
        Identity identity = identityRepository.findById(id)
                .orElseThrow(() -> new IdentityNotFoundException(id));

        if (!identity.getUsername().equals(command.username())) {
            if (identityRepository.existsByUsername(command.username())) {
                throw new IllegalArgumentException("Username đã tồn tại");
            }
        }

        if (identity.getPassword().equals(command.password())) {
            throw new IllegalArgumentException("Password phải  khác với password cũ");
        }

        identity.updateInfo(command.username(), command.password(), command.firstname(), command.lastname(), command.dob());
        Identity updatedIdentity = identityRepository.save(identity);
        return toResponse(updatedIdentity);
    }

    public void delete(String id) {
        if (!identityRepository.existsById(id)) {
            throw new IdentityNotFoundException(id);
        }

        identityRepository.deleteById(id);
    }

    private IdentityResponse toResponse(Identity identity) {
        return new IdentityResponse(
                identity.getId(),
                identity.getUsername(),
                identity.getPassword(),
                identity.getFirstname(),
                identity.getLastname(),
                identity.getDob()
        );
    }
}
