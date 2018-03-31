package truelancer.noteapp.noteapp.Database;

import com.orm.SugarRecord;

/**
 * Created by Sarvesh Palav on 1/4/2018.
 */

public class BankAccount extends SugarRecord {

    String name;
    String accountNo;
    String ifscCode;
    String calledNumber;
    String calledName;
    boolean incoming;
    String tsMilli;
    boolean savedFromApp;

    public BankAccount() {
    }

    public BankAccount(String name, String accountNo, String ifscCode, String tsMilli, boolean savedFromApp) {
        this.name = name;
        this.accountNo = accountNo;
        this.ifscCode = ifscCode;
        this.tsMilli = tsMilli;
        this.savedFromApp = savedFromApp;
    }

    public BankAccount(String name, String accountNo, String ifscCode, String calledNumber, String calledName, boolean incoming, String tsMilli) {
        this.name = name;
        this.accountNo = accountNo;
        this.ifscCode = ifscCode;
        this.calledNumber = calledNumber;
        this.calledName = calledName;
        this.incoming = incoming;
        this.tsMilli = tsMilli;
    }

    public boolean isSavedFromApp() {
        return savedFromApp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
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
