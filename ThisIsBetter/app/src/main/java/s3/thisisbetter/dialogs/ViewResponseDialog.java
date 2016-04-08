package s3.thisisbetter.dialogs;

import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Set;

import s3.thisisbetter.R;
import s3.thisisbetter.activities.TabbedEventActivity;
import s3.thisisbetter.fragments.DialogTabFragment;
import s3.thisisbetter.model.AvailabilityBlock;
import s3.thisisbetter.tabs.SlidingTabLayout;

public class ViewResponseDialog extends DialogFragment {

    private DialogSectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private AvailabilityBlock availabilityBlock;

    public static ViewResponseDialog newInstance(AvailabilityBlock availabilityBlock) {
        ViewResponseDialog dialogFragment = new ViewResponseDialog();
        dialogFragment.setAvailabilityBlock(availabilityBlock);

        return dialogFragment;
    }

    public void setAvailabilityBlock(AvailabilityBlock availabilityBlock) {
        this.availabilityBlock = availabilityBlock;
    }

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
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {

            Display display = dialog.getWindow().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x - 60;
            int height = size.y;

            if (!ViewConfiguration.get(getContext()).hasPermanentMenuKey())
                height -= 180;
            else
                height -= 120;


            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_view_response, container);

        // tab slider
        sectionsPagerAdapter = new DialogSectionsPagerAdapter(getChildFragmentManager(), availabilityBlock);

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager)view.findViewById(R.id.pager_tab_strip);
        viewPager.setAdapter(sectionsPagerAdapter);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setDistributeEvenly(true);

        TextView dialogDate = (TextView) view.findViewById(R.id.dialog_date);
        dialogDate.setText(availabilityBlock.getWeekday() + ", " + availabilityBlock.getMonthDay());

        TextView dialogTime = (TextView) view.findViewById(R.id.dialog_time);
        dialogTime.setText(availabilityBlock.getTimeRange());

        Button okButton = (Button) view.findViewById(R.id.dialog_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.dialog_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        return view;
    }

    private void dismissDialog() {
        dismiss();
    }


    public class DialogSectionsPagerAdapter extends FragmentPagerAdapter {

        private AvailabilityBlock availabilityBlock;

        public DialogSectionsPagerAdapter(FragmentManager fm, AvailabilityBlock availabilityBlock) {
            super(fm);
            this.availabilityBlock = availabilityBlock;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
            {
                // find first fragment...
                return DialogTabFragment.newInstance(availabilityBlock.getAvailableUserIds(), DialogTabFragment.AVAILABLE);
            }
            if (position == 1)
            {
                // find first fragment...
                return DialogTabFragment.newInstance(availabilityBlock.getNotAvailableUserIds(), DialogTabFragment.NOT_AVAILABLE);
            }
            else if (position == 2)
            {
                // find first fragment...
                return DialogTabFragment.newInstance(availabilityBlock.getNotRespondedUserIds(), DialogTabFragment.NOT_RESPONDED);
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
                    return "AVAILABLE (" + availabilityBlock.getAvailableUserIds().size() + ")";
                case 1:
                    return "UNAVAILABLE (" + availabilityBlock.getNotAvailableUserIds().size() + ")";
                case 2:
                    return "RESPONDING (" + availabilityBlock.getNotRespondedUserIds().size() + ")";
            }
            return null;
        }
    }

}