package com.vshpynta.sample.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by VShpynta on 10/1/14.
 */
@Configuration
@ComponentScan(basePackages = {"com.vshpynta.sample"})
@EnableAspectJAutoProxy
public class JavaConfig {

}
