package s3.thisisbetter.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import s3.thisisbetter.R;
import s3.thisisbetter.model.Event;

/**
 * Created by Chloe on 3/17/16.
 */
public class EventInvitedArrayAdapter extends ArrayAdapter<Event> {
    private final Context context;
    private final ArrayList<Event> values;
    private Map<String, String> uidToEmail;

    public EventInvitedArrayAdapter(Context context, ArrayList<Event> values) {
        super(context, R.layout.event_owned_cell_view, values);
        this.context = context;
        this.values = values;
        this.uidToEmail = new HashMap<>();
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

        String ownerDescription = "Owned By: " + uidToEmail.get(event.getOwnerID());
        numRespondedView.setText(ownerDescription);

        return rowView;
    }

    public void addEvent(Event e, String ownerEmail) {
        this.insert(e, 0);
        uidToEmail.put(e.getOwnerID(), ownerEmail);
    }

    public void deleteEvent(Event e) {
        this.remove(e);
    }
}
