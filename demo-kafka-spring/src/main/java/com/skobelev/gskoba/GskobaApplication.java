package com.skobelev.gskoba;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class GskobaApplication {
    public static void main(String[] args) {
        SpringApplication.run(GskobaApplication.class, args);
    }
}

@RestController
class MainController {

    @Autowired
    private KafkaTemplate<Object, Object> template;

    @GetMapping(path = "/send/{msg}")
    public void sendMsg(@PathVariable String msg) {
        this.template.send("first_topic", msg);
    }
}

@Configuration
class Config {
    @KafkaListener(id = "myGroup", topics = {"first_topic"})
    public void listener1(String msg) {
        System.out.println(msg);
    }
}



