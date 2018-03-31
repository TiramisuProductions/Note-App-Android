package truelancer.noteapp.noteapp;

import java.util.regex.Pattern;

public class Utils {


    public static boolean isValidMobile(String phone){
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 6 || phone.length() > 13) {
                //for only indian ic_phone no
                //if(ic_phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }
}
