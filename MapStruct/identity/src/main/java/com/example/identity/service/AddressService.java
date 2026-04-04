package com.example.identity.service;

import com.example.identity.dto.request.AddressCreateRequest;
import com.example.identity.dto.request.AddressUpdateResquest;
import com.example.identity.entity.Address;
import com.example.identity.mapper.AddressMapper;
import com.example.identity.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressMapper addressMapper;

    public Address createAddress(AddressCreateRequest request) {
        Address address = addressMapper.createAddress(request);
        return addressRepository.save(address);
    }

    public Address upadateAddress(AddressUpdateResquest resquest, String addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        addressMapper.updateAddress(resquest, address);
        return addressRepository.save(address);
    }

    public List<Address> getAll() {
        List<Address> addresses = addressRepository.findAll();
        return addressMapper.toListAddressResponse(addresses);
    }

    public Address getAddressById(String addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }

    public void deleteAddress(String addressId) {
        addressRepository.deleteById(addressId);
    }
}
