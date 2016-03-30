package s3.thisisbetter.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import s3.thisisbetter.R;
import s3.thisisbetter.activities.TabbedEventActivity;
import s3.thisisbetter.fragments.DialogTabFragment;
import s3.thisisbetter.tabs.SlidingTabLayout;

public class ViewResponseDialog extends DialogFragment {

    private DialogSectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_view_response, container);

        // tab slider
        sectionsPagerAdapter = new DialogSectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager)view.findViewById(R.id.pager_tab_strip);
        viewPager.setAdapter(sectionsPagerAdapter);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setDistributeEvenly(true);

        return view;
    }


    public class DialogSectionsPagerAdapter extends FragmentPagerAdapter
    {

        public DialogSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
            {
                // find first fragment...
                DialogTabFragment ft1 = new DialogTabFragment();
                return ft1;
            }
            if (position == 1)
            {
                // find first fragment...
                DialogTabFragment ft2 = new DialogTabFragment();
                return ft2;
            }
            else if (position == 2)
            {
                // find first fragment...
                DialogTabFragment ft3 = new DialogTabFragment();
                return ft3;
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "AVAILABLE";
                case 1:
                    return "UNAVAILABLE";
                case 2:
                    return "NOT RESPONDED";
            }
            return null;
        }
    }

}