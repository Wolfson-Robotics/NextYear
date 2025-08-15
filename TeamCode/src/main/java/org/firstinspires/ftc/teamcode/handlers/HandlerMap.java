package org.firstinspires.ftc.teamcode.handlers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandlerMap {

    private static final Map<String, HardwareComponentHandler<?>> handlerMap = new HashMap<>();

    public static void put(String name, HardwareComponentHandler<?> handler) {
        handlerMap.put(name, handler);
    }
    public static void put(HardwareComponentHandler<?> handler) {
        put(handler.toString(), handler);
    }
    @SuppressWarnings("unchecked")
    public static <T extends HardwareComponentHandler<?>> T get(String name) {
        return (T) handlerMap.get(name);
    }

    public static Collection<HardwareComponentHandler<?>> all() {
        return handlerMap.values();
    }
    public static List<DcMotorExHandler> allMotors() {
        return all().stream().filter(h -> h instanceof DcMotorExHandler).map(DcMotorExHandler.class::cast).collect(Collectors.toList());
    }
    public static List<ServoHandler> allServos() {
        return all().stream().filter(h -> h instanceof ServoHandler).map(ServoHandler.class::cast).collect(Collectors.toList());
    }
    public static List<HardwareComponentHandler<?>> allMisc() {
        return all().stream().filter(h -> !(h instanceof DcMotorExHandler) && !(h instanceof ServoHandler)).collect(Collectors.toList());
    }

}
