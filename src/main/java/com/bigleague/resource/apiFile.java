package com.bigleague.resource;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.Map;


import java.security.SecureRandom;
import java.math.BigInteger;


@RestController
@CrossOrigin(origins = "http://localhost:3000",allowCredentials = "true")

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

    @GetMapping("/standardPayRolls")
    public  List<Map<String, Object>> getStandardPayRolls() {

        String sql ="select id,role_name,standard_pay_scale from standard_payrolls";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {


            Map<String, Object> userDetailsMap = new HashMap<>();
            userDetailsMap.put("id",String.valueOf( row.get("id")));
            userDetailsMap.put("role_name", (String) row.get("role_name"));
            userDetailsMap.put("standard_pay_scale", String.valueOf( row.get("standard_pay_scale")));

//            userDetailsMap.put("sessionExpiry", (Date) row.get("sessionExpiry"));




            result.add(userDetailsMap);
        }

        System.out.println(result);

        return result;
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login( @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        Map<String, String> responseBody = new HashMap<>();
        SecureRandom random = new SecureRandom();

        Map<String,Object> finding= detailsExistInDb(loginRequest);
        if ((Boolean)finding.get("exists")) {

            String sessionId = new BigInteger(130, random).toString(32);


            Cookie sessionCookie = new Cookie("sessionId", sessionId);
            if(loginRequest.getWho().equalsIgnoreCase("Employee"))
            {

                sessionCookie.setPath("/");
                responseBody.put("employeeId",(String)finding.get("employeeId"));
                responseBody.put("payscale",(String)finding.get("payscale"));
                responseBody.put("currentRole",(String)finding.get("currentRole"));


            }
            else
                sessionCookie.setPath("/");


            response.addCookie(sessionCookie);
            responseBody.put("status", "SUCCESS");
            responseBody.put("sessionId", sessionId);
            return ResponseEntity.ok(responseBody);
        } else {
            responseBody.put("status", "FAILURE");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }
    }


    public Map<String,Object> detailsExistInDb(LoginRequest loginRequest)
    {
        Map<String,Object> result=new HashMap<>();
        if(loginRequest.getWho().equalsIgnoreCase("Employee"))
        {
            List<Map<String, Object>> empDetails=  getEmployeeDetails();

            for(Map<String,Object> i:empDetails)

            {
//            System.out.println(i.get("password")+" "+loginRequest.getPassword());
//                System.out.println(i.get("userid")+" "+loginRequest.getUserid());


                if(i.get("password").equals(loginRequest.getPassword()) && i.get("userid").equals(loginRequest.getUserid())
               ){
                    result.put("exists",true);
                    result.put("payscale",i.get("payscale"));
                    result.put("employeeId",i.get("id"));
                    result.put("currentRole",i.get("currentrole"));

                return result;
                }
            }
        }
        else if(loginRequest.getWho().equalsIgnoreCase("admin"))
        {
            List<Map<String, Object>> empDetails=  getAdminDetails();

            for(Map<String,Object> i:empDetails)
            {
                if(i.get("password").equals(loginRequest.getPassword()) && i.get("userid").equals(loginRequest.getUserid())
                ){

                    result.put("exists",true);

                                return result;
                }
            }
        }
         result.put("exists",false);
        return result;

    }


    @GetMapping("/getEmployeeDetails")
    public List<Map<String, Object>> getEmployeeDetails() {
        String sql ="select id,userid,password,payscale,currentrole from person where employeeStatus=true";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            Map<String, Object> userDetailsMap = new HashMap<>();

            // Convert the id to a numeric type instead of treating it as a String
            userDetailsMap.put("id",String.valueOf( row.get("id")));
            userDetailsMap.put("userid", (String) row.get("userid"));
            userDetailsMap.put("password", (String) row.get("password"));
            userDetailsMap.put("payscale",String.valueOf( row.get("payscale")));
            userDetailsMap.put("currentrole",String.valueOf( row.get("currentrole")));


            // userDetailsMap.put("sessionExpiry", (Date) row.get("sessionExpiry"));

            result.add(userDetailsMap);
        }

        return result;
    }

