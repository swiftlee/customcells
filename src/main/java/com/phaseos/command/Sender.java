package com.phaseos.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Dennis Heckmann on 14.05.17
 * Copyright (c) 2017 Dennis Heckmann
 */
@Retention(value = RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.TYPE)
public @interface Sender {

    SenderType sender() default SenderType.ANY;

}
