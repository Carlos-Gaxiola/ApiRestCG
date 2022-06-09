package com.encora.framework.json;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class JSONSerializer {
    public static <T> String serilaize(T source) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> objectClass = source.getClass();

        Field[] fields = objectClass.getDeclaredFields();
        List<String> values = new ArrayList<>(fields.length);

        for (int i = 0; i < fields.length; i++) {
            final Field f = fields[i];

            if (f.isAnnotationPresent(SkipSerialization.class)) {
                continue;
            }

            Method fieldGetter = objectClass.getMethod(getGetterMethodName(f));
            Object result = fieldGetter.invoke(source);
            String resultAsString = result != null ? result.toString() : "null";
            values.add("\n " + '"' + f.getName() + '"' + ":" + '"' + resultAsString + '"');
        }

        String jsonValue = Arrays.toString(values.toArray());
        return "{" + jsonValue.substring(1, jsonValue.length() - 1) + "\n } \n";
    }

    public static <T> T deserialize(Class<T> target, String json) {
        Constructor[] constructors = target.getConstructors();
        Constructor constructorOne = constructors[1];

        T object = null;

        try {
            object = (T) constructorOne.newInstance();
            String[] values = json.substring(1).split(",\\s");


            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].trim().replaceAll("}", "");
                String fieldName = values[i].substring(1, values[i].indexOf(":") - 1);
                Object fieldValue;
                if(values[i].substring(values[i].indexOf(":"), values[i].length()-1).contains("\"")){
                    fieldValue = values[i].substring(values[i].indexOf(":") + 3, values[i].lastIndexOf('"'));
                }else{
                    fieldValue = values[i].substring(values[i].indexOf(":") + 2, (i != values.length-1)?values[i].length():values[i].length()-1);
                }

                final Field f = target.getDeclaredField(fieldName);

                if (f.isAnnotationPresent(SkipSerialization.class)) {
                    continue;
                }

                Method fieldSetter = target.getMethod(getSetterMethodName(f), f.getType());

                if (f.getType().getSimpleName().toLowerCase().contains("boolean")) {
                    fieldValue = Boolean.parseBoolean(String.valueOf(fieldValue));
                } else if (f.getType().getSimpleName().toLowerCase().contains("int")) {
                    fieldValue = Integer.parseInt(String.valueOf(fieldValue));
                }

                fieldSetter.invoke(object, fieldValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    private static String getGetterMethodName(Field field) {
        String fieldName = field.getName();
        String camelFieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        String prefix = "get";
        if (field.getType().getSimpleName().toLowerCase().contains("boolean")) {
            prefix = "is";
        }
        return prefix + camelFieldName;
    }

    private static String getSetterMethodName(Field field) {
        String fieldName = field.getName();
        String camelFieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        return "set" + camelFieldName;
    }
}
