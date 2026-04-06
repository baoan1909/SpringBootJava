package com.example.identity.mapper;

import com.example.identity.dto.response.UserDeliveryResponse;
import com.example.identity.entity.Address;
import com.example.identity.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserDeliveryMapper {
    @Mapping(source = "user.username", target = "username")
//    @Mapping(source = "user.firstname", target = "firstname")
//    @Mapping(source = "user.lastname", target = "lastname")
//    @Mapping( source = "address.streetName", target = "streetName")
//    @Mapping(source = "address.cityName", target = "cityName")
    UserDeliveryResponse toUserDeliveryResponse(User user, Address address);

    @BeforeMapping
    default void validateData(User user, Address address) {
        if (user.getFirstname() == null || user.getFirstname().isBlank()) {
            throw new RuntimeException("FirstName không tìm thấy");
        }
    }

    @AfterMapping
    default void buildUserDeliveryResponse(@MappingTarget UserDeliveryResponse response, User user, Address address) {
        if (user.getFirstname() != null && user.getLastname() != null) {
            String fullName = user.getFirstname() + " " + user.getLastname();
            response.setFullname( fullName );
        }else {
            response.setFullname("Khách vô danh");
        }

        if (address != null) {
            String fullAddress = address.getStreetName() + " " + address.getCityName();
            response.setFullAddress( fullAddress );
        }
    }
}
