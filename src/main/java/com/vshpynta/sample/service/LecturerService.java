package com.vshpynta.sample.service;

import com.vshpynta.sample.model.Lecturer;
import com.vshpynta.sample.model.annotations.TestAnnotation;
import org.springframework.stereotype.Service;

/**
 * Created by vshpynta on 22.08.17.
 */
@Service
public class LecturerService {

    @TestAnnotation
    public Lecturer save(Lecturer lecturer) {
        System.out.println(("Saving lecturer: " + lecturer));
        return lecturer;
    }
}
