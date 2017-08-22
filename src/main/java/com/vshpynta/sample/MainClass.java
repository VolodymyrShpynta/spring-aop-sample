package com.vshpynta.sample;

import com.vshpynta.sample.config.JavaConfig;
import com.vshpynta.sample.model.Lecturer;
import com.vshpynta.sample.service.LecturerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by VShpynta on 10/1/14.
 */
public class MainClass {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(JavaConfig.class);
        LecturerService lecturerService = applicationContext.getBean(LecturerService.class);
        lecturerService.save(Lecturer.builder()
                .firstName("Bob")
                .lastName("Cruse")
                .build());
    }
}
