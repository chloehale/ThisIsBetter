package s3.thisisbetter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import s3.thisisbetter.R;
import s3.thisisbetter.model.TimeBlock;

/**
 * Created by Chloe on 3/19/16.
 */
public class ViewResponseArrayAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> values;
    private TimeBlock timeBlock;

    public ViewResponseArrayAdapter(Context context, ArrayList<String> values, TimeBlock timeBlock) {
        super(context, R.layout.event_invited_cell_view, values);
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
            rowView = inflater.inflate(R.layout.time_cell_view, parent, false);
        } else {
            rowView = convertView;
        }

        TextView text = (TextView) rowView.findViewById(R.id.time_text);
        text.setText(values.get(position));

        int numAvailable = timeBlock.calculateNumberOfPeopleAvailable(position);
        if (numAvailable > 0) {
            TextView availabilityText = (TextView) rowView.findViewById(R.id.availability_text);
            availabilityText.setText(numAvailable + " available");
        }

        return rowView;
    }

}
