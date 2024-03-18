package com.bigleague.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

        String sql ="select id,userid,password from person where employeeStatus=false";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {


            Map<String, Object> userDetailsMap = new HashMap<>();
            userDetailsMap.put("id",String.valueOf( row.get("id")));
            userDetailsMap.put("userid", (String) row.get("userid"));
            userDetailsMap.put("password", (String) row.get("password"));

//            userDetailsMap.put("sessionExpiry", (Date) row.get("sessionExpiry"));




            result.add(userDetailsMap);
        }

        System.out.println(result);

        return result;
    }


    @GetMapping("/getEmployeeDetails")
    public List<Map<String, Object>> getEmployeeDetails() {
        String sql ="select id,userid,password,payscale from person where employeeStatus=true";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            Map<String, Object> userDetailsMap = new HashMap<>();

            // Convert the id to a numeric type instead of treating it as a String
            userDetailsMap.put("id",String.valueOf( row.get("id")));
            userDetailsMap.put("userid", (String) row.get("userid"));
            userDetailsMap.put("password", (String) row.get("password"));
            userDetailsMap.put("payscale",String.valueOf( row.get("payscale")));

            // userDetailsMap.put("sessionExpiry", (Date) row.get("sessionExpiry"));

            result.add(userDetailsMap);
        }

        return result;
    }


    @GetMapping("/getEmployeeAttendanceDetails")
    public List<Map<String, Object>> getEmployeeAttendanceDetails() {
        String sql ="select * from person_attendance";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        return rows;
    }

    @PostMapping("/EmployeeDetail")
    public List<Map<String, Object>> getEmployeeDetail(@RequestBody Map<String, String> payload) {

        String userId = payload.get("userId");
        String sql = "SELECT s_no, user_id, clock_in_status,disable_clock_in, in_time, clock_out_status,disable_clock_out, out_time, hours_worked " + "FROM public.person_attendance " + "WHERE user_id = '" + userId + "' order by s_no ";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
//        List<Map<String, Object>> result = new ArrayList<>();
//
//        for (Map<String, Object> row : rows) {
//            Map<String, Object> userDetailsMap = new HashMap<>();
//
//            // Convert the id to a numeric type instead of treating it as a String
//            userDetailsMap.put("id",String.valueOf( row.get("id")));
//            userDetailsMap.put("userid", (String) row.get("userid"));
//            userDetailsMap.put("password", (String) row.get("password"));
//            // userDetailsMap.put("sessionExpiry", (Date) row.get("sessionExpiry"));
//
//            result.add(userDetailsMap);
//        }

        return rows;
    }

    @PostMapping("/updateEmployeeDetail")
    public List<Map<String, Object>> updateEmployeeDetail(@RequestBody Map<String, String> payload) {


        System.out.println(payload);
        Timestamp outTime=null;
        Timestamp inTime=null;

        // Extract values from the payload
        String userId = payload.get("user_id");
        String clockInStatus = payload.get("clock_in_status");
        boolean disableClockIn = Boolean.parseBoolean(payload.get("disable_clock_in"));
        String clockOutStatus = payload.get("clock_out_status");
        boolean disableClockOut = Boolean.parseBoolean(payload.get("disable_clock_out"));
        Double hoursWorked = (payload.get("hours_worked") != null) ? Double.parseDouble(payload.get("hours_worked")) : null;
        int sNo = Integer.parseInt(payload.get("s_no"));
        if (payload.containsKey("out_time") && payload.get("out_time") != null && !payload.get("out_time").isEmpty()) {
            try {
                // Try to parse the out_time value to Timestamp
                outTime = Timestamp.valueOf(payload.get("out_time"));
            } catch (IllegalArgumentException e) {
                // If parsing fails, set outTime to null
                outTime = null;
            }
        }
        if (payload.containsKey("in_time") && payload.get("in_time") != null && !payload.get("in_time").isEmpty()) {
            try {
                // Try to parse the in_time value to Timestamp
                inTime = Timestamp.valueOf(payload.get("in_time"));
            } catch (IllegalArgumentException e) {
                // If parsing fails, set inTime to null
                inTime = null;
            }
        }


        // Construct the SQL UPDATE statement
        String sql = "UPDATE public.person_attendance SET "
                + "user_id = ?, "
                + "clock_in_status = ?, "
                + "disable_clock_in = ?, "
                + "in_time = ?, "
                + "clock_out_status = ?, "
                + "disable_clock_out = ?, "
                + "out_time = ?, "
                + "hours_worked = ? "
                + "WHERE s_no = ?";

        // Execute the SQL UPDATE statement using JDBC template
        jdbcTemplate.update(sql, userId, clockInStatus, disableClockIn, inTime, clockOutStatus, disableClockOut, outTime, hoursWorked, sNo);

        // Retrieve and return updated rows (optional)
        List<Map<String, Object>> updatedRows = jdbcTemplate.queryForList("SELECT * FROM public.person_attendance WHERE s_no = ?", sNo);
        return updatedRows;
    }

    @PostMapping("/updatePersonPayRoll")
    public List<Map<String, Object>> updatePersonPayRoll(@RequestBody Map<String, String> payload) {


        System.out.println(payload);
        String payScalestr = payload.get("newpayscale");
        BigDecimal payscale = new BigDecimal(payScalestr);

        String empidString = payload.get("employeeId");
        BigDecimal id = new BigDecimal(empidString);



        // Construct the SQL UPDATE statement
        String sql = "update person set payscale=? where id=?";

        // Execute the SQL UPDATE statement using JDBC template
        jdbcTemplate.update(sql,payscale,id);

        // Retrieve and return updated rows (optional)
        List<Map<String, Object>> updatedRows = jdbcTemplate.queryForList("SELECT * FROM public.person WHERE id= ?", id);
        return updatedRows;
    }

    @PostMapping("/saveEmployeeDetail")
    public List<Map<String, Object>> saveEmployeeDetail(@RequestBody Map<String, String> payload) {

        System.out.println("payload in the save employee### " + payload);
        String employeeId = payload.get("employee_id");
        String payScalestr = payload.get("pay_scale");
        BigDecimal payScale = new BigDecimal(payScalestr);

        String userId = payload.get("user_id");
        String clockInStatus = payload.get("clock_in_status");
        boolean disableClockIn = Boolean.parseBoolean(payload.get("disable_clock_in"));
        String clockOutStatus = payload.get("clock_out_status");
        boolean disableClockOut = Boolean.parseBoolean(payload.get("disable_clock_out"));

        // Construct the SQL INSERT statement
        String sql = "INSERT INTO public.person_attendance "
                + "(employee_id,user_id, clock_in_status, disable_clock_in, clock_out_status, disable_clock_out,pay_scale) "
                + "VALUES (?, ?, ?, ?, ?, ?,?)";

        // Execute the SQL INSERT statement using JDBC template
        jdbcTemplate.update(sql,employeeId,userId, clockInStatus, disableClockIn, clockOutStatus, disableClockOut,payScale);

        // Return any desired response (e.g., success message or updated rows)
        return Collections.singletonList(Collections.singletonMap("message", "Data inserted successfully"));
    }

}