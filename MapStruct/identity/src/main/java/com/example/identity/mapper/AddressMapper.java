package com.example.identity.mapper;

import com.example.identity.dto.request.AddressCreateRequest;
import com.example.identity.dto.request.AddressUpdateResquest;
import com.example.identity.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address createAddress(AddressCreateRequest addressCreateRequest);
    void updateAddress(AddressUpdateResquest addressUpdateResquest, @MappingTarget Address address);
    List<Address> toListAddressResponse(List<Address> addresses);
}
