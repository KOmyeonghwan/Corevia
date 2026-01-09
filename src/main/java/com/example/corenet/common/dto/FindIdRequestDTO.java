package com.example.corenet.common.dto;

import lombok.Data;

@Data
public class FindIdRequestDTO {
    private String name;
    private String email;
    private String emailDomain;

    public String getFullEmail() {
        return email + "@" + emailDomain;
    }
}
