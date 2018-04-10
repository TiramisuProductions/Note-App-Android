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

import truelancer.noteapp.noteapp.Adapters.BankAccountAdapter;
import truelancer.noteapp.noteapp.Adapters.ContactAdapter;
import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BankAccountFragment extends Fragment {
    private RecyclerView mRecycleView;
    private BankAccountAdapter mAdapter;


    public BankAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_bank_account, container, false);

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycleView = (RecyclerView) rootView.findViewById(R.id.recycler_bank);
        mRecycleView.setLayoutManager(mLayoutManager);

        List<BankAccount> bankAccounts = BankAccount.listAll(BankAccount.class);
        Collections.reverse(bankAccounts);

        mAdapter = new BankAccountAdapter(getActivity(), bankAccounts);
        mRecycleView.setAdapter(mAdapter);
        onResume();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        List<BankAccount> banks = BankAccount.listAll(BankAccount.class);
        Collections.reverse(banks);
        mAdapter = new BankAccountAdapter(getActivity(), banks);

        mRecycleView.setAdapter(mAdapter);
    }

    @Subscribe
    public void onEvent(EventB event){
        // your implementation
        if(event.getMessage().equals("3")){
            mAdapter.notifyDataSetChanged();
            List<BankAccount> banks = BankAccount.listAll(BankAccount.class);
            Collections.reverse(banks);
            mAdapter = new BankAccountAdapter(getActivity(), banks);

            mRecycleView.setAdapter(mAdapter);
        }
    }

}
