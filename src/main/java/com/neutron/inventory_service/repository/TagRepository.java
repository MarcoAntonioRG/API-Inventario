package com.neutron.inventory_service.repository;

import com.neutron.inventory_service.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameIgnoreCase(String name);
    Optional<Tag> findByName(String name);
}
