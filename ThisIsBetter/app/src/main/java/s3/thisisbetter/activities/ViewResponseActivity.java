package s3.thisisbetter.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import s3.thisisbetter.AppConstants;
import s3.thisisbetter.R;
import s3.thisisbetter.adapters.EventOwnedArrayAdapter;
import s3.thisisbetter.adapters.ViewResponseArrayAdapter;
import s3.thisisbetter.dialogs.ViewResponseDialog;
import s3.thisisbetter.model.AvailabilityBlock;
import s3.thisisbetter.model.DB;
import s3.thisisbetter.model.Event;
import s3.thisisbetter.model.TimeBlock;


public class ViewResponseActivity extends AppCompatActivity {

    private String parentType;
    private String eventID;
    private Toolbar toolbar;
    private Map<Integer, List<AvailabilityBlock>> availabilityBlocks;
    private int totalInvitedCount;
    private Firebase queryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_response);

        //Change the color of the status bar if the version > lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        // Get the data
        Intent intent = getIntent();
        parentType = intent.getStringExtra(AppConstants.EXTRA_PARENT_TYPE);
        eventID = intent.getStringExtra(AppConstants.EXTRA_EVENT_ID);
        getEventData();

        setupBackButton();

        //disable default toolbar text
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onStop() {
        super.onStop();

        queryRef.removeEventListener(eventListener);
    }


    private void setupBackButton() {
        ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getEventData() {
        queryRef = DB.getEventsRef().child(eventID);
        queryRef.addValueEventListener(eventListener);
    }

    private ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Event event = snapshot.getValue(Event.class);
            setTitleText(event);
            setResponseStatusText(event);
            getAvailabilityData(event);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            System.out.println("Failed to read event: " + firebaseError.getMessage());
        }
    };

    private void getAvailabilityData(Event event) {

        totalInvitedCount = event.getInvitedHaveResponded().size();
        final Set<String> DATE_IDs = event.getProposedDateIDs().keySet();

        //separate into sets of individuals who have and haven't responded
        Map<String, Boolean> invitedHaveResponded = event.getInvitedHaveResponded();
        final Set<String> respondedUserIds = new TreeSet<>();
        final Set<String> notRespondedUserIds = new TreeSet<>();

        for (Map.Entry<String, Boolean> entry : invitedHaveResponded.entrySet()) {
            if (entry.getValue())
                respondedUserIds.add(entry.getKey());
            else
                notRespondedUserIds.add(entry.getKey());
        }

        //query for dates and build availability blocks
        if (totalInvitedCount > 1) {

            Query queryRef = DB.getDatesRef();
            queryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //reset and clear out all views
                    availabilityBlocks = new TreeMap<>(Collections.reverseOrder());
                    LinearLayout responseListLayout = (LinearLayout) findViewById(R.id.response_lists);
                    responseListLayout.removeAllViews();

                    for (String dateID : DATE_IDs) {

                        DataSnapshot dateSnapshot = dataSnapshot.child(dateID);
                        TimeBlock timeBlock = dateSnapshot.getValue(TimeBlock.class);

                        for (Map.Entry<String, Map<String, Boolean>> entry : timeBlock.getAvailability().entrySet()) {

                            Set<String> availableUserIds = entry.getValue().keySet();

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(timeBlock.getYear(), timeBlock.getMonth(), timeBlock.getDay());

                            AvailabilityBlock availabilityBlock = new AvailabilityBlock(totalInvitedCount, calendar, timeBlock.getMonthString(), entry.getKey(),
                                                                                        availableUserIds, respondedUserIds, notRespondedUserIds);

                            if (availabilityBlocks.get(availableUserIds.size()) != null) {
                                availabilityBlocks.get(availableUserIds.size()).add(availabilityBlock);
                            } else {
                                List<AvailabilityBlock> initialList = new ArrayList<>();
                                initialList.add(availabilityBlock);
                                availabilityBlocks.put(availableUserIds.size(), initialList);
                            }
                        }
                    }
                    setAvailabilityResponseList();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }


    private void setTitleText(Event event) {
        TextView title = (TextView) findViewById(R.id.title_text_view);
        title.setText("Responses for \"" + event.getTitle() + "\"");
    }

    private void setResponseStatusText(Event event) {
        int respondedCount = 0;
        int totalCount = event.getInvitedHaveResponded().size();

        for (Boolean responded : event.getInvitedHaveResponded().values()) {
            if (responded)
                respondedCount++;
        }

        TextView responseStatusTitle = (TextView) findViewById(R.id.status_text_view);
        TextView subStatusText = (TextView) findViewById(R.id.status_sub_text_view);
        if (totalCount == 1) {
            responseStatusTitle.setText("You are the only member of this event");

            Button responseInviteButton = (Button) findViewById(R.id.response_invite_button);
            responseInviteButton.setVisibility(View.VISIBLE);
            responseInviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewResponseActivity.this, InviteActivity.class);
                    intent.putExtra(AppConstants.EXTRA_EVENT_ID, eventID);
                    startActivity(intent);
                }
            });
        }
        else if (totalCount == respondedCount) {
            subStatusText.setVisibility(View.GONE);
            responseStatusTitle.setText("Everyone has responded");
            responseStatusTitle.setTextColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }
        else {
            subStatusText.setVisibility(View.GONE);
            responseStatusTitle.setText(respondedCount + " out of " + totalCount + " have responded");
        }
    }


    private void setAvailabilityResponseList() {

        LinearLayout responseListLayout = (LinearLayout) findViewById(R.id.response_lists);
        boolean responsePresent = false;

        for (Map.Entry<Integer, List<AvailabilityBlock>> entry : availabilityBlocks.entrySet()) {

            responsePresent = true;

            TextView responseRatio = new TextView(this);
            responseRatio.setText(entry.getKey() + " out of " + totalInvitedCount + " people available");
            responseRatio.setTextColor(this.getResources().getColor(R.color.colorGrayDark));
            responseRatio.setTextSize(16);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            params.setMargins(32, 32, 0, 16);
            responseRatio.setLayoutParams(params);
            responseRatio.setId(entry.getKey());
            responseListLayout.addView(responseRatio);

            ListView availabilityListView = new ListView(this);
            ViewResponseArrayAdapter adapter = new ViewResponseArrayAdapter(responseListLayout.getContext(), entry.getValue());
            availabilityListView.setAdapter(adapter);
            setDynamicHeight(availabilityListView);
//            LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//            listParams.height = 118 * entry.getValue().size();
//            availabilityListView.setLayoutParams(listParams);

            setUpListClickListener(availabilityListView);

            responseListLayout.addView(availabilityListView);

        }

        TextView subStatusText = (TextView) findViewById(R.id.status_sub_text_view);
        if (!responsePresent) {
            subStatusText.setVisibility(View.VISIBLE);
            subStatusText.setText("No one who responded was ever available");
        }
        else {
            subStatusText.setVisibility(View.GONE);
        }
    }


    public static void setDynamicHeight(ListView listView) {
        ViewResponseArrayAdapter adapter = (ViewResponseArrayAdapter) listView.getAdapter();
        //check adapter if null
        if (adapter == null) {
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layoutParams.height = height + (listView.getDividerHeight() * (adapter.getCount()));
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
    }


    private void setUpListClickListener(ListView listView) {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewResponseArrayAdapter adapter = (ViewResponseArrayAdapter)parent.getAdapter();
                AvailabilityBlock availabilityBlock = adapter.getItem(position);
                createDialog(availabilityBlock);
            }
        });
    }

    private void createDialog(AvailabilityBlock availabilityBlock) {
        FragmentManager fm = getSupportFragmentManager();
        ViewResponseDialog overlay = ViewResponseDialog.newInstance(availabilityBlock);
        overlay.show(fm, "FragmentDialog");
    }

}
