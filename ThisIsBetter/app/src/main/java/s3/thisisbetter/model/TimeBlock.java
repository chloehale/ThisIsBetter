package s3.thisisbetter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chloe on 3/17/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class TimeBlock implements Comparable<TimeBlock> {

    public static final String EVENT_ID_KEY = "eventID";

    private int day;
    private int month;
    private int year;
    private String ownerID;
    private String eventID;
    private Map<String, Map<String, Boolean>> availability;

    public TimeBlock() {
        availability = new HashMap<>();
    }

    public TimeBlock(CalendarDay data, String ownerID) {
        this();
        day = data.getDay();
        month = data.getMonth();
        year = data.getYear();
        this.ownerID = ownerID;
    }

    public TimeBlock(int day, int month, int year, String ownerID) {
        this();
        this.day = day;
        this.month = month;
        this.year = year;
        this.ownerID = ownerID;
    }


    /**
     * GETTERS & SETTERS
     */

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

    public String getEventID() {
        return eventID;
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

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }


    /**
     * GENERATOR METHODS
     */

    public ArrayList<String> generateTimesArray() {
        ArrayList<String> times = new ArrayList<>();
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
        times.add("8:00pm");
        return times;
    }

    public Map<Integer, String> generateAvailabilityIndex() {
        ArrayList<String> times = generateTimesArray();
        Map<Integer, String> availabilityIndex = new HashMap<>();

        for (int i = 0; i < times.size(); i++) {
            availabilityIndex.put(i, times.get(i));
        }

        return availabilityIndex;
    }


    /**
     * AVAILABILITY METHODS
     */

    public boolean isAvailableAtTime(int index) {
        String stringIndex = generateAvailabilityIndex().get(index);
        Map<String, Boolean> ownerToBool = availability.get(stringIndex);

        if (ownerToBool == null) { return false; }

        String uid = DB.getMyUID();
        Boolean available = ownerToBool.get(uid);
        if (available == null) {
            return false;
        }
        else {
            return available;
        }
    }

    public void setAvailableAtTime(int index) {
        String stringIndex = generateAvailabilityIndex().get(index);

        if (availability.get(stringIndex) == null) {
            availability.put(stringIndex, new HashMap<String, Boolean>());
        }

        String uid = DB.getMyUID();
        availability.get(stringIndex).put(uid, true);
    }

    public void setUnavailableAtTime(int index) {
        String stringIndex = generateAvailabilityIndex().get(index);
        if (availability.get(stringIndex) == null) { return; }
        String uid = DB.getMyUID();
        availability.get(stringIndex).remove(uid);
    }

    public int calculateNumberOfPeopleAvailableAtTime(int index) {
        String stringIndex = generateAvailabilityIndex().get(index);
        Map<String, Boolean> ownerToBool = availability.get(stringIndex);

        if (ownerToBool == null) {
            return 0;
        } else {
            return ownerToBool.size();
        }
    }

    public boolean isUserEverAvailable() {
        String uid = DB.getMyUID();

        for(Map<String, Boolean> ownerToBool : availability.values()) {
            for(Map.Entry<String, Boolean> entry : ownerToBool.entrySet()) {
                if (entry.getKey().equals(uid)) {
                    return true;
                }
            }
        }

        return false;
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

