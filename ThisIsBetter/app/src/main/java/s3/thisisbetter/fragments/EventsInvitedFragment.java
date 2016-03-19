package s3.thisisbetter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import s3.thisisbetter.R;
import s3.thisisbetter.adapters.EventInvitedArrayAdapter;
import s3.thisisbetter.model.DB;
import s3.thisisbetter.model.Event;

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
        // Create the firebase query that grabs all of the events I own.
        String uid = DB.getUID();
        Query queryRef = DB.getEventsRef().orderByChild(Event.INVITED_KEY + "/" + uid).equalTo(false);
        queryRef.addChildEventListener(eventListener);

        // Set up the adapter
        adapter = new EventInvitedArrayAdapter(rootView.getContext(), new ArrayList<Event>());
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    private ChildEventListener eventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final Event e = dataSnapshot.getValue(Event.class);

            String ownerID = e.getOwnerID();
            Firebase ownerEmailRef = DB.getUsersRef().child(ownerID).child("email");
            ownerEmailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String ownerEmail = (String) dataSnapshot.getValue();
                    adapter.addEvent(e, ownerEmail);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) { }
            });
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
//            Event e = dataSnapshot.getValue(Event.class);
//            adapter.deleteEvent(e);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

        @Override
        public void onCancelled(FirebaseError firebaseError) {}
    };
}
