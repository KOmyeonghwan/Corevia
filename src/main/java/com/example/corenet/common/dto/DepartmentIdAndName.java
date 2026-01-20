package com.example.corenet.common.dto;

public interface DepartmentIdAndName {
    //projection
    Integer getId();
    String getDepartmentName();

    /* 
    position
          ('대표', 0),
          ('부장', 1),
          ('과장', 2),
          ('대리', 3),
          ('사원', 4),
          ('시스템관리자', 10),
          (‘외부시스템관리자', 11);

    departments
          (1, ‘예외’),
          (101, '개발'),
          (102, '인사'),
          (103, '마케팅'),
          (104, '기획');
    */
}