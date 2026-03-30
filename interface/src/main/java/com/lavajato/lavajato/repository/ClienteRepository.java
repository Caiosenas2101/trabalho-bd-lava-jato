package com.lavajato.lavajato.repository;

import com.lavajato.lavajato.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}