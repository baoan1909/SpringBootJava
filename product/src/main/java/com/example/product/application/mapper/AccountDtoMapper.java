package com.example.product.application.mapper;

import com.example.product.application.dto.response.AccountResponse;
import com.example.product.domain.model.Account;
import com.example.product.domain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AccountDtoMapper {
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStrings")
    AccountResponse toAccountResponse(Account account);

    @Named("mapRolesToStrings")
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return new HashSet<>();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
