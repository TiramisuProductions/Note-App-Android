package truelancer.noteapp.noteapp;

import java.util.ArrayList;
import java.util.List;

import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.Database.Note;



public class Modelgson {
    List<Contact> clist;
    List<Email> elist;
    List<BankAccount> blist;
    List<Note> nlist;

    public Modelgson() {}

    public Modelgson(List<Contact> clist, List<Email> elist, List<BankAccount> blist, List<Note> nlist) {
        this.clist = clist;
        this.elist = elist;
        this.blist = blist;
        this.nlist = nlist;
    }

    public List<Contact> getClist() {
        return clist;
    }

    public void setClist(List<Contact> clist) {
        this.clist = clist;
    }

    public List<Email> getElist() {
        return elist;
    }

    public void setElist(List<Email> elist) {
        this.elist = elist;
    }

    public List<BankAccount> getBlist() {
        return blist;
    }

    public void setBlist(List<BankAccount> blist) {
        this.blist = blist;
    }

    public List<Note> getNlist() {
        return nlist;
    }

    public void setNlist(List<Note> nlist) {
        this.nlist = nlist;
    }
}
