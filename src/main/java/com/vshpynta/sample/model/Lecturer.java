package com.vshpynta.sample.model;

import com.vshpynta.sample.model.annotations.TestAnnotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@TestAnnotation
public class Lecturer {

    private String firstName;
    private String lastName;
}
