package com.example.corenet.admin.users.service;

import com.example.corenet.admin.users.entity.Position;
import com.example.corenet.admin.users.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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