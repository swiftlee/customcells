package com.phaseos.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dennis Heckmann on 12.11.17
 * Copyright (c) 2017 Dennis Heckmann
 * GitHub: https://github.com/Mylogo
 * Web: http://mylogo.me
 * Mail: contact@mylogo.me
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Inject {
    String value() default "";
}
