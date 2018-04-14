package truelancer.noteapp.noteapp;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sarvesh Palav on 12/27/2017.
 */

public class PhoneHoverFragment extends Fragment {

    String TAG = "pizza";



    public static PhoneHoverFragment newInstance(int identifier) {
        PhoneHoverFragment testFragment = new PhoneHoverFragment();
        Log.d("wolla", "wolla");
        Bundle bundle = new Bundle();
        bundle.putInt("id", identifier);
        testFragment.setArguments(bundle);
        return testFragment;
    }

    public PhoneHoverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hoverphone, container, false);
        // identifier.setText(String.valueOf(getArguments().getInt("id")));
        Log.d("wolla", "wolla");
        SmartTabLayout viewPagerTab = (SmartTabLayout) getActivity().findViewById(R.id.viewPagerTab);

        return view;
    }




    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

}
