package com.example.mind.interfaces;

public interface PostProcess {
    void Success(Object... o);
    void Failed(Exception e);
}
