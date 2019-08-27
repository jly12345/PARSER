package com.symbio.epb.bigfile;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
    }

}
