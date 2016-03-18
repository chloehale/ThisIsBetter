package s3.thisisbetter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chloe on 3/17/16.
 */
public class TimeBlock {

    private int day;
    private int month;
    private int year;

    private Map<String, Map<String, Boolean>> times;

    public TimeBlock() {}

    public TimeBlock(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;

        times = new HashMap<>();
        times.put("9am", null);
        times.put("10am", null);
        times.put("11am", null);
        times.put("12pm", null);
        times.put("1pm", null);
        times.put("2pm", null);
        times.put("3pm", null);
        times.put("4pm", null);
        times.put("5pm", null);
        times.put("6pm", null);
        times.put("7pm", null);
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public Map<String, Map<String, Boolean>> getTimes() {
        return times;
    }
}

