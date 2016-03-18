package s3.thisisbetter;

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
import android.widget.TabHost;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AvailabilityInputActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int selectedTabIndex = 0;
    private ArrayList<String> days;
    private ArrayList<String> months;
    private static int FADED_ALPHA = 180;
    private static int FULL_ALPHA = 255;

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

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AvailabilityInputActivity.this, InviteActivity.class);
                startActivity(intent);
            }
        });

        getDummyData();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.scroll_viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.scroll_tabs);
        tabLayout.setupWithViewPager(viewPager);

        initializeDateTabs();

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                TextView month = (TextView) tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tab_month);
                month.setText(months.get(tab.getPosition()));

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
            tabLayout.getTabAt(i).setCustomView(R.layout.date_tab_view);
            TextView day = (TextView) tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tab_day);
            day.setText(days.get(i));
            day.setTextColor(Color.argb(FADED_ALPHA, 255, 255, 255));
        }
        TextView day = (TextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab_day);
        day.setTextColor(Color.argb(FULL_ALPHA, 255, 255, 255));

        TextView month = (TextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab_month);
        month.setText(months.get(0));

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_tabbed_event, menu);
//        return true;
//    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < days.size(); i++) {
            adapter.addFrag(AvailabilityInputFragment.newInstance(i), "");
        }
        viewPager.setAdapter(adapter);
    }

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

    private void getDummyData() {
        days = new ArrayList<String>();
        months = new ArrayList<String>();

        days.add("Tues 25");
        months.add("September");

        days.add("Wed 26");
        months.add("September");

        days.add("Thurs 27");
        months.add("September");

        days.add("Fri 28");
        months.add("September");

        days.add("Mon 1");
        months.add("October");

        days.add("Tues 2");
        months.add("October");

        days.add("Wed 3");
        months.add("October");

        days.add("Thurs 4");
        months.add("October");

        days.add("Mon 4");
        months.add("November");

    }

}
