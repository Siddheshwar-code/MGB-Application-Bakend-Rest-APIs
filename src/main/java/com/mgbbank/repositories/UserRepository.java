package com.mgbbank.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mgbbank.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Boolean existsByEmail(String emial);
	Optional<User>findByEmail(String email);
	Boolean existsByAccountNumber(String accountNumber);
	User findByAccountNumber(String accoountNumber);
}
