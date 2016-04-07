package s3.thisisbetter.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by steve on 3/25/16.
 */
public class AvailabilityBlock {

    private int availableCount;
    private int totalInvitedCount;
    private Calendar calendar;
    private String weekday;
    private int day;
    private String month;
    private String timeRange;
    private Integer hour;

    private Set<String> availableUserIds;
    private Set<String> notAvailableUserIds;
    private Set<String> notRespondedUserIds;

    public AvailabilityBlock(int totalInvitedCount, Calendar calendar, String month, String time, Set<String> availableUserIds,
                             Set<String> respondedUserIds, Set<String> notRespondedUserIds) {

        this.availableCount = availableUserIds.size();
        this.totalInvitedCount = totalInvitedCount;
        this.calendar = calendar;
        this.weekday = this.convertToWeekdayString(calendar);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.month = month;
        this.timeRange = this.convertToTimeRange(time);
        this.hour = this.parseHour(time);

        this.availableUserIds = availableUserIds;

        //of the responded user ids, remove those that are available,
        //which leaves you with the not available user ids who have responded
        this.notAvailableUserIds = new TreeSet<>(respondedUserIds);
        this.notAvailableUserIds.removeAll(availableUserIds);

        this.notRespondedUserIds = notRespondedUserIds;

    }

    public Calendar getCalendar() { return calendar; }

    public String getWeekday() {
        return weekday;
    }

    public String getMonthDay() {
        return month + " " + day;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public Integer getHour() { return hour; }

    public int getAvailableCount() {
        return availableCount;
    }

    public int getTotalInvitedCount() {
        return totalInvitedCount;
    }

    public Set<String> getAvailableUserIds() {
        return availableUserIds;
    }

    public Set<String> getNotAvailableUserIds() {
        return notAvailableUserIds;
    }

    public Set<String> getNotRespondedUserIds() {
        return notRespondedUserIds;
    }

    private String convertToWeekdayString(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
            default:
                return "Error";
        }
    }

    private int parseHour(String time) {
        String hourString = new String();
        for (int i = 0; i < time.length(); i++) {
            if (time.charAt(i) == ':')
                break;
            hourString += time.charAt(i);
        }

        int hour = Integer.parseInt(hourString);

        String period = this.parsePeriod(time);
        if (period.equals("pm") && hour != 12) {
            hour += 12;
        }
        else if (period.equals("am") && hour == 12) {
            hour = 0;
        }

        return hour;
    }

    private String parsePeriod(String time) {
        String period = new String();
        period += time.charAt(time.length()-2);
        period += time.charAt(time.length()-1);
        return period;
    }

    private String convertToTimeRange(String time) {
        switch(time) {
            case "12:00am":
                return "12:00am-1:00am";
            case "1:00am":
                return "1:00am-2:00am";
            case "2:00am":
                return "2:00am-3:00am";
            case "3:00am":
                return "3:00am-4:00am";
            case "4:00am":
                return "4:00am-5:00am";
            case "5:00am":
                return "5:00am-6:00am";
            case "6:00am":
                return "6:00am-7:00am";
            case "7:00am":
                return "7:00am-8:00am";
            case "8:00am":
                return "8:00am-9:00am";
            case "9:00am":
                return "9:00am-10:00am";
            case "10:00am":
                return "10:00am-11:00am";
            case "11:00am":
                return "11:00am-12:00pm";
            case "12:00pm":
                return "12:00pm-1:00pm";
            case "1:00pm":
                return "1:00pm-2:00pm";
            case "2:00pm":
                return "2:00pm-3:00pm";
            case "3:00pm":
                return "3:00pm-4:00pm";
            case "4:00pm":
                return "4:00pm-5:00pm";
            case "5:00pm":
                return "5:00pm-6:00pm";
            case "6:00pm":
                return "6:00pm-7:00pm";
            case "7:00pm":
                return "7:00pm-8:00pm";
            case "8:00pm":
                return "8:00pm-9:00pm";
            case "9:00pm":
                return "9:00pm-10:00pm";
            case "10:00pm":
                return "10:00pm-11:00pm";
            case "11:00pm":
                return "11:00pm-12:00am";
            default:
                return "error";
        }
    }

}
