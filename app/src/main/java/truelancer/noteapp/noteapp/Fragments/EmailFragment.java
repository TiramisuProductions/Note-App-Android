package truelancer.noteapp.noteapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
import truelancer.noteapp.noteapp.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailFragment extends Fragment {
    public static RecyclerView mRecyclerView;
    private EmailAdapter mAdapter;
    public static RelativeLayout REmail_no_data;


    public EmailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_email, container, false);
        REmail_no_data = (RelativeLayout) rootView.findViewById(R.id.Rlayout_no_data_email);
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
            List<Email> emails = Email.listAll(Email.class);
            Collections.reverse(emails);
            if (emails.size() == 0) {
                Utils.Visibility_no_data(2, true);
            } else {
                Utils.Visibility_no_data(2, false);
            }
            mAdapter = new EmailAdapter(getActivity(), emails);
            mRecyclerView.setAdapter(mAdapter);
        }
        if (event.getMessage().equals("7")) {
            List<Email> emails = null;
            if (MyApp.isIncomingFilterHighlighted) {
                emails = Email.findWithQuery(Email.class, "Select * from Email where incoming = ? and is_saved_from_app = ?", "1", "0");
            }
            if (MyApp.isOutgoingFilterHighlighted) {
                emails = Email.findWithQuery(Email.class, "Select * from Email where incoming = ? and is_saved_from_app = ?", "0", "0");
            }
            if (MyApp.isSavedFromAppFilterHighlighted) {
                emails = Email.findWithQuery(Email.class, "Select * from Email where is_saved_from_app = ?", "1");
            }
            Collections.reverse(emails);
            if (emails.size() == 0) {
                Utils.Visibility_no_data(2, true);
            } else {
                Utils.Visibility_no_data(2, false);
            }
            mAdapter = new EmailAdapter(getActivity(), emails);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
