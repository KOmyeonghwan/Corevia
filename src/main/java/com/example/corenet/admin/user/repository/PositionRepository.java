package com.example.corenet.admin.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.corenet.entity.Position;


@Repository
public interface PositionRepository extends JpaRepository<Position, Integer> {

    
}