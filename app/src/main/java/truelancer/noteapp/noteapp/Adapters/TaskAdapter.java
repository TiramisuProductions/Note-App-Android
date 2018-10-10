package truelancer.noteapp.noteapp.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import truelancer.noteapp.noteapp.Database.Task;
import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyView> {
    List<Task> tasks;
    Context context;

    public TaskAdapter(Context context, List<Task> tasks) {

        this.context = context;
        this.tasks = tasks;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(MyView holder, final int position) {
        final Task task = tasks.get(position);

        if (MyApp.nightMode) {
           // holder.noteCardView.setBackgroundColor(context.getResources().getColor(R.color.darker_card));
            holder.noteCardView.setCardBackgroundColor(context.getResources().getColor(R.color.darker_card));
            holder.textViewTask.setTextColor(context.getResources().getColor(R.color.white));
        }

        if (!task.isDone) {
            holder.checkBoxText.setChecked(false);
            holder.textViewTask.setText(tasks.get(position).getTaskText());

        } else {
            holder.checkBoxText.setChecked(true);
            holder.textViewTask.setText(tasks.get(position).getTaskText());
            holder.textViewTask.setPaintFlags(holder.textViewTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task taskDelete = Task.findById(Task.class, task.getId());
                taskDelete.delete();
                EventBus.getDefault().post(new EventB("updateTask"));
            }
        });


        holder.checkBoxText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    Task task = Task.findById(Task.class, tasks.get(position).getId());
                    task.isDone = false;
                    task.save();
                    EventBus.getDefault().post(new EventB("updateTask"));

                } else {
                    Task task = Task.findById(Task.class, tasks.get(position).getId());
                    task.isDone = true;
                    task.save();
                    EventBus.getDefault().post(new EventB("updateTask"));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("done", "" + tasks.size());
        return tasks.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        CheckBox checkBoxText;
        TextView textViewTask;
        ImageView deleteTask;
        CardView noteCardView;

        public MyView(View itemView) {

            super(itemView);
            checkBoxText = (CheckBox) itemView.findViewById(R.id.checkBoxTask);
            textViewTask = (TextView) itemView.findViewById(R.id.taskText);
            deleteTask = (ImageView) itemView.findViewById(R.id.imageButtonDelete);
            noteCardView = (CardView) itemView.findViewById(R.id.cardView_task);
        }
    }
}
