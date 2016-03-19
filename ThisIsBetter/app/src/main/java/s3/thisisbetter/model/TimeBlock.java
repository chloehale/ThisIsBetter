package s3.thisisbetter.model;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
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

    private ArrayList<String> times;
    private Map<String, Map<String, Boolean>> availability;
    private Map<Integer, String> availabilityIndex;

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

        times = new ArrayList<>();
        times.add("9:00am");
        times.add("10:00am");
        times.add("11:00am");
        times.add("12:00pm");
        times.add("1:00pm");
        times.add("2:00pm");
        times.add("3:00pm");
        times.add("4:00pm");
        times.add("5:00pm");
        times.add("6:00pm");
        times.add("7:00pm");

        availability = new HashMap<>();
        availabilityIndex = new HashMap<>();

        for (int i = 0; i < times.size(); i++) {
            availability.put(times.get(i), new HashMap<String, Boolean>());
            availabilityIndex.put(i, times.get(i));
        }
    }

    public void setAvailable(int index, String userID) {
        availability.get(availabilityIndex.get(index)).put(userID, true);
    }

    public void setUnavailable(int index, String userID) {
        availability.get(availabilityIndex.get(index)).put(userID, true);
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

    public Map<String, Map<String, Boolean>> getAvailability() {
        return availability;
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

