package com.example.corenet.admin.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "board_manager")
public class BoardManager {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String boardCode; // 숫자 or 문자

    @Column(nullable = false)
    private String boardName;  

    @Column(nullable = true)
    private Integer deptCode; // 숫자로 변경...

}