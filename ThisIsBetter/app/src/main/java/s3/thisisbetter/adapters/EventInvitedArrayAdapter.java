package s3.thisisbetter.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import s3.thisisbetter.AppConstants;
import s3.thisisbetter.R;
import s3.thisisbetter.activities.AvailabilityInputActivity;
import s3.thisisbetter.activities.ViewResponseActivity;
import s3.thisisbetter.fragments.EventsInvitedFragment;
import s3.thisisbetter.model.DB;
import s3.thisisbetter.model.Event;

/**
 * Created by Chloe on 3/17/16.
 */
public class EventInvitedArrayAdapter extends ArrayAdapter<Event> {
    private final Context context;
    private final ArrayList<Event> values;
    private Map<String, String> uidToEmail;
    private Map<String, Event> eventIDToObject;
    public final static String PARENT_TYPE = "invitation_tab";


    public EventInvitedArrayAdapter(Context context, ArrayList<Event> values) {
        super(context, R.layout.cell_event_basic, values);
        this.context = context;
        this.values = values;
        this.uidToEmail = new HashMap<>();
        eventIDToObject = new HashMap<>();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Event event = values.get(position);
        boolean haveResponded = event.getInvitedHaveResponded().get(DB.getMyUID());

        final View rowView;
        if(haveResponded) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.cell_event_with_view_response_button, parent, false);

            Button editButton = (Button) rowView.findViewById(R.id.view_response_button);
            editButton.setFocusable(false);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event e = getItem(position);
                    String eventID = getEventID(e);

                    Intent intent = new Intent(getContext(), ViewResponseActivity.class);
                    intent.putExtra(AppConstants.EXTRA_EVENT_ID, eventID);
                    intent.putExtra(AppConstants.EXTRA_PARENT_TYPE, PARENT_TYPE);
                    rowView.getContext().startActivity(intent);

//                    Intent intent = new Intent(getContext(), AvailabilityInputActivity.class);
//                    intent.putExtra(AppConstants.EXTRA_PARENT_TYPE, EventsInvitedFragment.PARENT_TYPE);
//                    intent.putExtra(AppConstants.EXTRA_EVENT_ID, eventID);
//                    intent.putExtra(AppConstants.EXTRA_EVENT_TITLE, e.getTitle());
//                    rowView.getContext().startActivity(intent);
                }
            });
        } else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.cell_event_basic, parent, false);
        }

        TextView eventTitleView = (TextView) rowView.findViewById(R.id.first_line);
        TextView detailsView = (TextView) rowView.findViewById(R.id.second_line);
        TextView respondedView = (TextView) rowView.findViewById(R.id.third_line);

        eventTitleView.setText(event.getTitle());
        eventTitleView.setTextColor(Color.DKGRAY);

        String ownerDescription = "Owned By: " + uidToEmail.get(event.getOwnerID());
        detailsView.setText(ownerDescription);

        if(haveResponded && respondedView != null) {
            respondedView.setText("You've Responded To This Invitation");
        }

        return rowView;
    }

    public String getEventID(Event e) {
        String eventID = null;
        for (Map.Entry<String, Event> entry : eventIDToObject.entrySet()) {
            if(entry.getValue().equals(e)) {
                eventID = entry.getKey();
                break;
            }
        }
        return eventID;
    }

    public void addEvent(Event e, String eventID, String ownerEmail) {
        boolean haveResponded = e.getInvitedHaveResponded().get(DB.getMyUID());

        if(haveResponded) {
            this.insert(e, getCount());
        } else {
            this.insert(e, 0);
        }

        uidToEmail.put(e.getOwnerID(), ownerEmail);
        eventIDToObject.put(eventID, e);
    }

    public void editEvent(Event e, String eventID) {
        Event oldEventObject = eventIDToObject.get(eventID);
        int position = this.getPosition(oldEventObject);
        this.remove(oldEventObject);
        this.insert(e, position);
        eventIDToObject.put(eventID, e);
    }

    public void deleteEvent(Event e) {
        this.remove(e);
    }
}
