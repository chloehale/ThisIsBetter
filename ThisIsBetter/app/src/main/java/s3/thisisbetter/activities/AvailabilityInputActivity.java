package s3.thisisbetter.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private String eventTitle;

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
        getData();

        // Set up the buttons
        setupSaveButton();
    }

    /**
     * SETUP METHODS
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // React to the user tapping the back/up icon in the action bar
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupSaveButton() {
        Button nextButton = (Button) findViewById(R.id.next_button);

        if (parentType.equals(CreateEventActivity.PARENT_TYPE)) {
            nextButton.setText(R.string.SaveEvent);
        } else if (parentType.equals(EventsInvitedFragment.PARENT_TYPE)) {
            nextButton.setText(R.string.SaveFinish);
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUserEverAvailable()) {
                    showConfirmationDialog();
                } else {
                    saveTapped();
                }
            }
        });
    }

    private void onDataRetrieved() {
        Collections.sort(dates);

        // Set up the tabs
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(eventTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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

    private boolean isUserEverAvailable() {
        for(TimeBlock t : dates) {
            if(t.isUserEverAvailable()) {
                return true;
            }
        }

        return false;
    }

    private void getData() {
        dates = new ArrayList<>();

        Intent prevIntent = getIntent();
        eventTitle = prevIntent.getStringExtra(AppConstants.EXTRA_EVENT_TITLE);

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

    private void showConfirmationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(AvailabilityInputActivity.this)
                .setTitle("Are You Sure?")
                .setMessage("You haven't set any times that you're available. Are you really that busy?")
                .setCancelable(false)
                .setPositiveButton("Yes, I'm Never Free", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveTapped();
                    }
                })
                .setNegativeButton("No, Go Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();

        dialog.show();

        int color = getResources().getColor(R.color.colorPrimary);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(color);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(color);
    }

    private void saveTapped() {
        if (parentType.equals(CreateEventActivity.PARENT_TYPE)) {
            // We just came from the "creating a new event" activity...so it's time to
            // actually save the event!
            String eventID = saveNewEvent();

            Intent intent = new Intent(AvailabilityInputActivity.this, InviteActivity.class);
            intent.putExtra(AppConstants.EXTRA_EVENT_ID, eventID);
            startActivity(intent);
        } else {
            updateAvailabilityForEvent();
            finish();
        }
    }

    private String saveNewEvent() {
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

    private void updateAvailabilityForEvent() {
        Intent prevIntent = getIntent();
        final String eventID = prevIntent.getStringExtra(AppConstants.EXTRA_EVENT_ID);
        final String myUID = DB.getMyUID();

        final Firebase eventRef = DB.getEventsRef().child(eventID);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event e = dataSnapshot.getValue(Event.class);

                if(e == null) {
                    // the event just got deleted
                    int duration = Toast.LENGTH_LONG;
                    String message = "Sorry, the event you were responding to was deleted.";
                    Toast.makeText(getApplicationContext(), message, duration)
                            .show();
                    return;
                }

                // Check that we're still invited to this event, and set the invitedHaveResponded
                // for this user to TRUE
                if(e.setInviteeResponseTo(true, myUID)) {
                    // if we're able to successfully update the invitedHaveResponded for the event,
                    // then we're still invited to the event and should continue on!
                    eventRef.setValue(e);
                    saveUserAvailabilityForEachDate(eventID);
                } else {
                    // the user just got un-invited from the event
                    int duration = Toast.LENGTH_LONG;
                    String message = "Sorry, you were uninvited from the event you were responding to.";
                    Toast.makeText(getApplicationContext(), message, duration)
                            .show();
                    return;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    private void saveUserAvailabilityForEachDate(String eventID) {
        Query eventDatesQuery = DB.getDatesRef().orderByChild(TimeBlock.EVENT_ID_KEY).equalTo(eventID);
        eventDatesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dateData : dataSnapshot.getChildren()) {
                    TimeBlock date = dateData.getValue(TimeBlock.class);

                    for (TimeBlock updatedDate : dates) {
                        if (updatedDate.getDay() == date.getDay() &&
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
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

}
