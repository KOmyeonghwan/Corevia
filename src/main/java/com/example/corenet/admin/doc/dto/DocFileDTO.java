package com.example.corenet.admin.doc.dto;

import java.time.LocalDate;

public class DocFileDTO {
    
    private String path;        // 파일 경로
    private String name;        // 파일 이름
    private long size;          // 파일 크기
    private LocalDate createdDate; // 파일 생성일

    // 기본 생성자
    public DocFileDTO() {}

    // 모든 필드를 포함한 생성자
    public DocFileDTO(String path, String name, long size, LocalDate createdDate) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.createdDate = createdDate;
    }

    // Getter / Setter
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "DocFileDTO{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", createdDate=" + createdDate +
                '}';
    }
}