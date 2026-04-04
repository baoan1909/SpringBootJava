package com.example.identity.service;

import com.example.identity.dto.request.UserCreationRequest;
import com.example.identity.dto.response.UserDeliveryResponse;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.dto.request.UserUpdateRequest;
import com.example.identity.entity.Address;
import com.example.identity.entity.User;
import com.example.identity.mapper.UserDeliveryMapper;
import com.example.identity.mapper.UserMapper;
import com.example.identity.repository.AddressRepository;
import com.example.identity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserDeliveryMapper userDeliveryMapper;

    @Autowired
    private UserMapper userMapper;

    public User createUser(UserCreationRequest request){
//        User user = new User();
//
//        user.setUsername(request.getUsername());
//        user.setPassword(request.getPassword());
//        user.setFirstname(request.getFirstname());
//        user.setLastname(request.getLastname());
//        user.setDob(request.getDob());
        User user = userMapper.createUser(request);
        return userRepository.save(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

//        user.setPassword(request.getPassword());
//        user.setFirstname(request.getFirstname());
//        user.setLastname(request.getLastname());
//        user.setDob(request.getDob());
        userMapper.updateUser(request, user);
        User userUpdated = userRepository.save(user);
        return userMapper.toUserResponse(userUpdated);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public List<UserResponse> getUsers(){
        List<User> users = userRepository.findAll();
        return userMapper.toListUserResponse(users);
    }

    public UserResponse getUser(String id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserResponse(user);
    }

    public UserDeliveryResponse getUserDelivery(String userId, String addressId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        return userDeliveryMapper.toUserDeliveryResponse(user, address);
    }
}
