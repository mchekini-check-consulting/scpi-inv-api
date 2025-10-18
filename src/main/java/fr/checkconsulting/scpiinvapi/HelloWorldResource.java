package fr.checkconsulting.scpiinvapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/hello")
public class HelloWorldResource {


    @GetMapping
    public String sayHello() {
        return "Hello World v 1.0!";
    }
}
