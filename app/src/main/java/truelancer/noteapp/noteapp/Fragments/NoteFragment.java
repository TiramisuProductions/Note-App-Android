package truelancer.noteapp.noteapp.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
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

import truelancer.noteapp.noteapp.Adapters.NoteAdapter;
import truelancer.noteapp.noteapp.AsyncTaskModel;
import truelancer.noteapp.noteapp.Database.Note;
import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;
import truelancer.noteapp.noteapp.Utils;


public class NoteFragment extends Fragment {
    public static RecyclerView mRecyclerView;
    private NoteAdapter mAdapter;
    public static RelativeLayout RNote_no_data;

    public NoteFragment() {/*required empty*/}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_last_note, container, false);

        RNote_no_data = (RelativeLayout) rootView.findViewById(R.id.Rlayout_no_data_note);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_note);
        mRecyclerView.setLayoutManager(mLayoutManager);


        AsyncTaskModel asyncTaskModel = new AsyncTaskModel(getActivity(), 4);
        asyncTaskModel.execute();

        if (!MyApp.defaultTheme) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.dark));
            rootView.setBackgroundColor(getResources().getColor(R.color.dark));
        }


      /*  List<Note> notes = Note.listAll(Note.class);
        Collections.reverse(notes);


        mAdapter = new NoteAdapter(getActivity(), notes);
        mRecyclerView.setAdapter(mAdapter);*/

        return rootView;
    }


    @Subscribe
    public void onEvent(EventB event) {
        // your implementation
        if (event.getMessage().equals("4")) {
            //mAdapter.notifyDataSetChanged();
            List<Note> notes = Note.listAll(Note.class);
            Collections.reverse(notes);
            if (notes.size() == 0) {
                Utils.Visibility_no_data(4, true);
            } else {
                Utils.Visibility_no_data(4, false);
            }
            mAdapter = new NoteAdapter(getActivity(), notes);
            mRecyclerView.setAdapter(mAdapter);
        }
        if (event.getMessage().equals("9")){
            List<Note> notes = null;

            if (MyApp.isIncomingFilterHighlighted) {
                notes = Note.findWithQuery(Note.class, "Select * from Note where incoming = ? and is_saved_from_app = ?", "1", "0");
            }
            if (MyApp.isOutgoingFilterHighlighted) {
                notes = Note.findWithQuery(Note.class, "Select * from Note where incoming = ? and is_saved_from_app = ?", "0");
            }
            if (MyApp.isSavedFromAppFilterHighlighted) {
                notes = Note.findWithQuery(Note.class, "Select * from Note where is_saved_from_app = ?", "1");
            }

            Collections.reverse(notes);
            if (notes.size() == 0) {
                Utils.Visibility_no_data(4, true);
            } else {
                Utils.Visibility_no_data(4, false);
            }
            mAdapter = new NoteAdapter(getActivity(), notes);
            mRecyclerView.setAdapter(mAdapter);
        }
    }


}