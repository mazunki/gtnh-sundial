package tech.mazunki.gtnh.sundial.common.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tech.mazunki.gtnh.sundial.common.dimension.DimensionReading;

// renders a DimensionReading through a date(1)-style format string,
// %x short codes or {name} long names
public final class ClockFormatter {

    private ClockFormatter() {}

    // unknown {name}/%x throws IllegalArgumentException
    // '%%' escapes a literal '%'
    // unclosed '{' passes through literally
    public static String render(String format, DimensionReading r) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < format.length(); i++) {
            char c = format.charAt(i);

            if (c == '{') {
                int end = format.indexOf('}', i + 1);
                if (end == -1) {
                    out.append(c);
                    continue;
                }
                String name = format.substring(i + 1, end);
                FormatField field = FormatField.byLongName(name);
                if (field == null) {
                    throw new IllegalArgumentException("No such specifier: {" + name + "}");
                }
                out.append(value(field, r));
                i = end;
                continue;
            }

            if (c == '%') {
                if (i + 1 >= format.length()) {
                    out.append(c);
                    continue;
                }
                char directive = format.charAt(i + 1);
                if (directive == '%') {
                    out.append('%');
                    i++;
                    continue;
                }
                FormatField field = FormatField.byShortCode(String.valueOf(directive));
                if (field == null) {
                    throw new IllegalArgumentException("No such specifier: %" + directive);
                }
                out.append(value(field, r));
                i++;
                continue;
            }

            out.append(c);
        }
        return out.toString();
    }

    public static String value(FormatField field, DimensionReading r) {
        switch (field) {
            case CODENAME:
                return r.codename;
            case NAME:
                return r.displayName;
            case DAY:
                return String.valueOf(r.dayNumber);
            case HOUR24:
                return String.format(Locale.ROOT, "%02d", r.hour24);
            case HOUR12:
                return String.format(Locale.ROOT, "%02d", r.hour12);
            case MINUTE:
                return String.format(Locale.ROOT, "%02d", r.minute);
            case SECOND:
                return String.format(Locale.ROOT, "%02d", r.second);
            case AMPM:
                return (r.ampmUpper != null) ? r.ampmUpper : "";
            case AMPM_LOWER:
                return (r.ampmUpper != null) ? r.ampmUpper.toLowerCase(Locale.ROOT) : "";
            case TIME24:
                return String.format(Locale.ROOT, "%02d:%02d", r.hour24, r.minute);
            case TIME:
                return String.format(Locale.ROOT, "%02d:%02d:%02d", r.hour24, r.minute, r.second);
            case FULLDATE:
                return String.format(Locale.ROOT, "Day %d %02d:%02d", r.dayNumber, r.hour24, r.minute);
            case PHASE:
                return (r.phase != null) ? capitalize(r.phase.label) : "none";
            case ETA:
                return formatDuration(r.secondsUntilNextPhase);
            case RATIO:
                return formatRatio(r.dayLengthRatio) + "x";
            case CYCLE_LENGTH:
                return formatDuration(r.cycleLengthSeconds);
            case DAY_LENGTH:
                return formatDuration(r.dayLengthSeconds);
            case NIGHT_LENGTH:
                return formatDuration(r.nightLengthSeconds);
            case SUNRISE:
                return formatDuration(r.sunriseSeconds);
            case SUNSET:
                return formatDuration(r.sunsetSeconds);
            default:
                return "";
        }
    }

    // whole numbers print bare ("8x"); everything else keeps two decimals ("1.03x")
    public static String formatRatio(double ratio) {
        if (ratio == Math.rint(ratio)) {
            return String.format(Locale.ROOT, "%.0f", ratio);
        }
        return String.format(Locale.ROOT, "%.2f", ratio);
    }

    // "2h 40min", "56min", "45s"
    public static String formatDuration(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        boolean showSeconds = seconds != 0 || (hours == 0 && minutes == 0);
        boolean showMinutes = minutes != 0 || (hours != 0 && showSeconds);
        boolean showHours = hours != 0;

        List<String> parts = new ArrayList<>();
        if (showHours) {
            parts.add(hours + "h");
        }
        if (showMinutes) {
            String value = showHours ? String.format(Locale.ROOT, "%02d", minutes) : String.valueOf(minutes);
            parts.add(value + "min");
        }
        if (showSeconds) {
            boolean padded = showHours || showMinutes;
            String value = padded ? String.format(Locale.ROOT, "%02d", seconds) : String.valueOf(seconds);
            parts.add(value + "s");
        }
        return String.join(" ", parts);
    }

    public static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
