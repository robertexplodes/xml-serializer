package services;

import lombok.SneakyThrows;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collector;

public class XmlSerializer {

    private final List<Class<?>> WRAPPER_CLASSES = List.of(Integer.class, Boolean.class, Long.class, Float.class, Character.class);

    @SneakyThrows
    public String serialize(Object object) {
        var builder = new StringBuilder();

        builder.append("<").append(object.getClass().getSimpleName()).append(">\n");

        var methods = sortedValidMethods(object);

        for (var method : methods) {
            var startsWithIs = method.getName().startsWith("is");
            var fieldName = method.getName().substring(startsWithIs ? 2 : 3);
            fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1); // first letter to lower case

            var value = method.invoke(object);
            builder.append(serializeValue(fieldName, value, method.getReturnType()));
        }

        builder.append("</").append(object.getClass().getSimpleName()).append(">");

        return builder.toString();
    }

    private String serializeValue(String fieldName, Object value, Class<?> returnType) {
        if (value == null)
            return "";
        var builder = new StringBuilder();
        builder.append("    <").append(fieldName).append(">\n");
        builder.append("        ");
        if (value instanceof String str) {
            builder.append(encodeString(str)).append("\n");
        } else if (value instanceof Object[] arr) {
            builder.append(serializeArray(arr));
        } else if (value instanceof Collection<?> col) {
            builder.append(serializeArray(col.toArray()));
        } else if (value instanceof Map<?,?> map) {
            builder.append(serializeMap(map));
        } else if (!returnType.isPrimitive() && !isWrapper(value.getClass())) {
            builder.append(serialize(value));
        } else {
            builder.append(value).append("\n");
        }
        builder.append("    </").append(fieldName).append(">\n");

        return builder.toString();
    }

    private String serializeMap(Map<?, ?> map) {
        var builder = new StringBuilder();

        for (var entry : map.entrySet()) {
            builder.append("            <key>").append(entry.getKey()).append("</key>\n");
            builder.append("            <value>").append(serialize(entry.getValue())).append("</value>\n");
        }

        return builder.toString();
    }

    private String serializeArray(Object[] value) {
        var builder = new StringBuilder();
        for (int i = 0; i < Array.getLength(value); i++) {
            builder.append("    <value>\n");
            builder.append("        ").append(Array.get(value, i)).append("\n");
            builder.append("    </value>\n");
        }
        return builder.toString();
    }

    private boolean isWrapper(Class<?> type) {
        return WRAPPER_CLASSES.contains(type);
    }

    private List<Method> getValidMethods(Object object) {
        var methods = object.getClass().getDeclaredMethods();
        var fields = Arrays.stream(object.getClass().getDeclaredFields()).map(Field::getName).toArray(String[]::new);

        var validMethods = new ArrayList<Method>();

        for (var method : methods) {
            if ((method.getName().startsWith("get") || method.getName().startsWith("is")) && method.getParameterCount() == 0) {
                var startsWithIs = method.getName().startsWith("is");
                var fieldName = method.getName().substring(startsWithIs ? 2 : 3);
                fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1); // first letter to lower case

                if (Arrays.asList(fields).contains(fieldName)) {
                    validMethods.add(method);
                }
            }
        }
        return validMethods;
    }

    private List<Method> sortedValidMethods(Object object) {
        var methods = getValidMethods(object);
        var fields = Arrays.stream(object.getClass().getDeclaredFields()).map(Field::getName).toArray(String[]::new);

        var newOrder = new ArrayList<Method>();
        for (var field : fields) {
            for (var method : methods) {
                var startsWithIs = method.getName().startsWith("is");
                var fieldName = method.getName().substring(startsWithIs ? 2 : 3);
                fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1); // first letter to lower case

                if (fieldName.equals(field)) {
                    newOrder.add(method);
                }
            }
        }
        return newOrder;
    }

    private String encodeString(String str) {
        return str.chars().mapToObj(c -> switch (c) {
            case '<' -> "&lt";
            case '>' -> "&gt";
            case '&' -> "&amp";
            case '"' -> "&quot";
            case '\'' -> "&apos";
            default -> String.valueOf((char) c);
        }).collect(Collector.of(
                StringBuilder::new,
                StringBuilder::append,
                StringBuilder::append,
                StringBuilder::toString));
    }
}
