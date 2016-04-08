package s3.thisisbetter.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.PopupMenu;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import s3.thisisbetter.fragments.EventsIOwnFragment;
import s3.thisisbetter.R;
import s3.thisisbetter.fragments.EventsInvitedFragment;
import s3.thisisbetter.fragments.SettingsFragment;
import s3.thisisbetter.model.DB;
import s3.thisisbetter.model.Event;

public class TabbedEventActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private int notificationCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DB.getFirebaseRef().addAuthStateListener(authListener);
    }

    private Firebase.AuthStateListener authListener = new Firebase.AuthStateListener() {
        @Override
        public void onAuthStateChanged(AuthData authData) {
            if (authData == null) {
                DB.getFirebaseRef().removeAuthStateListener(authListener);

                Intent intent = new Intent(TabbedEventActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Removes other activities from the stack
                startActivity(intent);
            } else {
                onCreate();
            }
        }
    };

    private void onCreate() {
        setContentView(R.layout.activity_tabbed_event);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mToolbar = (Toolbar)findViewById(R.id.tabbed_events_toolbar);

        if (mToolbar != null) {
            mToolbar.setTitle("This is Better");
        }
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setUpNotifications();
    }

    private void setUpNotifications() {
        // Create the firebase query that grabs all of the events I'm invited to that I haven't
        // responded to yet.
        String uid = DB.getMyUID();
        Query queryRef = DB.getEventsRef().orderByChild(Event.INVITED_KEY + "/" + uid).equalTo(false);
        queryRef.addChildEventListener(eventListener);
        DB.monitorChildListener(queryRef, eventListener);
    }

    private void adjustNotificationCountBy(int delta) {
        notificationCount += delta;
        TabLayout.Tab tab = tabLayout.getTabAt(1);

        if (notificationCount > 0) {
            tab.setText("Invitations (" + notificationCount + ")");
        } else {
            tab.setText("Invitations");
        }
    }

    private ChildEventListener eventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            adjustNotificationCountBy(1);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            adjustNotificationCountBy(-1);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

        @Override
        public void onCancelled(FirebaseError firebaseError) {}
    };

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.logout_item:
                        DB.removeAllListeners();

                        Firebase ref = DB.getFirebaseRef();
                        ref.unauth();

                        return true;
                }
                return false;
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.settings_menu_popup, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            if (position == 0) {
                return EventsIOwnFragment.newInstance(position + 1);
            } else if (position == 1) {
                return EventsInvitedFragment.newInstance(position + 1);
            } else {
                return SettingsFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Events I Own";
                case 1:
                    return "Invitations";
            }
            return null;
        }
    }
}
