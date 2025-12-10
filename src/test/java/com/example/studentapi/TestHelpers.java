package com.example.studentapi;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class TestHelpers {

    public static void setId(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
