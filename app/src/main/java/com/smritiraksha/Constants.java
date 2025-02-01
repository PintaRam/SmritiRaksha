package com.smritiraksha;

import java.net.URL;

public class Constants {

   //root server url
    private static  String Root_URL = " https://bc9f-2401-4900-22b4-a8c9-c49a-7aa8-aaa8-d755.ngrok-free.app/android/v1/";

    //registration
    public  static  String URL_REGISTER = Root_URL + "registerUser.php";

    //login
    public  static String login_url =  Root_URL + "login_user.php";
    public  static String URL_REGISTER_GUIDE =  Root_URL + "guideRegistration.php";
  public  static String URL_REGISTER_PATIENTS =  Root_URL + "patientRegistration.php";
    public  static String get_Guide =  Root_URL + "get_guides.php";
    public static  String FETCH_PATIENT_URL = Root_URL+"fetch_patient_data.php";


    public static String Remainder_URL=Root_URL+"Send_medicine.php";
}
