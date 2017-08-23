package com.vshpynta.sample.service;

import com.vshpynta.sample.model.Lecturer;
import com.vshpynta.sample.model.annotations.TestAnnotation;
import com.vshpynta.sample.model.annotations.Validate;
import com.vshpynta.sample.validation.BaseEntityValidator;
import org.springframework.stereotype.Service;

/**
 * Created by vshpynta on 22.08.17.
 */
@Service
public class LecturerService {

    @TestAnnotation
    public Lecturer save(@Validate(BaseEntityValidator.class) Lecturer lecturer) {
        System.out.println(("Saving lecturer: " + lecturer));
        return lecturer;
    }
}
