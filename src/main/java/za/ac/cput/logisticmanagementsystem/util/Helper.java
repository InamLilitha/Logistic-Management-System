/* 27/07/2026
 *Helper.java
 *Helper model class in Util folder
 *Maghdie Petersen 230600204
 *  */

package za.ac.cput.logisticmanagementsystem.util;

import java.util.UUID;
import java.util.regex.Pattern;

public class Helper {

    //Regex pattern for validating email format (standard user@domain.com)
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9+_.-]+$";

    //Regex pattern for South African phone Numbers starting with 0 or +27 followed by 9 digits
    private static final String PHONE_REGEX = "^(\\+27|0)[0-9]{9}$";

    //Checks if string is completely null or contains only white spaces
  public static boolean isNullOrEmpty(String str){
      return str == null || str.trim().isEmpty();
  }

    //Generates a universally unique 36-character random string for primary keys
    public static String generateId(){
        return UUID.randomUUID().toString();
    }

    //Compares the email input against our regular expression pattern
    public static boolean isEmailValid(String email){
      if (isNullOrEmpty(email)) return false;
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }

    //Compare the phone number input against our regex pattern
    public static boolean isValidPhoneNumber(String phoneNumber){
        if (isNullOrEmpty(phoneNumber)) return false;
        return Pattern.compile(PHONE_REGEX).matcher(phoneNumber).matches();
    }

}
