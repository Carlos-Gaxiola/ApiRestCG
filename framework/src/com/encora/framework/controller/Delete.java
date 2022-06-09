package com.encora.framework.controller;

public @interface Delete {
    String value() default "/";

    String consumes();

    String produces();
}
