package com.example.systemserviceofficial.system.repository;


import com.example.systemserviceofficial.system.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByCode(String code);
    
    boolean existsByCode(String code);
    
    Page<Permission> findByDeletedAtIsNull(Pageable pageable);
    
    @Query("SELECT p FROM Permission p WHERE p.deletedAt IS NULL AND " +
           "(LOWER(p.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Permission> searchPermissions(@Param("search") String search, Pageable pageable);
}
