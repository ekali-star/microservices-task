package com.example.gymcrm;

import com.example.gymcrm.config.AppConfig;
import com.example.gymcrm.facade.GymFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GymCrmApplication {
    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        GymFacade facade = context.getBean(GymFacade.class);

        System.out.println("Gym CRM Application started successfully!");
    }
}