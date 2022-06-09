package com.encora.framework.controller;

public @interface Post {
    String value() default "/";

    String consumes();

    String produces();
}
