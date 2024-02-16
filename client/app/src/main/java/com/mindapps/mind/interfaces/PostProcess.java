package com.mindapps.mind.interfaces;

public interface PostProcess {
    void Success(Object... o);
    void Failed(Exception e);
}
