package truelancer.noteapp.noteapp.Database;

import com.orm.SugarRecord;

/**
 * Created by Siddhant Naique on 09-04-2018.
 */

public class CallRecording extends SugarRecord {
    String RecordName;
    String RecordPath;
    String calledNumber;
    String calledName;
    boolean incoming;
    String tsMilli;

    public CallRecording() {
    }

    public CallRecording(String recordName, String recordPath, String calledNumber, String calledName, boolean incoming, String tsMilli) {
        RecordName = recordName;
        RecordPath = recordPath;
        this.calledNumber = calledNumber;
        this.calledName = calledName;
        this.incoming = incoming;
        this.tsMilli = tsMilli;
    }

    public String getRecordName() {
        return RecordName;
    }

    public void setRecordName(String recordName) {
        RecordName = recordName;
    }

    public String getRecordPath() {
        return RecordPath;
    }

    public void setRecordPath(String recordPath) {
        RecordPath = recordPath;
    }

    public String getCalledNumber() {
        return calledNumber;
    }

    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber;
    }

    public String getCalledName() {
        return calledName;
    }

    public void setCalledName(String calledName) {
        this.calledName = calledName;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    public String getTsMilli() {
        return tsMilli;
    }

    public void setTsMilli(String tsMilli) {
        this.tsMilli = tsMilli;
    }
}
