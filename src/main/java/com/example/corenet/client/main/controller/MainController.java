package com.example.corenet.client.main.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.corenet.client.main.service.MainService;

@Controller
public class MainController {

    @Autowired
    private MainService mainService;

    // 메인 전자결재 집계
    @GetMapping("/count-doc-status")
    @ResponseBody
    public Map<String, Integer> countDocsStatus(@RequestParam("jobcode") String jobcode) {

        System.out.println("JobCode: " + jobcode);

        Map<String, Integer> result = new HashMap<>();

        if("10002".equals(jobcode) || "10002".equals(jobcode)){
            result.put("systemAdmin", 1);
            System.out.println("result > " + result);
            return result;
        }
        boolean isCheckApprover = mainService.isAdminUser(jobcode);
   
        
        if (isCheckApprover) {
            result = mainService.countDocsStatusForAdmin(jobcode);
        } else {
            result = mainService.countDocsStatusForUser(jobcode);
        }
        System.out.println("result: " + result);

        return result;
    }

}
