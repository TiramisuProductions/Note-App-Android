package truelancer.noteapp.noteapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import truelancer.noteapp.noteapp.Adapters.ContactAdapter;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {


    public static RecyclerView mRecyclerView;
    private ContactAdapter mAdapter;

    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

       /* if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }*/
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_contact);
        mRecyclerView.setLayoutManager(mLayoutManager);

        AsyncTaskModel asyncTaskModel = new AsyncTaskModel(getActivity(),1);
        asyncTaskModel.execute();

        if(!MyApp.defaultTheme){
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.dark));
            rootView.setBackgroundColor(getResources().getColor(R.color.dark));
        }


       /*List<Contact> contacts = Contact.listAll(Contact.class);
        Collections.reverse(contacts);

        mAdapter = new ContactAdapter(getActivity(), contacts);
        mRecyclerView.setAdapter(mAdapter);*/


        onResume();
        return rootView;
    }

}
