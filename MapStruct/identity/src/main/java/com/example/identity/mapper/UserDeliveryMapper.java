package com.example.identity.mapper;

import com.example.identity.dto.response.UserDeliveryResponse;
import com.example.identity.entity.Address;
import com.example.identity.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDeliveryMapper {
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstname", target = "firstname")
    @Mapping(source = "user.lastname", target = "lastname")
    @Mapping( source = "address.streetName", target = "streetName")
    @Mapping(source = "address.cityName", target = "cityName")
    UserDeliveryResponse toUserDeliveryResponse(User user, Address address);
}
