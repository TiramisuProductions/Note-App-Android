package truelancer.noteapp.noteapp.Database;

import com.orm.SugarRecord;

/**
 * Created by sarveshpalav on 23/04/18.
 */

public class Task extends SugarRecord {

    String taskText;
    String noteId;
    public boolean isDone;
    String noteTimeStamp;

    public Task() {}

    public Task(String taskText, String noteId, boolean isDone, String noteTimeStamp) {
        this.taskText = taskText;
        this.noteId = noteId;
        this.isDone = isDone;
        this.noteTimeStamp = noteTimeStamp;
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getNoteTimeStamp() {
        return noteTimeStamp;
    }

    public void setNoteTimeStamp(String noteTimeStamp) {
        this.noteTimeStamp = noteTimeStamp;
    }
}