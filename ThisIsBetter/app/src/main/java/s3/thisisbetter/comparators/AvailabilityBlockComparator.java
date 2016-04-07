package s3.thisisbetter.comparators;

import java.util.Comparator;

import s3.thisisbetter.model.AvailabilityBlock;

/**
 * Created by steve on 4/6/16.
 */
public class AvailabilityBlockComparator implements Comparator<AvailabilityBlock> {
    @Override
    public int compare(AvailabilityBlock lhs, AvailabilityBlock rhs) {

        if (lhs.getCalendar().compareTo(rhs.getCalendar()) == 0) {
            return lhs.getHour().compareTo(rhs.getHour());
        }
        else {
            return lhs.getCalendar().compareTo(rhs.getCalendar());
        }
    }
}
