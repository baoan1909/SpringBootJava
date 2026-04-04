package com.example.identity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="address")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    private String id;

    @Column(name="streetName")
    private String streetName;

    @Column(name="cityName")
    private String cityName;
}
