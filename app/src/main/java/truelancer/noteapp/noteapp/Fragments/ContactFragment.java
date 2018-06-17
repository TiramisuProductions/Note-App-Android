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

import truelancer.noteapp.noteapp.Adapters.ContactAdapter;
import truelancer.noteapp.noteapp.AsyncTaskModel;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;
import truelancer.noteapp.noteapp.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    public static RecyclerView mRecyclerView;
    private ContactAdapter mAdapter;
    public static RelativeLayout RContact_no_data;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        RContact_no_data = (RelativeLayout) rootView.findViewById(R.id.Rlayout_no_data_contact);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_contact);
        mRecyclerView.setLayoutManager(mLayoutManager);

        AsyncTaskModel asyncTaskModel = new AsyncTaskModel(getActivity(), 1);
        asyncTaskModel.execute();

        if (!MyApp.defaultTheme) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.dark));
            rootView.setBackgroundColor(getResources().getColor(R.color.dark));
        }
        /*List<Contact> contacts = Contact.listAll(Contact.class);
        Collections.reverse(contacts);

        mAdapter = new ContactAdapter(getActivity(), contacts);
        mRecyclerView.setAdapter(mAdapter);*/

        /*onResume();*/
        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void onEvent(EventB event) {
        // your implementation
        if (event.getMessage().equals("1")) {
            Log.d("wood", "KIdde: ");
            //mAdapter.notifyDataSetChanged();
            List<Contact> contacts = Contact.listAll(Contact.class);
            if (contacts.size() == 0) {
                Utils.Visibility_no_data(1, true);
            } else {
                Utils.Visibility_no_data(1, false);
            }

            Collections.reverse(contacts);
            mAdapter = new ContactAdapter(getActivity(), contacts);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
