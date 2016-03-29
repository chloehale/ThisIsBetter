package s3.thisisbetter.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import s3.thisisbetter.AppConstants;
import s3.thisisbetter.R;
import s3.thisisbetter.model.DB;
import s3.thisisbetter.model.TimeBlock;

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

    private void openAlert(final int position)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder
                .setTitle("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //values.remove(position);
                    /*
                        Intent prevIntent = getIntent();
                        String eventID = prevIntent.getStringExtra(AppConstants.EXTRA_EVENT_ID);
                        Query eventQuery = DB.getEventsRef().orderByChild(TimeBlock.EVENT_ID_KEY).equalTo(eventID);

                        eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot dateData : dataSnapshot.getChildren()) {
                                    TimeBlock date = dateData.getValue(TimeBlock.class);

                                    for(TimeBlock updatedDate : dates) {
                                        if(updatedDate.getDay() == date.getDay() &&
                                                updatedDate.getMonth() == date.getMonth() &&
                                                updatedDate.getYear() == date.getYear()) {

                                            Firebase dateRef = DB.getDatesRef().child(dateData.getKey());
                                            dateRef.setValue(updatedDate);
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) { }
                        });
                    */
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {


        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.fragment_list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        String email = values.get(position);
        ImageButton deleteBtn = (ImageButton) rowView.findViewById(R.id.delete_button);

        if(email == ownerEmail) {
            deleteBtn.setVisibility(View.GONE);
            email += " (You are the Owner)";
        } else {
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openAlert(position);
                }
            });
        }

        txtTitle.setText(email);

        return rowView;
    }

    public void addEmail(String e) {
        if(e.equals(ownerEmail)) {
            this.insert(ownerEmail, 0);
        } else {
            this.insert(e, this.getCount());
        }
    }

    public void setOwnerEmail(String e) {
        ownerEmail = e;
    }
}