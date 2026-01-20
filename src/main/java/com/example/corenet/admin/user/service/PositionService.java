package com.example.corenet.admin.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.corenet.admin.user.repository.PositionRepository;
import com.example.corenet.entity.Position;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    public Position getPositionById(Integer id) {
        return positionRepository.findById(id).orElse(null);
    }

    public Position savePosition(Position position) {
        return positionRepository.save(position);
    }

    public void deletePosition(Integer id) {
        positionRepository.deleteById(id);
    }
}