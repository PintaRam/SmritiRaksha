package com.smritiraksha;

import java.net.URL;

public class Constants {

   //root server url
    private static  String Root_URL = "https://e1b1-2401-4900-61cb-7b90-8d2e-13d7-33cd-ec52.ngrok-free.app/android/v1/";

    //registration
    public  static  String URL_REGISTER = Root_URL + "registerUser.php";

    //login
    public  static String login_url =  Root_URL + "login_user.php";
    public  static String URL_REGISTER_GUIDE =  Root_URL + "guideRegistration.php";
  public  static String URL_REGISTER_PATIENTS =  Root_URL + "patientRegistration.php";
    public  static String get_Guide =  Root_URL + "get_guides.php";
    public static  String FETCH_PATIENT_URL = Root_URL+"fetch_patient_data.php";
}
