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
    boolean isBackedUp;

    public void setSavedFromApp(boolean savedFromApp) {
        isSavedFromApp = savedFromApp;
    }

    public boolean isBackedUp() {
        return isBackedUp;
    }

    public void setBackedUp(boolean backedUp) {
        isBackedUp = backedUp;
    }

    public Contact(String name, String phoneno, String calledNumber, String calledName, boolean incoming, String tsMilli,boolean isBackedUp) {
        this.name = name;
        this.phoneno = phoneno;
        this.calledNumber = calledNumber;
        this.calledName = calledName;
        this.incoming = incoming;
        this.tsMilli = tsMilli;
        this.isBackedUp = isBackedUp;
    }

    public Contact(String name, String phoneno, String calledNumber, String calledName, boolean incoming, String tsMilli) {
        this.name = name;
        this.phoneno = phoneno;
        this.calledNumber = calledNumber;
        this.calledName = calledName;
        this.incoming = incoming;
        this.tsMilli = tsMilli;
        this.isBackedUp = isBackedUp;
    }

    public boolean isSavedFromApp() {
        return isSavedFromApp;
    }

    public Contact(String name, String phoneno, String tsMilli, boolean isSavedFromApp,boolean isBackedUp) {
        this.name = name;
        this.phoneno = phoneno;
        this.tsMilli = tsMilli;
        this.isSavedFromApp = isSavedFromApp;
        this.isBackedUp = isBackedUp;
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
