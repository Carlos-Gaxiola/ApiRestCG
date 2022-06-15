package com.encora.framework.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Put {
    String value() default "/";

    String consumes() default "";

    String produces() default "";
}
