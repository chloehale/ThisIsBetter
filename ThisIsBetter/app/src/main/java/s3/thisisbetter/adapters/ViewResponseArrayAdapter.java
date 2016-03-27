package s3.thisisbetter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import s3.thisisbetter.R;
import s3.thisisbetter.model.AvailabilityBlock;

/**
 * Created by steve on 3/26/16.
 */
public class ViewResponseArrayAdapter extends ArrayAdapter<AvailabilityBlock> {

    private Context context;
    private List<AvailabilityBlock> availabilityBlockList;

    public ViewResponseArrayAdapter(Context context, List<AvailabilityBlock> availabilityBlockList) {
        super(context, R.layout.cell_event_owned_view, availabilityBlockList);
        this.context = context;
        this.availabilityBlockList = availabilityBlockList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.cell_response_view, parent, false);

        final AvailabilityBlock availabilityBlock = availabilityBlockList.get(position);

        TextView dateInfoText = (TextView) rowView.findViewById(R.id.date_info);
        dateInfoText.setText(availabilityBlock.getWeekday() + ", " + availabilityBlock.getMonthDay());

        TextView timeRangeInfoText = (TextView) rowView.findViewById(R.id.time_range);
        timeRangeInfoText.setText(availabilityBlock.getTimeRange());

        TextView responseRatioText = (TextView) rowView.findViewById(R.id.response_ratio);
        responseRatioText.setText(availabilityBlock.getAvailableCount() + "/" + availabilityBlock.getTotalInvitedCount());

        return rowView;
    }

}

