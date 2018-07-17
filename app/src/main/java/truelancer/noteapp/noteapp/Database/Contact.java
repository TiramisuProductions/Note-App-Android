package truelancer.noteapp.noteapp.Database;

import com.orm.SugarRecord;


public class Contact extends SugarRecord {
    String name;
    String phoneno;
    String calledNumber;
    String calledName;
    boolean incoming;
    String tsMilli;
    boolean isSavedFromApp;

    public Contact(String name, String phoneno, String calledNumber, String calledName, boolean incoming, String tsMilli) {
        this.name = name;
        this.phoneno = phoneno;
        this.calledNumber = calledNumber;
        this.calledName = calledName;
        this.incoming = incoming;
        this.tsMilli = tsMilli;
    }

    public boolean isSavedFromApp() {
        return isSavedFromApp;
    }

    public Contact(String name, String phoneno, String tsMilli, boolean isSavedFromApp) {
        this.name = name;
        this.phoneno = phoneno;
        this.tsMilli = tsMilli;
        this.isSavedFromApp = isSavedFromApp;
    }

    public Contact() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
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
