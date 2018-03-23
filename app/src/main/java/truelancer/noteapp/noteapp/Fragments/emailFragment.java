package truelancer.noteapp.noteapp.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import truelancer.noteapp.noteapp.Adapters.contactAdapter;
import truelancer.noteapp.noteapp.Adapters.emailAdapter;
import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.R;


import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class emailFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private emailAdapter mAdapter;


    public emailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_email, container, false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_email);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<Email> emails = Email.listAll(Email.class);
        Collections.reverse(emails);

        //Toast.makeText(getContext(),"emails coming! "+emails.size(),Toast.LENGTH_LONG).show();

        mAdapter = new emailAdapter(getActivity(), emails);
        mRecyclerView.setAdapter(mAdapter);
        onResume();
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        List<Email> emails = Email.listAll(Email.class);
        Collections.reverse(emails);
        mAdapter = new emailAdapter(getActivity(), emails);

        mRecyclerView.setAdapter(mAdapter);
    }
}
