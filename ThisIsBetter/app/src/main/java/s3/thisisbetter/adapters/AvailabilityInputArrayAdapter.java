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
        super(context, R.layout.cell_availability_input_view, values);
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

        View topDivider = rowView.findViewById(R.id.top_divider);
        if (position == 0) {
            topDivider.setVisibility(View.VISIBLE);
        } else {
            topDivider.setVisibility(View.GONE);
        }

        TextView text = (TextView) rowView.findViewById(R.id.time_text);
        text.setText(values.get(position));

        if (timeBlock.isAvailableAtTime(position)) {
            rowView.setBackgroundResource(R.color.colorSelected);
            setNumAvailableText(true, position, rowView);
        }
        else {
            rowView.setBackgroundResource(R.color.colorWhite);
            setNumAvailableText(false, position, rowView);
        }

        return rowView;
    }

    private void setNumAvailableText(boolean userIsAvailable, int position, View rowView) {
        int numAvailable = timeBlock.calculateNumberOfPeopleAvailableAtTime(position);

        TextView availabilityText = (TextView) rowView.findViewById(R.id.availability_text);

        if (numAvailable == 0) {
            availabilityText.setText("");
        }  else if (numAvailable == 1) {
            if(userIsAvailable) {
                availabilityText.setText("You are available");
            } else {
                availabilityText.setText("1 person available");
            }
        } else {
            if(userIsAvailable) {
                int others = numAvailable - 1;
                String addS = others > 1 ? "s" : "";
                availabilityText.setText("You and " + others + " other" + addS + " available");
            } else {
                availabilityText.setText(numAvailable + " people available");
            }
        }
    }

}
