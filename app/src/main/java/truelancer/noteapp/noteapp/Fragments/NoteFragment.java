package truelancer.noteapp.noteapp.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import truelancer.noteapp.noteapp.Adapters.NoteAdapter;
import truelancer.noteapp.noteapp.Adapters.noteDoneAdapter;
import truelancer.noteapp.noteapp.Database.Note;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;


public class NoteFragment extends Fragment {
    private RecyclerView mRecyclerView, mRecyclerView2;
    private NoteAdapter mAdapter;
    private noteDoneAdapter mAdapter2;
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

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());




        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_note);
        mRecyclerView2 = (RecyclerView) rootView.findViewById(R.id.recycler_note2);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView2.setLayoutManager(mLayoutManager2);

        if(!MyApp.defaultTheme){
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.dark));
            mRecyclerView2.setBackgroundColor(getResources().getColor(R.color.dark));
            rootView.setBackgroundColor(getResources().getColor(R.color.dark));
        }

        List<Note> notes = Note.listAll(Note.class);
        Collections.reverse(notes);


        mAdapter = new NoteAdapter(getActivity(), notes);
        mRecyclerView.setAdapter(mAdapter);
        onResume();
        return rootView;
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

    public void getNote() {

    }
}