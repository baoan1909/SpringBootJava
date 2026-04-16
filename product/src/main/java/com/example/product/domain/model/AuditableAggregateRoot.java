package com.example.product.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
public abstract class AuditableAggregateRoot<A extends AbstractAggregateRoot<A>> extends AbstractAggregateRoot<A> {
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    public Collection<Object> getDomainEvents() {
        return super.domainEvents();
    }

    public void clearAllDomainEvents() {
        super.clearDomainEvents();
    }
}
