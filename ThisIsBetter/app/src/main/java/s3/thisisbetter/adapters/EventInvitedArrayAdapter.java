package s3.thisisbetter.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import s3.thisisbetter.AppConstants;
import s3.thisisbetter.R;
import s3.thisisbetter.activities.ViewResponseActivity;
import s3.thisisbetter.model.DB;
import s3.thisisbetter.model.Event;
import s3.thisisbetter.model.EventListItem;
import s3.thisisbetter.model.SectionItem;

/**
 * Created by Chloe on 3/17/16.
 */
public class EventInvitedArrayAdapter extends ArrayAdapter<EventListItem> {
    private final Context context;
    private final ArrayList<EventListItem> values;
    private Map<String, String> uidToEmail;
    private Map<String, Event> eventIDToObject;
    public final static String PARENT_TYPE = "invitation_tab";

    private int numAwaitingResponse;
    private int numRespondedTo;


    public EventInvitedArrayAdapter(Context context, ArrayList<EventListItem> values) {
        super(context, R.layout.cell_event_with_view_response_button, values);
        this.values = values;
        this.context = context;
        this.values.add(new SectionItem("Awaiting Your Response"));
        this.values.add(new SectionItem("You've Responded"));
        this.uidToEmail = new HashMap<>();
        eventIDToObject = new HashMap<>();
        numAwaitingResponse = 0;
        numRespondedTo = 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        EventListItem item = values.get(position);
        View rowView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (item.isSection()) {
            SectionItem section = (SectionItem) item;
            rowView = inflater.inflate(R.layout.cell_section_header, parent, false);
            TextView sectionTitleView = (TextView) rowView.findViewById(R.id.section_title);
            sectionTitleView.setText(section.getTitle());

            TextView noInvitesView = (TextView) rowView.findViewById(R.id.no_events_text);
            if((position == 0 && numAwaitingResponse == 0) || (position != 0 && numRespondedTo == 0)) {
                noInvitesView.setVisibility(View.VISIBLE);
            } else {
                noInvitesView.setVisibility(View.GONE);
            }

        } else {
            Event event = (Event) item;
            rowView = inflater.inflate(R.layout.cell_event_with_view_response_button, parent, false);

            TextView eventTitleView = (TextView) rowView.findViewById(R.id.first_line);
            eventTitleView.setText(event.getTitle());
            eventTitleView.setTextColor(Color.DKGRAY);

            TextView detailsView = (TextView) rowView.findViewById(R.id.second_line);
            String ownerDescription = "Owned By: " + uidToEmail.get(event.getOwnerID());
            detailsView.setText(ownerDescription);

            setupViewResponseButton(rowView, event);
        }

        return rowView;
    }

    private void setupViewResponseButton(final View rowView, final Event event) {
        boolean haveResponded = event.getInvitedHaveResponded().get(DB.getMyUID());
        Button editButton = (Button) rowView.findViewById(R.id.view_response_button);

        if(haveResponded) {
            editButton.setVisibility(View.VISIBLE);
            editButton.setFocusable(false);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String eventID = getEventID(event);

                    Intent intent = new Intent(getContext(), ViewResponseActivity.class);
                    intent.putExtra(AppConstants.EXTRA_EVENT_ID, eventID);
                    intent.putExtra(AppConstants.EXTRA_PARENT_TYPE, PARENT_TYPE);
                    rowView.getContext().startActivity(intent);
                }
            });
        } else {
            editButton.setVisibility(View.GONE);
        }
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
        insertEvent(e, eventID);
        uidToEmail.put(e.getOwnerID(), ownerEmail);
    }

    public void editEvent(Event e, String eventID) {
        Event oldEventObject = eventIDToObject.get(eventID);
        if(oldEventObject != null) {
            this.remove(oldEventObject);

            boolean oldHaveResponded = oldEventObject.getInvitedHaveResponded().get(DB.getMyUID());
            if(oldHaveResponded) {
                numRespondedTo--;
            } else {
                numAwaitingResponse--;
            }
        }

        insertEvent(e, eventID);
    }

    private void insertEvent(Event e, String eventID) {
        boolean haveResponded = e.getInvitedHaveResponded().get(DB.getMyUID());

        if(haveResponded) {
            numRespondedTo++;
            this.insert(e, getCount());
        } else {
            numAwaitingResponse++;
            this.insert(e, numAwaitingResponse);
        }

        eventIDToObject.put(eventID, e);
    }
}
