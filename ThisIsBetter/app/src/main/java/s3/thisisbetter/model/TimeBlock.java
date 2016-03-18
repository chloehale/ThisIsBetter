package s3.thisisbetter.model;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chloe on 3/17/16.
 */
public class TimeBlock implements Comparable<TimeBlock> {

    private int day;
    private int month;
    private int year;
    private String ownerID;

    private Map<String, Map<String, Boolean>> times;

    public TimeBlock() {}

    public TimeBlock(CalendarDay data, String ownerID) {
        day = data.getDay();
        month = data.getMonth();
        year = data.getYear();
        this.ownerID = ownerID;
    }

    public TimeBlock(int day, int month, int year, String ownerID) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.ownerID = ownerID;

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

    public String getOwnerID() {
        return ownerID;
    }

    public Map<String, Map<String, Boolean>> getTimes() {
        return times;
    }

    public String getShortDescription() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(year, month, day);

        int dayIndex = c.get(Calendar.DAY_OF_WEEK);
        String[] names = {"", "Sun", "Mon", "Tue", "Wed", "Thurs", "Fri", "Sat"};
        return names[dayIndex] + " " + day;
    }

    public String getMonthString() {
        String[] names = {"January", "February", "March", "April", "May", "June", "July", "August",
        "September", "October", "November", "December"};
        return names[month];
    }

    /**
     * COMPARABLE METHODS
     */

    @Override
    public int compareTo(TimeBlock another) {
        Calendar c = GregorianCalendar.getInstance();
        c.set(year, month, day);

        Calendar other = GregorianCalendar.getInstance();
        other.set(another.getYear(), another.getMonth(), another.getDay());

        return c.compareTo(other);
    }
}

