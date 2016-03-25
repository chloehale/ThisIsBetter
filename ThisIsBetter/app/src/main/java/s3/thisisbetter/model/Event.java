package s3.thisisbetter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chloe on 3/17/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class Event {
    public static final String TITLE_KEY = "title";
    public static final String OWNER_KEY = "ownerID";
    public static final String INVITED_KEY = "invitedHaveResponded";

    private String title;
    private String ownerID;
    private Map<String, Boolean> invitedHaveResponded;
    private Map<String, Boolean> proposedDateIDs;

    public Event() {
        title = null;
        ownerID = null;
        invitedHaveResponded = new HashMap<>();
        proposedDateIDs = new HashMap<>();
    }

    public Event(String title, String ownerID) {
        this.title = title;
        this.ownerID = ownerID;
        invitedHaveResponded = new HashMap<>();
        proposedDateIDs = new HashMap<>();
    }

    public String getTitle() {
        return title;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public Map<String, Boolean> getInvitedHaveResponded() {
        return invitedHaveResponded;
    }

    public Map<String, Boolean> getProposedDateIDs() {
        return proposedDateIDs;
    }

    public void addProposedDateIDs(ArrayList<String> ids) {
        for (String id : ids) {
            // The true doesn't matter here. What matters is that the id is a key in the map.
            proposedDateIDs.put(id, true);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) {
            return false;
        }
        Event other = (Event)o;
        if (!this.getTitle().equals(other.getTitle())) {
            return false;
        }
        if (!this.getOwnerID().equals(other.getOwnerID())) {
            return false;
        }

        return true;
    }

    public void inviteByID(String id, boolean hasResponded) {
        if(!invitedHaveResponded.containsKey(id)) {
            invitedHaveResponded.put(id, hasResponded);
        }
    }

    public int determineNumberResponded() {
        int numResponded = 0;
        for(Map.Entry<String, Boolean> entry : invitedHaveResponded.entrySet()) {
            if(entry.getValue()) {
                numResponded++;
            }
        }
        return numResponded;
    }

}
