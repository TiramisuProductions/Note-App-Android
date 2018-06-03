package truelancer.noteapp.noteapp.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import truelancer.noteapp.noteapp.Adapters.EmailAdapter;
import truelancer.noteapp.noteapp.Adapters.NoteAdapter;
import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.Database.Note;
import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.R;


public class NoteFragment extends Fragment {
    private RecyclerView mRecyclerView, mRecyclerView2;
    private NoteAdapter mAdapter;
    private ArrayList<Note> noteDoneList = new ArrayList<>();
    private ArrayList<Note> noteNotDoneList = new ArrayList<>();

    public NoteFragment() {/*required empty*/}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_last_note, container, false);


        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_note);

        mRecyclerView.setLayoutManager(mLayoutManager);


        List<Note> notes = Note.listAll(Note.class);
        Collections.reverse(notes);


        mAdapter = new NoteAdapter(getActivity(), notes);
        mRecyclerView.setAdapter(mAdapter);
        onResume();
        return rootView;
    }


    @Subscribe
    public void onEvent(EventB event){
        // your implementation
        if(event.getMessage().equals("4")){
            mAdapter.notifyDataSetChanged();
            List<Note> emails = Note.listAll(Note.class);
            Collections.reverse(emails);
            mAdapter = new NoteAdapter(getActivity(), emails);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        List<Note> emails = Note.listAll(Note.class);
        Collections.reverse(emails);
        mAdapter = new NoteAdapter(getActivity(), emails);
        mRecyclerView.setAdapter(mAdapter);
    }
}