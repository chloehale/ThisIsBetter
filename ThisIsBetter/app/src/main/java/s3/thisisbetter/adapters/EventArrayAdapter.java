package s3.thisisbetter.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import s3.thisisbetter.R;
import s3.thisisbetter.model.Event;

/**
 * Created by Chloe on 3/17/16.
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    private final Context context;
    private final ArrayList<Event> values;

    public EventArrayAdapter(Context context, ArrayList<Event> values) {
        super(context, R.layout.event_cell_view, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // See if we can reuse the view passed to us
        View rowView;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.event_cell_view, parent, false);
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

        return rowView;
    }

    public void addEvent(Event e) {
        this.insert(e, 0);
    }

    public void deleteEvent(Event e) {
        this.remove(e);
    }
}
