package com.restController;

import com.LongPolling.State.RequestPromise;
import com.entity.Employee;
import com.LongPolling.Overseer;
import com.exceptionHandlingStuff.EmployeeNotFoundException;
import com.services.EmployeeServiceInterface;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/employee")
public class EmployeeRestController {

    private final EmployeeServiceInterface employeeService;
    private final Overseer overseer;

    @Autowired
    public EmployeeRestController(EmployeeServiceInterface employeeService, Overseer overseer) {
        this.employeeService = employeeService;
        this.overseer = overseer;
    }

    // subscribe for data to be sent back when available
    @NotNull
    @GetMapping("/subscribe")
    public RequestPromise handleAsync(HttpSession session){
        return overseer.subscribe(
                Employee.class.getName(),
                session,
                employeeService);
    }

    @NotNull
    @GetMapping("/trigger/{employeeId}")
    public ResponseEntity<?> updateEmployee(@PathVariable int employeeId){
        Employee temp = employeeService.getEmployee(employeeId);
        employeeService.saveEmployee(temp);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //GET EMPLOYEE BY ID
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Employee> getEmployee(@PathVariable int employeeId){
        Employee returnedEmployee = employeeService.getEmployee(employeeId);
//        session.invalidate();
        if(returnedEmployee !=null){
            return ResponseEntity.ok().body(returnedEmployee);
        }
        else throw new EmployeeNotFoundException("Employee not found: " + employeeId);
    }


}
