package s3.thisisbetter.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import s3.thisisbetter.AppConstants;
import s3.thisisbetter.R;
import s3.thisisbetter.activities.AvailabilityInputActivity;
import s3.thisisbetter.activities.InviteActivity;
import s3.thisisbetter.fragments.EventsInvitedFragment;
import s3.thisisbetter.model.DB;
import s3.thisisbetter.model.Event;
import s3.thisisbetter.model.EventListItem;
import s3.thisisbetter.model.SectionItem;

/**
 * Created by Chloe on 3/17/16.
 */
public class EventOwnedArrayAdapter extends ArrayAdapter<EventListItem> {
    private final Context context;
    private final ArrayList<EventListItem> values;
    private Map<String, Event> eventIDToObject;

    private int numAwaitingResponse;
    private int numEveryoneResponded;

    public EventOwnedArrayAdapter(Context context, ArrayList<EventListItem> values) {
        super(context, R.layout.cell_event_with_edit_button, values);
        this.context = context;
        this.values = values;
        this.values.add(new SectionItem("Awaiting Responses"));
        this.values.add(new SectionItem("Everyone Responded"));
        eventIDToObject = new HashMap<>();
        numAwaitingResponse = 0;
        numEveryoneResponded = 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventListItem item = values.get(position);
        View rowView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (item.isSection()) {
            SectionItem section = (SectionItem) item;
            rowView = inflater.inflate(R.layout.cell_section_header, parent, false);
            TextView sectionTitleView = (TextView) rowView.findViewById(R.id.section_title);
            sectionTitleView.setText(section.getTitle());

            TextView noEventsView = (TextView) rowView.findViewById(R.id.no_events_text);
            if((position == 0 && numAwaitingResponse == 0) || (position != 0 && numEveryoneResponded == 0)) {
                noEventsView.setVisibility(View.VISIBLE);
            } else {
                noEventsView.setVisibility(View.GONE);
            }

        } else {
            final Event event = (Event) item;
            rowView = inflater.inflate(R.layout.cell_event_with_edit_button, parent, false);

            TextView eventTitleView = (TextView) rowView.findViewById(R.id.first_line);
            TextView numRespondedView = (TextView) rowView.findViewById(R.id.second_line);

            eventTitleView.setText(event.getTitle());
            eventTitleView.setTextColor(Color.DKGRAY);

            int numResponded = event.determineNumberResponded();
            int totalInvites = event.getInvitedHaveResponded().size();
            String respondedText = "Nobody invited";
            if (totalInvites > 1)
            {
                if (numResponded == totalInvites) {
                    respondedText = "";
                }
                else {
//                    respondedText = (numResponded - 1) + " out of " + (totalInvites - 1) + " people have responded";
                    int waitingCount = totalInvites - numResponded;
                    if (waitingCount == 1)
                        respondedText = "Waiting for " + waitingCount + " person to respond";
                    else
                        respondedText = "Waiting for " + waitingCount + " people to respond";
                }
            }
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
                                case R.id.delete_event:
                                    deleteEvent(v, event);
                                    return true;
                            }
                            return false;
                        }
                    });
                    popup.getMenuInflater().inflate(R.menu.menu_edit_event_popup, popup.getMenu());
                    popup.show();
                }
            });
        }


        return rowView;
    }

    public void goToEditInvites(View v, Event e) {
        // find the eventID
        String eventID = getEventID(e);

        Intent intent = new Intent(v.getContext(), InviteActivity.class);
        intent.putExtra(AppConstants.EXTRA_EVENT_ID, eventID);
        v.getContext().startActivity(intent);
    }

    public String getEventID(EventListItem item) {
        if(item.isSection()) { return null; }

        Event e = (Event) item;
        String eventID = null;
        for (Map.Entry<String, Event> entry : eventIDToObject.entrySet()) {
            if(entry.getValue().equals(e)) {
                eventID = entry.getKey();
                break;
            }
        }
        return eventID;
    }

    public void deleteEvent(View v, final Event e) {
        final String eventID = getEventID(e);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    // Remove the event from the database (this will call the onChildRemoved listener in
                    // EventsIOwnFragment which will remove the event from the array adapter)
                    Firebase eventRef = DB.getEventsRef().child(eventID);
                    eventRef.removeValue();

                    // Remove the associated dates from the database
                    for(String dateID : e.getProposedDateIDs().keySet()) {
                        DB.getDatesRef().child(dateID).removeValue();
                    }
                    }
                })
                .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    })
                .create();

        // show it
        dialog.show();

        int color = v.getResources().getColor(R.color.colorPrimary);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(color);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(color);
    }

    public void goToEditEvent(View v, Event e) {
        String eventID = getEventID(e);

        Intent intent = new Intent(getContext(), AvailabilityInputActivity.class);
        intent.putExtra(AppConstants.EXTRA_PARENT_TYPE, EventsInvitedFragment.PARENT_TYPE);
        intent.putExtra(AppConstants.EXTRA_EVENT_ID, eventID);
        intent.putExtra(AppConstants.EXTRA_EVENT_TITLE, e.getTitle());
        v.getContext().startActivity(intent);
    }

    public void addEvent(Event e, String eventID) {
        insertEvent(e, eventID);
    }

    public void editEvent(Event e, String eventID) {
        Event oldEventObject = eventIDToObject.get(eventID);
        this.remove(oldEventObject);

        int totalInvites = oldEventObject.getInvitedHaveResponded().size();

        if (totalInvites > 1)
        {
            int numResponded = oldEventObject.determineNumberResponded();
            if (numResponded == totalInvites) {
                numEveryoneResponded--;
            }
            else {
                numAwaitingResponse--;
            }
        } else {
            // there is no one invited to this event (besides the owner)
            numAwaitingResponse--;
        }

        insertEvent(e, eventID);
    }

    public void deleteEvent(Event e) {
        this.remove(e);

        int totalInvites = e.getInvitedHaveResponded().size();

        if (totalInvites > 1)
        {
            int numResponded = e.determineNumberResponded();
            if (numResponded == totalInvites) {
                numEveryoneResponded--;
            }
            else {
                numAwaitingResponse--;
            }
        } else {
            // there is no one invited to this event (besides the owner)
            numAwaitingResponse--;
        }
    }

    private void insertEvent(Event e, String eventID) {
        int totalInvites = e.getInvitedHaveResponded().size();

        if (totalInvites > 1)
        {
            int numResponded = e.determineNumberResponded();
            if (numResponded == totalInvites) {
                numEveryoneResponded++;
                this.insert(e, getCount());
            }
            else {
                numAwaitingResponse++;
                this.insert(e, numAwaitingResponse);

            }
        } else {
            // there is no one invited to this event (besides the owner)
            numAwaitingResponse++;
            this.insert(e, numAwaitingResponse);
        }

        eventIDToObject.put(eventID, e);
    }
}
