package com.manara.crm;

//import com.manara.crm.service.User;
import com.manara.crm.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmApplication.class, args);
    }
/*@Bean
public CommandLineRunner commandLineRunner(
            UserService service
){
    return args -> {
        service.register(User.builder()
                .username("Ali")
                .email("ali@mail.com")
                .password("aaa")
                .build());

        service.register(User.builder()
                .username("John")
                .email("john@mail.com")
                .password("aaa")
                .build());

        service.register(User.builder()
                .username("Anny")
                .email("anna@mail.com")
                .password("aaa")
                .build());
    };
}*/
}
