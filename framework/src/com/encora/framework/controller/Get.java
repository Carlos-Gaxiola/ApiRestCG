package com.encora.framework.controller;

public @interface Get {
    String value() default "/";

    String consumes();

    String produces();
}
