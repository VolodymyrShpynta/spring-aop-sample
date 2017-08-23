package com.vshpynta.sample.validation;

import com.vshpynta.sample.model.Lecturer;
import org.springframework.stereotype.Component;

/**
 * Created by vshpynta on 23.08.17.
 */
@Component
public class BaseEntityValidator implements EntityValidator<Lecturer> {
    @Override
    public void validate(Lecturer lecturer) {
        System.out.println("Validating lecturer: " + lecturer);
    }
}
