package s3.thisisbetter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import s3.thisisbetter.R;
import s3.thisisbetter.model.TimeBlock;

/**
 * Created by Chloe on 3/19/16.
 */
public class AvailabilityInputArrayAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> values;
    private TimeBlock timeBlock;

    public AvailabilityInputArrayAdapter(Context context, ArrayList<String> values, TimeBlock timeBlock) {
        super(context, R.layout.cell_event_invited_view, values);
        this.context = context;
        this.values = values;
        this.timeBlock = timeBlock;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // See if we can reuse the view passed to us
        View rowView;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.cell_availability_input_view, parent, false);
        } else {
            rowView = convertView;
        }

        TextView text = (TextView) rowView.findViewById(R.id.time_text);
        text.setText(values.get(position));

        int numAvailable = timeBlock.calculateNumberOfPeopleAvailableAtTime(position);
        TextView availabilityText = (TextView) rowView.findViewById(R.id.availability_text);
        if (numAvailable > 0) {
            availabilityText.setText(numAvailable + " available");
        } else {
            availabilityText.setText("");
        }

        if (timeBlock.isAvailableAtTime(position)) {
            rowView.setBackgroundResource(R.color.colorSelected);
        }
        else {
            rowView.setBackgroundResource(R.color.colorLight);
        }

        return rowView;
    }

}
