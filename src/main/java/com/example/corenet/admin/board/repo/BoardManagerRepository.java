package com.example.corenet.admin.board.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.corenet.admin.board.entity.BoardManager;
import java.util.List;


public interface BoardManagerRepository extends JpaRepository<BoardManager, Long>{
    Optional<BoardManager> findByBoardCode(String boardCode);

    Optional<BoardManager> findByBoardName(String boardName);
    List<BoardManager> findByDeptCode(Integer deptCode);

    // 부서 수 카운트
    @Query("SELECT COUNT(DISTINCT b.deptCode) FROM BoardManager b WHERE b.boardCode = :boardCode")
    Integer countDistinctDeptByBoardCode(@Param("boardCode") String boardCode);
    
}