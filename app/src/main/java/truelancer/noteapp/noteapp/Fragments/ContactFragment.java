package truelancer.noteapp.noteapp.Fragments;


import android.content.Context;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import truelancer.noteapp.noteapp.Adapters.contactAdapter;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private contactAdapter mAdapter;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

            EventBus.getDefault().register(this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_contact);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<Contact> contacts = Contact.listAll(Contact.class);
        Collections.reverse(contacts);

        mAdapter = new contactAdapter(getActivity(), contacts);
        mRecyclerView.setAdapter(mAdapter);

        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            Log.d("logitech", "" + contact.isIncoming());
        }
        onResume();
        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        List<Contact> contacts = Contact.listAll(Contact.class);
        Collections.reverse(contacts);
        mAdapter = new contactAdapter(getActivity(), contacts);
        mRecyclerView.setAdapter(mAdapter);
    }
@Subscribe
    public void onEvent(EventB event){
        // your implementation
       if(event.getMessage().equals("1")){
           mAdapter.notifyDataSetChanged();
           List<Contact> contacts = Contact.listAll(Contact.class);
           Collections.reverse(contacts);
           mAdapter = new contactAdapter(getActivity(), contacts);
           mRecyclerView.setAdapter(mAdapter);
       }
    }
}
