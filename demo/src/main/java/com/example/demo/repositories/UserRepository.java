package com.example.demo.repositories;

import java.util.*;
import com.example.demo.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {
	boolean existsByEmail(String email);
	boolean existsByCpf(String cpf);

}