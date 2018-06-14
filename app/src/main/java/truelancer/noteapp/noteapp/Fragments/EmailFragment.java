package truelancer.noteapp.noteapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;

import truelancer.noteapp.noteapp.Adapters.EmailAdapter;
import truelancer.noteapp.noteapp.AsyncTaskModel;
import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailFragment extends Fragment {
    public static RecyclerView mRecyclerView;
    private EmailAdapter mAdapter;


    public EmailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_email, container, false);

        // getActivity().setTheme(R.style.MyMaterialThemeDark);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_email);
        mRecyclerView.setLayoutManager(mLayoutManager);

        AsyncTaskModel asyncTaskModel = new AsyncTaskModel(getActivity(), 2);
        asyncTaskModel.execute();

        if (!MyApp.defaultTheme) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.dark));
            rootView.setBackgroundColor(getResources().getColor(R.color.dark));
        }

   /*     List<Email> emails = Email.listAll(Email.class);
        Collections.reverse(emails);*/

        //Toast.makeText(getContext(),"emails coming! "+emails.size(),Toast.LENGTH_LONG).show();

      /*  mAdapter = new EmailAdapter(getActivity(), emails);
        mRecyclerView.setAdapter(mAdapter);*/

        onResume();
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Subscribe
    public void onEvent(EventB event) {
        // your implementation
        if (event.getMessage().equals("2")) {
            // mAdapter.notifyDataSetChanged();
            List<Email> emails = Email.listAll(Email.class);
            Collections.reverse(emails);
            mAdapter = new EmailAdapter(getActivity(), emails);

            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
