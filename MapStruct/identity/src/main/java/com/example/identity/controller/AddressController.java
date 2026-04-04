package com.example.identity.controller;

import com.example.identity.dto.request.AddressCreateRequest;
import com.example.identity.dto.request.AddressUpdateResquest;
import com.example.identity.entity.Address;
import com.example.identity.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @PostMapping
    Address createAdress(@RequestBody AddressCreateRequest request){
        return addressService.createAddress(request);
    }

    @PutMapping("/{addressId}")
    Address updateAddress(@RequestBody AddressUpdateResquest request, @PathVariable String addressId){
        return addressService.upadateAddress(request, addressId);
    }

    @GetMapping
    List<Address> getAll(){
        return addressService.getAll();
    }

    @GetMapping("/{addressId}")
    Address getAddressById(@PathVariable String addressId){
        return addressService.getAddressById(addressId);
    }

    @DeleteMapping("/{addressId}")
    String deleteAddressById(@PathVariable String addressId){
        addressService.deleteAddress(addressId);
        return "Address deleted";
    }
}
