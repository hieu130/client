package com.example.client.controller;

import com.example.client.model.Employees;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class EmployeesController {
    private final String REST_API_LIST = "http://localhost:8080/api/v1/employees";
    private final String REST_API_CREATE = "http://localhost:8080/api/v1/employees/saveemployees";
    private final String REST_API_UPDATE = "http://localhost:8080/api/v1/employees/updateemployees";

    @GetMapping(value = {"/" , "/listemployees"})
    public String index(Model model){
        javax.ws.rs.client.Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_LIST);
        List<Employees> ls =  target.request(MediaType.APPLICATION_JSON_TYPE).get(List.class);

        model.addAttribute("listEmployees" , ls);
        return "listEmployees";

    }

    private static javax.ws.rs.client.Client createJerseyRestClient() {
        ClientConfig clientConfig = new ClientConfig();

        // Config logging for client side
        clientConfig.register( //
                new LoggingFeature( //
                        Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME), //
                        Level.INFO, //
                        LoggingFeature.Verbosity.PAYLOAD_ANY, //
                        10000));

        return ClientBuilder.newClient(clientConfig);
    }
    @GetMapping(value = "createNewEmployees")
    public String createNewEmployees(){
        return "create";
    }

    @PostMapping("saveemployees")
    public String saveEmployees(@RequestParam String name,
                           @RequestParam String wage){
        Employees e = new Employees();
        e.setName(name);
        e.setWage(wage);

        String jsonEmployees = convertToJson(e);
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_CREATE);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(jsonEmployees , MediaType.APPLICATION_JSON));
        return "redirect:/";
    }
    private static String convertToJson(Employees employees) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(employees);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
