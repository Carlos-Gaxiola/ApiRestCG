package com.encora.framework.controller;

public @interface Put {
    String value() default "/";

    String consumes();

    String produces();
}
