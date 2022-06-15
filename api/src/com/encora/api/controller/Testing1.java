package com.encora.api.controller;

import com.encora.framework.controller.RestController;

@RestController(url = "/testing1")
public class Testing1 {
    public void printText(){
        System.out.println("This is the testing one");
    }
    public void printText2(){
        System.out.println("This is the testing two");
    }
    public void printText3(){
        System.out.println("This is the testing three");
    }
    public void printText4(){
        System.out.println("Different text");
    }
}
