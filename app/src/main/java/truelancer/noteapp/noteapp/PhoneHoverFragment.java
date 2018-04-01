package truelancer.noteapp.noteapp;

import android.media.MediaPlayer;
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
    String pathZ = null;
    String TAG = "pizza";
    Button callRecord,stopRecord;

    MediaRecorder mRecorder = null;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hoverphone, container, false);


        stopRecord = (Button) view.findViewById(R.id.callRecordStopButton);

        Toast.makeText(getActivity(), "sd", Toast.LENGTH_LONG).show();
        // identifier.setText(String.valueOf(getArguments().getInt("id")));
        Log.d("wolla", "wolla");
        makeDirectory();
        SmartTabLayout viewPagerTab = (SmartTabLayout) getActivity().findViewById(R.id.viewPagerTab);


        stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecorder.stop();
                Toast.makeText(getActivity(),"stop",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void startRecording(MediaRecorder mRecorder) {
        String filename = "" + System.currentTimeMillis()+".amr";

        Log.d("pizza", "filename: " + pathZ + filename);


        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(pathZ + filename);

        try {
            mRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("pizza", "prepare failed");
        }
        mRecorder.start();

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

    private void makeDirectory() {
        pathZ = Environment.getExternalStorageDirectory() + File.separator + "HelloNote";
        File dir = new File(pathZ);

        if (dir.exists() && dir.isDirectory()) {
            Log.d("pizza", "writeExt: Exists");
        } else {
            Log.d("pizza", "writeExt: Not exists");
            Log.d("pizza", "Absolute " + pathZ);

            dir.mkdir();
        }
    }
}
