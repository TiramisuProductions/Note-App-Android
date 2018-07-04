package truelancer.noteapp.noteapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;

import truelancer.noteapp.noteapp.Adapters.BankAccountAdapter;
import truelancer.noteapp.noteapp.Adapters.RecordingAdapter;
import truelancer.noteapp.noteapp.AsyncTaskModel;
import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.CallRecording;
import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;
import truelancer.noteapp.noteapp.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordingFragment extends Fragment {

    public static RecyclerView mRecyclerView;
    private RecordingAdapter mAdapter;
    public static RelativeLayout RRecord_no_data;
    private View rootView;

    public RecordingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         rootView = inflater.inflate(R.layout.fragment_recording, container, false);

        RRecord_no_data = (RelativeLayout)rootView.findViewById(R.id.Rlayout_no_data_record);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_recording);
        mRecyclerView.setLayoutManager(mLayoutManager);

        AsyncTaskModel asyncTaskModel = new AsyncTaskModel(getActivity(), 5);
        asyncTaskModel.execute();


        if (!MyApp.defaultTheme) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.dark));
            rootView.setBackgroundColor(getResources().getColor(R.color.dark));
        }

       /* List<CallRecording> callRecordings = CallRecording.listAll(CallRecording.class);
        Collections.reverse(callRecordings);

        Log.d(TAG, ""+callRecordings.size());

        mAdapter = new RecordingAdapter(getActivity(), callRecordings);
        mRecyclerView.setAdapter(mAdapter);*/

        return rootView;

    }

    @Subscribe
    public void onEvent(EventB event) {
        if (event.getMessage().equals("5")) {
            //mAdapter.notifyDataSetChanged();
            List<CallRecording> callRecordings = CallRecording.listAll(CallRecording.class);
            Collections.reverse(callRecordings);
            if (callRecordings.size() == 0) {
                Utils.Visibility_no_data(5, true);
            } else {
                Utils.Visibility_no_data(5, false);
            }
            mAdapter = new RecordingAdapter(getActivity(), callRecordings);
            mRecyclerView.setAdapter(mAdapter);
        }

        if (event.getMessage().equals("changeUIMode")) {
            Log.d("works here", "works here 1");
            if (!MyApp.defaultTheme) {
                Log.d("works here", "works here 2");
                mRecyclerView.setBackgroundColor(getResources().getColor(R.color.dark));
                rootView.setBackgroundColor(getResources().getColor(R.color.dark));
            } else {
                mRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
                rootView.setBackgroundColor(getResources().getColor(R.color.white));
            }

            List<CallRecording> records = CallRecording.listAll(CallRecording.class);
            if (records.size() == 0) {
                Utils.Visibility_no_data(1, true);
            } else {
                Utils.Visibility_no_data(1, false);
            }

            Collections.reverse(records);
            mAdapter = new RecordingAdapter(getActivity(), records);
            mRecyclerView.setAdapter(mAdapter);
        }


    }


}
