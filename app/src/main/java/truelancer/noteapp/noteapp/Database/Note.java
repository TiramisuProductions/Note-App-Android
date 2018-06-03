package truelancer.noteapp.noteapp.Database;

import com.orm.SugarRecord;



public class Note extends SugarRecord {
    String note;
    String calledName;
    String calledNumber;
    String tsMilli;
    boolean incoming;
    boolean isSavedFromApp;
    public boolean isDone;

    public Note(){}

    public Note(String note,String calledName,String calledNumber,String tsMilli,boolean incoming,boolean isDone){
        this.note=note;
        this.calledName=calledName;
        this.calledNumber=calledNumber;
        this.tsMilli=tsMilli;
        this.incoming=incoming;
        this.isDone=isDone;

    }

    public Note(String note, String tsMilli, boolean isSavedFromApp) {
        this.note = note;
        this.tsMilli = tsMilli;
        this.isSavedFromApp = isSavedFromApp;
    }


    public boolean isSavedFromApp() {return isSavedFromApp;}

    public void setSavedFromApp(boolean savedFromApp) {isSavedFromApp = savedFromApp;}

    public String getNote() {return note;}

    public void setNote(String note) {this.note = note;}

    public String getCalledNumber() {return calledNumber;}

    public void setCalledNumber(String calledNumber) {this.calledNumber = calledNumber;}

    public String getCalledName() {return calledName;}

    public void setCalledName(String calledName) {this.calledName = calledName;}

    public boolean isIncoming() {return incoming;}

    public void setIncoming(boolean incoming) {this.incoming = incoming;}

    public String getTsMilli() {return tsMilli;}

    public void setTsMilli(String tsMilli) {this.tsMilli = tsMilli;}

    public boolean isDone() {return isDone;}

    public void setDone(boolean done) {isDone = done;}

}