package com.bigleague.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")

public class apiFile {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/check")
    public String getString() {
        String responseString = "running properly";
        return responseString;
    }


    @GetMapping("/getAdminDetails")
    public  List<Map<String, Object>> getAdminDetails() {

        String sql ="select id,name,password from person where employeeStatus=false";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        for (Map<String, Object> row : rows) {


            Map<String, Object> userDetailsMap = new HashMap<>();
            userDetailsMap.put("id",String.valueOf( row.get("id")));
            userDetailsMap.put("name", (String) row.get("name"));
            userDetailsMap.put("password", (String) row.get("password"));
//            userDetailsMap.put("sessionExpiry", (Date) row.get("sessionExpiry"));




            result.add(userDetailsMap);
        }

        System.out.println(result);

        return result;
    }


    @GetMapping("/getEmployeeDetails")
    public  List<Map<String, Object>> getEmployeeDetails() {

        String sql ="select id,name,password from person where employeeStatus=false";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        for (Map<String, Object> row : rows) {


            Map<String, Object> userDetailsMap = new HashMap<>();
            userDetailsMap.put("id",String.valueOf( row.get("id")));
            userDetailsMap.put("name", (String) row.get("name"));
            userDetailsMap.put("password", (String) row.get("password"));
//          userDetailsMap.put("sessionExpiry", (Date) row.get("sessionExpiry"));

            result.add(userDetailsMap);
        }

        System.out.println(result);

        return result;
    }


}