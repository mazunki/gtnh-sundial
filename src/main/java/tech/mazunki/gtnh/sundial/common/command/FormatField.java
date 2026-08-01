package tech.mazunki.gtnh.sundial.common.command;

public enum FormatField {

    CODENAME("D", "codename", "Dimension codename (e.g. DIM-28)"),
    NAME("N", "name", "Display name (e.g. Moon)"),
    DAY("d", "day", "Days since this dimension's clock started"),
    HOUR24("H", "hour24", "Hour, 24-hour format (00-23)"),
    HOUR12("I", "hour12", "Hour, 12-hour format (01-12)"),
    MINUTE("M", "minute", "Minute (00-59)"),
    SECOND("S", "second", "Second (00-59)"),
    AMPM("p", "ampm", "AM or PM"),
    AMPM_LOWER("P", "ampm_lower", "am or pm"),
    TIME24("R", "time24", "Current time, 24-hour (i.e. HH:MM)"),
    TIME("T", "time", "Current time, 24-hour (i.e. HH:MM:SS)"),
    // closest equivalent to iso8601 in minecraft lol
    FULLDATE("F", "fulldate", "Day number and 24-hour time (e.g. Day 5 13:31)"),
    PHASE("K", "phase", "Day, Dusk, Night, or Dawn"),
    ETA("e", "eta", "Real time until the next phase transition"),
    RATIO("r", "ratio", "How many Overworld days this dimension's day takes"),
    CYCLE_LENGTH("c", "cyclelength", "Real time for one full day+night cycle"),
    DAY_LENGTH("y", "daylength", "Real time from sunrise until sleep becomes possible"),
    NIGHT_LENGTH("n", "nightlength", "Real time sleep remains possible, until the next sunrise"),
    SUNRISE("u", "sunrise", "Real time until the next sunrise"),
    SUNSET("x", "sunset", "Real time until sleep becomes possible");

    public final String shortCode;
    public final String longName;
    public final String description;

    FormatField(String shortCode, String longName, String description) {
        this.shortCode = shortCode;
        this.longName = longName;
        this.description = description;
    }

    public static FormatField byShortCode(String code) {
        for (FormatField field : values()) {
            if (field.shortCode.equals(code)) {
                return field;
            }
        }
        return null;
    }

    public static FormatField byLongName(String name) {
        for (FormatField field : values()) {
            if (field.longName.equals(name)) {
                return field;
            }
        }
        return null;
    }
}
