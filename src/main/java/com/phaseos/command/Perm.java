package com.phaseos.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dennis Heckmann on 13.05.17
 * Copyright (c) 2017 Dennis Heckmann
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Perm {

    String value();

}
