package com.example.systemserviceofficial.system.repository;

import com.example.systemserviceofficial.system.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByCode(String code);
    
    boolean existsByCode(String code);
    
    @Query("SELECT r FROM Role r JOIN FETCH r.permissions WHERE r.id = :id AND r.deletedAt IS NULL")
    Optional<Role> findByIdWithPermissions(@Param("id") Long id);
    
    Page<Role> findByDeletedAtIsNull(Pageable pageable);
    
    @Query("SELECT r FROM Role r WHERE r.deletedAt IS NULL AND " +
           "(LOWER(r.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Role> searchRoles(@Param("search") String search, Pageable pageable);
}
