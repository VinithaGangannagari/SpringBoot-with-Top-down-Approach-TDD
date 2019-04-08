package com.example.demo;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CarRepository extends CrudRepository<Car, Long> {
    Car findByName(String name);
    Optional<Car> findByType(String type);
    Car findByDescription(String desc);
    Car deleteByName(String name);
}
