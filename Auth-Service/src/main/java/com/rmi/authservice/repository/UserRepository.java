package com.rmi.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rmi.authservice.model.AppUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    List<AppUser> findByIdIn(List<Long> userIds);

    Optional<AppUser> findByUsername(String username);
    
    Boolean existsByUsername(String username);
    
}