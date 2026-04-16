package com.example.product.infrastructure.persistence.seeder;

import com.example.product.domain.model.Role;
import com.example.product.domain.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public DatabaseSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if(roleRepository.findByName(Role.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role(null, Role.ROLE_ADMIN, "Quản viên toàn quyền hệ thống");
            roleRepository.save(adminRole);
        }
        if(roleRepository.findByName(Role.ROLE_SELLER).isEmpty()) {
            Role sellerRole = new Role(null, Role.ROLE_SELLER, "Người bán hàng quản lý sản phẩm");
            roleRepository.save(sellerRole);
        }
        if(roleRepository.findByName(Role.ROLE_CUSTOMER).isEmpty()) {
            Role customerRole = new Role(null, Role.ROLE_CUSTOMER, "Khách hàng mua sắm thông thường");
            roleRepository.save(customerRole);
        }
    }
}
