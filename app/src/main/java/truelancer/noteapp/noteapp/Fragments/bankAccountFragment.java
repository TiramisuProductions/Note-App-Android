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

import java.util.Collections;
import java.util.List;

import truelancer.noteapp.noteapp.Adapters.bankAccountAdapter;
import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.R;


import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class bankAccountFragment extends Fragment {
    private RecyclerView mRecycleView;
    private bankAccountAdapter mAdapter;


    public bankAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_bank_account, container, false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycleView = (RecyclerView) rootView.findViewById(R.id.recycler_bank);
        mRecycleView.setLayoutManager(mLayoutManager);

        List<BankAccount> bankAccounts = BankAccount.listAll(BankAccount.class);
        Collections.reverse(bankAccounts);

        mAdapter = new bankAccountAdapter(getActivity(), bankAccounts);
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
        mAdapter = new bankAccountAdapter(getActivity(), banks);

        mRecycleView.setAdapter(mAdapter);
    }

}
