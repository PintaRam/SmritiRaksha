package com.smritiraksha;

import java.net.URL;

public class Constants {

   //root server url

    private static  String Root_URL = "https://0786-2406-7400-94-75a8-bc8c-b227-bfb8-10b7.ngrok-free.app/android/v1/";

    //registration
    public  static  String URL_REGISTER = Root_URL + "registerUser.php";

    //login
    public  static String login_url =  Root_URL + "login_user.php";
    public  static String URL_REGISTER_GUIDE =  Root_URL + "guideRegistration.php";
  public  static String URL_REGISTER_PATIENTS =  Root_URL + "patientRegistration.php";
    public  static String get_Guide =  Root_URL + "get_guides.php";
    public static  String FETCH_PATIENT_URL = Root_URL+"fetch_patient_data.php";



    public static String Remainder_URL=Root_URL+"Send_medicine.php";
    public  static  String getPatients = Root_URL + "getPatients.php";
    public static  String FETCH_PATIENT_GUIDES_URL = Root_URL+"fetch_patient_guide.php";
    public static  String CHECK_EMERGENCY = Root_URL+"checkEmergency.php";
    public static  String TRIGGER_EMERGENCY= Root_URL+"triggerEmergencyAlert.php";

    public static String Doc_Register=Root_URL+"DoctorRegistration.php";

    public static String All_Patinets=Root_URL+"AllPatinets.php";

}
