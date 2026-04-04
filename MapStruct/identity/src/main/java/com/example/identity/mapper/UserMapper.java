package com.example.identity.mapper;

import com.example.identity.dto.request.UserCreationRequest;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.dto.request.UserUpdateRequest;
import com.example.identity.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User createUser(UserCreationRequest userCreationRequest);
    void updateUser(UserUpdateRequest userUpdateRequest, @MappingTarget User user);
    UserResponse toUserResponse(User user);
    List<UserResponse> toListUserResponse(List<User> users);


}
