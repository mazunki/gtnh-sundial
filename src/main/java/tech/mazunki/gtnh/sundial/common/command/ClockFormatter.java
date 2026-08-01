package tech.mazunki.gtnh.sundial.common.command;

public final class ClockFormatter {

    private ClockFormatter() {}

    public static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
