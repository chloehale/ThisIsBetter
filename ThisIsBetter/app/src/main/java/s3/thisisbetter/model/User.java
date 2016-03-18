package s3.thisisbetter.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chloe on 3/17/16.
 */
public class User {

    private Map<String, Boolean> eventsOwned;
    private Map<String, Boolean> eventsInvitedTo;

    public User() {
        eventsOwned = new HashMap<>();
        eventsInvitedTo = new HashMap<>();
    }

    public Map<String, Boolean> getEventsOwned() {
        return eventsOwned;
    }

    public Map<String, Boolean> getEventsInvitedTo() {
        return eventsInvitedTo;
    }
}
