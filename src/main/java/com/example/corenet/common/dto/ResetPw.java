package com.example.corenet.common.dto;

import lombok.Data;

@Data
public class ResetPw {
    private String password;
    private String companyEmail;

    public String getCompanyEmail(){
        return companyEmail + "@" + "corenet.com";
    }
}
