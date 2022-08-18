package com.example.client.controller;

import ch.qos.logback.core.net.server.Client;
import com.example.client.model.Employees;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeesController {
    private final String REST_API_LIST = "http://localhost:8080/api/v1/employees";
    private final String REST_API_CREATE = "http://localhost:8080/api/v1/employees/saveemployees";
    private final String REST_API_DELETE = "http://localhost:8080/api/v1/employees/updateemployees";

    @GetMapping(value = {"/" , "/list"})
    public String index(Model model){
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_LIST);
        List<Employees> ls =  target.request(MediaType.APPLICATION_JSON_TYPE).get(List.class);

        model.addAttribute("list" , ls);
        return "list";

    }

    @GetMapping("delete")
    public String deleteUser( Integer id){
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_DELETE + id);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).delete();
        return "redirect:/";
    }
    private static Client createJerseyRestClient() {
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
    @GetMapping(value = "create")
    public String createNewUsers(){
        return "create";
    }

    @PostMapping("save")
    public String saveUser(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam int phone){
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPhone(phone);

        String jsonUser = convertToJson(u);
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_CREATE);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(jsonUser , MediaType.APPLICATION_JSON));
        return "redirect:/";
    }
    private static String convertToJson(User user) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
