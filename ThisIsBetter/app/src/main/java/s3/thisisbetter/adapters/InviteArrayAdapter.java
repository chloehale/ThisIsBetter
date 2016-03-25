package s3.thisisbetter.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import s3.thisisbetter.R;

/**
 * Created by psoder3 on 3/17/16.
 */
public class InviteArrayAdapter extends ArrayAdapter<String> {

    private final Activity context;
    public final ArrayList<String> values;
    public String ownerEmail;

    public InviteArrayAdapter(Activity context, ArrayList<String> values) {
        super(context, R.layout.fragment_list_single, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.fragment_list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        txtTitle.setText(values.get(position));
        return rowView;
    }

    public void addEmail(String e) {
        if(e.equals(ownerEmail)) {
            String modified = e + " (You are the Owner)";
            this.insert(modified, 0);
        } else {
            this.insert(e, this.getCount());
        }
    }

    public void setOwnerEmail(String e) {
        ownerEmail = e;
    }
}