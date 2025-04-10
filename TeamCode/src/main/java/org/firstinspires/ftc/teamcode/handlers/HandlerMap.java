package org.firstinspires.ftc.teamcode.handlers;

import java.util.HashMap;
import java.util.Map;

public class HandlerMap {

    private static final Map<String, HardwareComponentHandler<?>> handlerMap = new HashMap<>();

    public static void put(String name, HardwareComponentHandler<?> handler) {
        handlerMap.put(name, handler);
    }
    @SuppressWarnings("unchecked")
    public static <T extends HardwareComponentHandler<?>> T get(String name) {
        return (T) handlerMap.get(name);
    }
}
