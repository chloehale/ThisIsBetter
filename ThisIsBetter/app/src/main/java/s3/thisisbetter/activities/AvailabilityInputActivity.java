package s3.thisisbetter.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import s3.thisisbetter.AppConstants;
import s3.thisisbetter.fragments.AvailabilityInputFragment;
import s3.thisisbetter.R;
import s3.thisisbetter.fragments.EventsInvitedFragment;
import s3.thisisbetter.model.DB;
import s3.thisisbetter.model.Event;
import s3.thisisbetter.model.TimeBlock;

public class AvailabilityInputActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static int FADED_ALPHA = 180;
    private static int FULL_ALPHA = 255;
    private String parentType;
    private ArrayList<TimeBlock> dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability_input);

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
        getDates();

        // Set up the buttons
        setupBackButton();
        setupSaveButton();
    }

    /**
     * SETUP METHODS
     */

    private void setupBackButton() {
        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupSaveButton() {
        Button nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parentType.equals(CreateEventActivity.PARENT_TYPE)) {
                    // We just came from the "creating a new event" activity...so it's time to
                    // actually save the event!
                    String eventID = saveNewEvent();

                    Intent intent = new Intent(AvailabilityInputActivity.this, InviteActivity.class);
                    intent.putExtra(AppConstants.EXTRA_EVENT_ID, eventID);
                    startActivity(intent);
                } else {
                    // TODO: this should be called when the user is responding to an invite
                    saveUserAvailability();
                    finish();
                }
            }
        });
    }

    private void onDataRetrieved() {
        Collections.sort(dates);

        // Set up the tabs
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setupViewPager();
        setupTabs();
    }

    private void setupViewPager() {
        viewPager = (ViewPager) findViewById(R.id.scroll_viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < dates.size(); i++) {
            adapter.addFrag(AvailabilityInputFragment.newInstance(i, dates.get(i)), "");
        }
        viewPager.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout = (TabLayout) findViewById(R.id.scroll_tabs);
        tabLayout.setupWithViewPager(viewPager);
        initializeDateTabs();
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                TextView month = (TextView) tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tab_month);
                int index = tab.getPosition();
                month.setText(dates.get(index).getMonthString());

                TextView day = (TextView) tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tab_day);
                day.setTextColor(Color.argb(FULL_ALPHA, 255, 255, 255));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                TextView month = (TextView) tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tab_month);
                month.setText("");

                TextView day = (TextView) tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tab_day);
                day.setTextColor(Color.argb(FADED_ALPHA, 255, 255, 255));
            }
        });
    }

    public void initializeDateTabs() {
        for(int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setCustomView(R.layout.tab_date_view);
            TextView day = (TextView) tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tab_day);
            day.setText(dates.get(i).getShortDescription());
            day.setTextColor(Color.argb(FADED_ALPHA, 255, 255, 255));
        }
        TextView day = (TextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab_day);
        day.setTextColor(Color.argb(FULL_ALPHA, 255, 255, 255));

        TextView month = (TextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab_month);
        month.setText(dates.get(0).getMonthString());
    }

    /**
     * ADAPTERS
     */

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /**
     * HELPER METHODS
     */

    private void getDates() {
        dates = new ArrayList<>();

        Intent prevIntent = getIntent();

        if (parentType.equals(CreateEventActivity.PARENT_TYPE)) {
            // The user is coming from the CreateEventActivity. The dates are in the intent (not the database).
            ArrayList<CalendarDay> datesData = prevIntent.getParcelableArrayListExtra(AppConstants.EXTRA_DATES_ARRAY);

            String uid = DB.getMyUID();
            for(CalendarDay date : datesData) {
                dates.add(new TimeBlock(date.getDay(), date.getMonth(), date.getYear(), uid));
            }

            onDataRetrieved();

        } else if (parentType.equals(EventsInvitedFragment.PARENT_TYPE)) {
            String eventID = prevIntent.getStringExtra(AppConstants.EXTRA_EVENT_ID);
            Query eventDatesQuery = DB.getDatesRef().orderByChild(TimeBlock.EVENT_ID_KEY).equalTo(eventID);

            eventDatesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot dateData : dataSnapshot.getChildren()) {
                        TimeBlock date = dateData.getValue(TimeBlock.class);
                        dates.add(date);
                    }

                    onDataRetrieved();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) { }
            });
        }
    }

    private String saveNewEvent() {
        Intent prevIntent = getIntent();
        String eventTitle = prevIntent.getStringExtra(AppConstants.EXTRA_EVENT_TITLE);

        String uid = DB.getMyUID();
        Firebase eventsRef = DB.getEventsRef();

        // Create and save the event
        Firebase newEventRef = eventsRef.push();
        String newEventID = newEventRef.getKey();
        Event event = new Event(eventTitle, uid);
        event.inviteByID(uid, true);
        saveDatesForEvent(event, newEventID);
        newEventRef.setValue(event);

        return newEventID;
    }

    private void saveDatesForEvent(Event event, String eventID) {
        Firebase datesRef = DB.getDatesRef();
        ArrayList<String> dateIDs = new ArrayList<>();

        for (TimeBlock date : dates) {
            date.setEventID(eventID);
            Firebase newDateRef = datesRef.push();
            newDateRef.setValue(date);
            dateIDs.add(newDateRef.getKey());
        }

        event.addProposedDateIDs(dateIDs);
    }

    private void saveUserAvailability() {
        Intent prevIntent = getIntent();
        String eventID = prevIntent.getStringExtra(AppConstants.EXTRA_EVENT_ID);
        Query eventDatesQuery = DB.getDatesRef().orderByChild(TimeBlock.EVENT_ID_KEY).equalTo(eventID);

        eventDatesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
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

        String uid = DB.getMyUID();
        Firebase eventsRef = DB.getEventsRef().child(eventID).child(Event.INVITED_KEY).child(uid);
        eventsRef.setValue(true);
    }

}
