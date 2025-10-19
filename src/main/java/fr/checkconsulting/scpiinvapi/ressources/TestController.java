package fr.checkconsulting.scpiinvapi.ressources;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")

public class TestController {


    @GetMapping
    public ResponseEntity<? > getTest(){

            return new ResponseEntity<> ("Hello Word", HttpStatus.OK) ;
    }

}
