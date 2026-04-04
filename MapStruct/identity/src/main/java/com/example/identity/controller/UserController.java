package com.example.identity.controller;

import com.example.identity.dto.request.UserCreationRequest;
import com.example.identity.dto.response.UserDeliveryResponse;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.dto.request.UserUpdateRequest;
import com.example.identity.entity.User;
import com.example.identity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    User createUser(@RequestBody UserCreationRequest request){
        return userService.createUser(request);
    }

    @GetMapping
    List<UserResponse> getUsers() {
        return userService.getUsers();
    }
    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return "User deleted";
    }

    @GetMapping("/{userId}/delivery/{addressId}")
    UserDeliveryResponse getUserDelivery(@PathVariable String userId, @PathVariable String addressId){
       return userService.getUserDelivery(userId, addressId);
    }

}
