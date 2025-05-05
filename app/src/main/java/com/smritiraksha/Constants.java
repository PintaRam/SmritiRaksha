package com.smritiraksha;

import java.net.URL;

public class Constants {

    //root server url


    private static  String Root_URL = "https://d027-2406-7400-94-c18f-9dff-bae-f9b5-cb28.ngrok-free.app/android/v1/";


    //registration
    public  static  String URL_REGISTER = Root_URL + "registerUser.php";

    //login
    public  static String login_url =  Root_URL + "login_user.php";
    public  static String URL_REGISTER_GUIDE =  Root_URL + "guideRegistration.php";
    public  static String URL_REGISTER_PATIENTS =  Root_URL + "patientRegistration.php";
    public  static String get_Guide =  Root_URL + "get_guides.php";
    public static  String FETCH_PATIENT_URL = Root_URL+"fetch_patient_data.php";

    public static String  SAVE_PRESCRIPTION=Root_URL+"insert_prescription.php";
    public static String Remainder_URL=Root_URL+"Send_medicine.php";
    public  static  String getPatients = Root_URL + "getPatients.php";
    public static  String FETCH_PATIENT_GUIDES_URL = Root_URL+"fetch_patient_guide.php";
    public static  String CHECK_EMERGENCY = Root_URL+"checkEmergency.php";
    public static  String TRIGGER_EMERGENCY= Root_URL+"triggerEmergencyAlert.php";
    public static  String Patient_location= Root_URL+"updatePatientLocation.php";
    public static  String Get_Patient_location= Root_URL+"get_patient_location.php";

    public static String Doc_Register=Root_URL+"DoctorRegistration.php";

    public static String All_Patinets=Root_URL+"AllPatinets.php";
    public static String Doc_Info_API=Root_URL+"DoctorInfo.php";
    public static String UPDATE_RADIUS_URL=Root_URL+"update_radius.php";

    public static String GuideInfo=Root_URL+"GuideInfo.php";

    public static String GET_Pat_Via_Guide=Root_URL+"Get_Patient_Details_FrmGuide.php";

}
//ngrok config add-authtoken 2pU1y8rkrxYIsyfSJO8fzXSgM8Y_35H6ZALE1Nmx9h5XEZGiC
//ngrok http http://localhost:80