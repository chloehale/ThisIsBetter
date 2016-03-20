package s3.thisisbetter.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import s3.thisisbetter.AppConstants;
import s3.thisisbetter.R;
import s3.thisisbetter.activities.InviteActivity;
import s3.thisisbetter.model.Event;

/**
 * Created by Chloe on 3/17/16.
 */
public class EventOwnedArrayAdapter extends ArrayAdapter<Event> {
    private final Context context;
    private final ArrayList<Event> values;
    private Map<String, Event> eventIDToObject;

    public EventOwnedArrayAdapter(Context context, ArrayList<Event> values) {
        super(context, R.layout.event_owned_cell_view, values);
        this.context = context;
        this.values = values;
        eventIDToObject = new HashMap<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // See if we can reuse the view passed to us
        View rowView;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.event_owned_cell_view, parent, false);
        } else {
            rowView = convertView;
        }

        TextView eventTitleView = (TextView) rowView.findViewById(R.id.first_line);
        TextView numRespondedView = (TextView) rowView.findViewById(R.id.second_line);

        final Event event = values.get(position);
        eventTitleView.setText(event.getTitle());
        eventTitleView.setTextColor(Color.DKGRAY);

        int numResponded = event.determineNumberResponded();
        int totalInvites = event.getInvitedHaveResponded().size();
        String respondedText = numResponded + " / " + totalInvites + " Responded";
        numRespondedView.setText(respondedText);

        // Set up the edit icon
        ImageButton editIcon = (ImageButton) rowView.findViewById(R.id.edit_icon);
        editIcon.setFocusable(false);//set focusable to false required here to still handle click events in the list view
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.edit_event:
                                goToEditEvent(v, event);
                                return true;
                            case R.id.edit_invites:
                                goToEditInvites(v, event);
                                return true;
                        }
                        return false;
                    }
                });
                popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
                popup.show();
            }
        });

        return rowView;
    }

    public void goToEditInvites(View v, Event e) {
        // find the eventID
        String eventID = getEventID(e);

        Intent intent = new Intent(v.getContext(), InviteActivity.class);
        intent.putExtra(AppConstants.EXTRA_EVENT_ID, eventID);
        v.getContext().startActivity(intent);
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

    public void goToEditEvent(View v, Event e) {
        System.out.println("TODO: Go to editing availability");
    }

    public void addEvent(Event e, String eventID) {
        this.insert(e, 0);
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
