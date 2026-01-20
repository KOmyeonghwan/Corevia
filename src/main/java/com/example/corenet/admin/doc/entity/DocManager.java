package com.example.corenet.admin.doc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "doc_manager")
public class DocManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String docCode;

    @Column(nullable = false)
    private String docName;

    @Column(nullable = false)
    private Integer deptCode;

    @Column(nullable = false, length = 1)
    private int useApproval = 0;
    
    @Column(nullable = false, length = 1)
    private int useFile = 0;

}
