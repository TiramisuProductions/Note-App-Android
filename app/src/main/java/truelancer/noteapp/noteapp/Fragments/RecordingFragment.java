package truelancer.noteapp.noteapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import truelancer.noteapp.noteapp.Database.CallRecording;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;
import truelancer.noteapp.noteapp.Adapters.RecordingAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordingFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecordingAdapter mAdapter;
    String TAG = "yellow";

    public RecordingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recording, container, false);



        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_recording);
        mRecyclerView.setLayoutManager(mLayoutManager);


        if(!MyApp.defaultTheme){
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.dark));
            rootView.setBackgroundColor(getResources().getColor(R.color.dark));
        }
        List<CallRecording> callRecordings = CallRecording.listAll(CallRecording.class);
        Collections.reverse(callRecordings);

        Log.d(TAG, ""+callRecordings.size());

        mAdapter = new RecordingAdapter(getActivity(), callRecordings);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        List<CallRecording> callRecordings = CallRecording.listAll(CallRecording.class);
        Collections.reverse(callRecordings);

        mAdapter = new RecordingAdapter(getActivity(), callRecordings);
        mRecyclerView.setAdapter(mAdapter);
    }
}
