package com.example.identity.interfaces.rest;

import com.example.identity.application.dto.CreateIdentityCommand;
import com.example.identity.application.dto.IdentityResponse;
import com.example.identity.application.dto.UpdateIdentityCommand;
import com.example.identity.application.service.IdentityApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/identitys")
public class IdentityController {
    private final IdentityApplicationService identityApplicationService;

    public IdentityController(IdentityApplicationService identityApplicationService) {
        this.identityApplicationService = identityApplicationService;
    }

    @PostMapping
    public IdentityResponse create(@RequestBody CreateIdentityCommand command) {
        return identityApplicationService.create(command);
    }

    @GetMapping
    public List<IdentityResponse> getAll() {
        return identityApplicationService.getAll();
    }

    @GetMapping("/{id}")
    public IdentityResponse getById(@PathVariable String id){
        return identityApplicationService.getById(id);
    }

    @PutMapping("/{id}")
    public  IdentityResponse update(@PathVariable String id, @RequestBody UpdateIdentityCommand command){
        return identityApplicationService.update(id, command);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id){
        identityApplicationService.delete(id);
    }

}
