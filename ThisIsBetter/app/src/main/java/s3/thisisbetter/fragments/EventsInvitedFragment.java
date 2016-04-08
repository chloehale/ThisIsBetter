package s3.thisisbetter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import s3.thisisbetter.AppConstants;
import s3.thisisbetter.R;
import s3.thisisbetter.activities.AvailabilityInputActivity;
import s3.thisisbetter.activities.ViewResponseActivity;
import s3.thisisbetter.adapters.EventInvitedArrayAdapter;
import s3.thisisbetter.model.DB;
import s3.thisisbetter.model.Event;
import s3.thisisbetter.model.InvitedListItem;
import s3.thisisbetter.model.User;

/**
 * The fragment for the Invited tab
 */
public class EventsInvitedFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private EventInvitedArrayAdapter adapter;
    public final static String PARENT_TYPE = "invitation_tab";


    public EventsInvitedFragment() { }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EventsInvitedFragment newInstance(int sectionNumber) {
        EventsInvitedFragment fragment = new EventsInvitedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events_invited, container, false);
        setUpListView(rootView);

        return rootView;
    }

    private void setUpListView(View rootView) {
        // Create the firebase query that grabs all of the events I'm invited to.
        String uid = DB.getMyUID();
        Query queryRef = DB.getEventsRef().orderByChild(Event.INVITED_KEY + "/" + uid);
        queryRef.addChildEventListener(eventListener);
        DB.monitorChildListener(queryRef, eventListener);


        // Set up the adapter
        adapter = new EventInvitedArrayAdapter(rootView.getContext(), new ArrayList<InvitedListItem>());
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InvitedListItem item = adapter.getItem(position);
                if(item.isSection()) { return; }

                Event e = (Event) item;
                String eventID = adapter.getEventID(e);

                Intent intent = new Intent(getContext(), AvailabilityInputActivity.class);
                intent.putExtra(AppConstants.EXTRA_PARENT_TYPE, PARENT_TYPE);
                intent.putExtra(AppConstants.EXTRA_EVENT_ID, eventID);
                intent.putExtra(AppConstants.EXTRA_EVENT_TITLE, e.getTitle());
                startActivity(intent);
            }
        });
    }

    private ChildEventListener eventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            final Event e = dataSnapshot.getValue(Event.class);
            String ownerUID = e.getOwnerID();
            String myUID = DB.getMyUID();

            // The invited tab doesn't show any events you own or events that you aren't invited to
            if(ownerUID.equals(myUID)) { return; }
            if(!e.getInvitedHaveResponded().containsKey(myUID)) { return; }

            final String eventID = dataSnapshot.getKey();

            Firebase ownerEmailRef = DB.getUsersRef().child(ownerUID).child(User.EMAIL_KEY);
            ownerEmailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String ownerEmail = (String) dataSnapshot.getValue();
                    adapter.addEvent(e, eventID, ownerEmail);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) { }
            });
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Event e = dataSnapshot.getValue(Event.class);
            String ownerID = e.getOwnerID();
            // The invited tab doesn't show any events you own
            if(ownerID.equals(DB.getMyUID())) { return; }
            adapter.editEvent(e, dataSnapshot.getKey());
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {}

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

        @Override
        public void onCancelled(FirebaseError firebaseError) {}
    };
}
