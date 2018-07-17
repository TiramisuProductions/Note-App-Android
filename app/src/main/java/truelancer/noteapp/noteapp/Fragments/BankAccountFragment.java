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
import truelancer.noteapp.noteapp.Adapters.ContactAdapter;
import truelancer.noteapp.noteapp.AsyncTaskModel;
import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;
import truelancer.noteapp.noteapp.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BankAccountFragment extends Fragment {

    public static RecyclerView mRecyclerView;
    private BankAccountAdapter mAdapter;
    public static RelativeLayout RBank_no_data;
    public View rootView;

    public BankAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         rootView = inflater.inflate(R.layout.fragment_bank_account, container, false);


        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        RBank_no_data = (RelativeLayout) rootView.findViewById(R.id.Rlayout_no_data_bank);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_bank);
        mRecyclerView.setLayoutManager(mLayoutManager);

        AsyncTaskModel asyncTaskModel = new AsyncTaskModel(getActivity(), 3);
        asyncTaskModel.execute();


        if (MyApp.nightMode) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.dark));
            rootView.setBackgroundColor(getResources().getColor(R.color.dark));
        }

      /*  List<BankAccount> bankAccounts = BankAccount.listAll(BankAccount.class);
        Collections.reverse(bankAccounts);

        mAdapter = new BankAccountAdapter(getActivity(), bankAccounts);
        mRecycleView.setAdapter(mAdapter);
        onResume();*/

        return rootView;
    }


    @Subscribe
    public void onEvent(EventB event) {
        // your implementation
        if (event.getMessage().equals("3")) {
            //mAdapter.notifyDataSetChanged();
            List<BankAccount> banks = BankAccount.listAll(BankAccount.class);
            Collections.reverse(banks);
            if (banks.size() == 0) {
                Utils.Visibility_no_data(3, true);
            } else {
                Utils.Visibility_no_data(3, false);
            }
            mAdapter = new BankAccountAdapter(getActivity(), banks);

            mRecyclerView.setAdapter(mAdapter);
        }

        if (event.getMessage().equals("changeUIMode")) {
            Log.d("works here", "works here 1");
            if (MyApp.nightMode) {
                Log.d("works here", "works here 2");
                mRecyclerView.setBackgroundColor(getResources().getColor(R.color.dark));
                rootView.setBackgroundColor(getResources().getColor(R.color.dark));


            } else {
                mRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
                rootView.setBackgroundColor(getResources().getColor(R.color.white));
            }

            List<BankAccount> banks = BankAccount.listAll(BankAccount.class);
            if (banks.size() == 0) {
                Utils.Visibility_no_data(3, true);
            } else {
                Utils.Visibility_no_data(3, false);
            }

            Collections.reverse(banks);
            mAdapter = new BankAccountAdapter(getActivity(), banks);
            mRecyclerView.setAdapter(mAdapter);
        }

    }
}
