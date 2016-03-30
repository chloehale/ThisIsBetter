package s3.thisisbetter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import s3.thisisbetter.R;

/**
 * Created by steve on 3/29/16.
 */
public class DialogTabFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_dialog_tab, container, false);
    }
}
