package s3.thisisbetter.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import s3.thisisbetter.AppConstants;
import s3.thisisbetter.adapters.InviteArrayAdapter;
import s3.thisisbetter.R;
import s3.thisisbetter.model.DB;
import s3.thisisbetter.model.Event;

public class InviteActivity extends AppCompatActivity {

    private InviteArrayAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new InviteArrayAdapter(InviteActivity.this, new ArrayList<String>());
        listView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                // Set up the input
                final EditText input = new EditText(InviteActivity.this);
                // Specify the type of input expected;
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                AlertDialog dialog = new AlertDialog.Builder(InviteActivity.this)
                        .setTitle("Invite by Email")
                        .setView(input)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hideKeyboard(input);
                                addEmail(input.getText().toString());
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hideKeyboard(input);
                                dialog.cancel();
                            }
                        })
                        .create();

                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        Button finishBtn = (Button) findViewById(R.id.button);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInvites();
                goToHomeScreen();
            }
        });
    }

    private void hideKeyboard(EditText input) {
        if (input != null) {
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }

    private void saveInvites() {

        final ArrayList<String> invitedEmails = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            invitedEmails.add(adapter.getItem(i));
        }

        final ArrayList<String> invitedUIDs = new ArrayList<>();

        // Get the uids associated with each of these emails

        Firebase usersRef = DB.getUsersRef();
        // Get all of the data for users
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userData : dataSnapshot.getChildren()) {
                    String userEmail = (String) userData.child("email").getValue();

                    // How this is implemented, only emails that are already linked to an account
                    // in our database will be invited
                    if (invitedEmails.contains(userEmail)) {
                        invitedUIDs.add(userData.getKey());
                    }
                }

                updateEventWithInvitations(invitedUIDs);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }

    private void updateEventWithInvitations(final ArrayList<String> invitedUIDs) {
        Intent prevIntent = getIntent();
        String eventID = prevIntent.getStringExtra(AppConstants.EXTRA_EVENT_ID);

        final Firebase eventRef = DB.getEventsRef().child(eventID);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event e = dataSnapshot.getValue(Event.class);
                for (String id : invitedUIDs) {
                    e.inviteByID(id, false);
                }
                eventRef.setValue(e);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }

    private void goToHomeScreen() {
        Intent intent = new Intent(InviteActivity.this, TabbedEventActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other activities from the stack
        startActivity(intent);
    }

    public void addEmail(String email) {
        adapter.addEmail(email);
    }

}
