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
import s3.thisisbetter.model.Event;
import s3.thisisbetter.model.TimeBlock;

/**
 * Created by psoder3 on 3/17/16.
 */
public class InviteArrayAdapter extends ArrayAdapter<String> {

    private final Activity context;
    public final ArrayList<String> values;
    public String ownerEmail;

    public InviteArrayAdapter(Activity context, ArrayList<String> values) {
        super(context, R.layout.cell_invite, values);
        this.context = context;
        this.values = values;
    }

    private void removeUsersResponses(String eventID, final String userID) {
        Query datesWithEventIDQuery = DB.getDatesRef().orderByChild(TimeBlock.EVENT_ID_KEY).equalTo(eventID);

        datesWithEventIDQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot date : dataSnapshot.getChildren()) {
                    Firebase dateRef = DB.getDatesRef().child(date.getKey());
                    //Query datesWithEventIDQuery = dateRef.child("availability");

                    for (DataSnapshot timeBlock : date.child("availability").getChildren())
                    {
                        for (DataSnapshot userInvited : timeBlock.getChildren()) {
                            if (userInvited.getKey() == userID) {
                                Firebase invitedUserRef = dateRef.child("availability").child(timeBlock.getKey()).child(userInvited.getKey());
                                invitedUserRef.removeValue();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }

    private void openAlert(final int position, View v) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteInviteAt(position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create();

        dialog.show();

        int color = v.getResources().getColor(R.color.colorPrimary);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(color);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(color);
    }

    private void deleteInviteAt(int position) {
        final String email = values.get(position);

        values.remove(position);
        InviteArrayAdapter.this.remove(email);

        Intent prevIntent = context.getIntent();
        final String eventID = prevIntent.getStringExtra(AppConstants.EXTRA_EVENT_ID);
        Query invitedUsersQuery = DB.getEventsRef().child(eventID).child(Event.INVITED_KEY);

        invitedUsersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot invitedData : dataSnapshot.getChildren()) {
                    final String invitedUserID = invitedData.getKey();

                    Query allUsersQuery = DB.getUsersRef();
                    allUsersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot userData : dataSnapshot.getChildren()) {
                                String emailUser = (String)userData.child("email").getValue();
                                String invitedUserIDClicked = userData.getKey();

                                if (email.equals(emailUser))
                                {
                                    if (invitedUserID == invitedUserIDClicked) {
                                        Firebase invitedUserRef = DB.getEventsRef().child(eventID).child(Event.INVITED_KEY).child(invitedUserID);
                                        boolean hasRespondedAlready = (boolean) invitedData.getValue();
                                        invitedUserRef.removeValue();
                                        if (hasRespondedAlready) {
                                            removeUsersResponses(eventID, invitedUserID);
                                            return;
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) { }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.cell_invite, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        String email = values.get(position);
        ImageButton deleteBtn = (ImageButton) rowView.findViewById(R.id.delete_button);

        if(email.equals(ownerEmail)) {
            deleteBtn.setVisibility(View.INVISIBLE);
            email += " (You are the Owner)";
        } else {
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAlert(position, view);
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