<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
=======
>>>>>>> Stashed changes

    @GetMapping("/getEmployeeAttendanceDetails")
    public List<Map<String, Object>> getEmployeeAttendanceDetails() {
        String sql ="select * from person_attendance where hours_worked is not null or hours_worked!=0";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        return rows;
    }

    @PostMapping("/EmployeeDetail")
    public List<Map<String, Object>> getEmployeeDetail(@CookieValue(name = "sessionId", required = false) String sessionId,@RequestBody Map<String, String> payload) {

        System.out.println("Got the session_Id"+sessionId);

        if(sessionId!=null)
        {
            System.out.println("Got the session_Id"+sessionId);
        }
        String userId = payload.get("userId");

        String sql = "SELECT s_no, user_id, clock_in_status,disable_clock_in, to_char(in_time, 'YYYY-MM-DD HH24:MI:SS') AS in_time, clock_out_status,disable_clock_out, to_char(out_time, 'YYYY-MM-DD HH24:MI:SS') AS out_time, hours_worked, pay_scale FROM public.person_attendance  WHERE user_id = '" + userId + "' order by s_no ";
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
>>>>>>> Stashed changes

    @PostMapping("/getStandardPayrolls")
    public List<Map<String, Object>> getStandardPayrolls(@CookieValue(name = "sessionId", required = false) String sessionId) {


       System.out.println("session Id"+sessionId);
        String sql = "select * from standard_payrolls";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);


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
        String currentRole = payload.get("current_role_name");


        String payScalestr = payload.get("pay_scale");
        BigDecimal payScale = new BigDecimal(payScalestr);

        String userId = payload.get("user_id");
        String clockInStatus = payload.get("clock_in_status");
        boolean disableClockIn = Boolean.parseBoolean(payload.get("disable_clock_in"));
        String clockOutStatus = payload.get("clock_out_status");
        boolean disableClockOut = Boolean.parseBoolean(payload.get("disable_clock_out"));

        String sql ="INSERT INTO public.person_attendance (employee_id, user_id, clock_in_status, disable_clock_in, clock_out_status, disable_clock_out, pay_scale, current_role_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


        // Execute the SQL INSERT statement using JDBC template
        jdbcTemplate.update(sql,employeeId,userId, clockInStatus, disableClockIn, clockOutStatus, disableClockOut,payScale,currentRole);

        // Return any desired response (e.g., success message or updated rows)
        return Collections.singletonList(Collections.singletonMap("message", "Data inserted successfully"));
    }



    @PostMapping("/submit/leaves")
    public Map<String, Object> createLeave(@RequestBody Map<String, Object> leave) {
        String employeeIdString = (String) leave.get("employeeId");
        int employeeId=Integer.parseInt(employeeIdString);
        LocalDate startDate = LocalDate.parse((CharSequence) leave.get("startDate"));
        LocalDate endDate = LocalDate.parse((CharSequence) leave.get("endDate"));


        String startDateString = (String) leave.get("startDate");
        String endDateString =(String) leave.get("endDate");

        String reason = (String) leave.get("reason");
        String status = (String) leave.get("status");

        // Check for date clashes
        if (isDateClash(employeeId, startDate, endDate)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Leave dates clash with existing leaves.");
            return response;
        }

        String sql = "INSERT INTO leaves_table (employee_id, start_date, end_date, reason, status) VALUES (?, ?, ?, ?, ?) RETURNING id";
        int id = jdbcTemplate.queryForObject(sql, Integer.class, employeeId, startDate, endDate, reason, status);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("id", id);
        response.put("message", "Leave successfully created.");
        return response;
    }

    private boolean isDateClash(int employeeId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COUNT(*) FROM leaves_table WHERE employee_id = ? AND ((start_date <= ? AND end_date >= ?) OR (start_date <= ? AND end_date >= ?) OR (start_date >= ? AND end_date <= ?))";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, employeeId, startDate, startDate, endDate, endDate, startDate, endDate);
        return count > 0;
    }


    @GetMapping("/leaves/history")
    public List<Map<String, Object>> getLeaveHistory(@RequestParam int employeeId) {
        String sql = "SELECT * FROM leaves_table where employee_id=?";
        return jdbcTemplate.queryForList(sql,employeeId);
    }

    @PostMapping("/leaves/approve")
    public Map<String, Object> approveLeave(@RequestParam int id, @RequestParam boolean approve) {
        String status = approve ? "Approved" : "Rejected";
        String sql = "UPDATE leaves_table SET status = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, status, id);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("status", status);
        response.put("rowsAffected", rowsAffected);
        return response;
    }


}

class LoginRequest{

    public String getUserid() {
        return userId;
    }

    public void setUser_id(String user_id) {
        this.userId = user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String userId;
    public String password;

    public String who;

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }
}