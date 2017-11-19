package com.zenzanodanny.trustme;

/**
 * Created by Zenzano on 4/11/2017.
 */
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    //Shared Preferences
    private SharedPreferences pref;

    //Editor for Shared Preferences
    private SharedPreferences.Editor editor;
    //Shared pref file name
    private static final String PREF_NAME = "TRUSTME";

    //Context
    private Context _context;
    //Shared pref mode

    //SETEO LOS KEYS

        private String KEY_REGISTRO = "nivel_registro";
    private String KEY_PROFILE_PHOTO_EXIST = "profile_photo_exist";
        private String KEY_USER_ID="user_id";
        private String KEY_MSISDN="msisdn";
        private String KEY_MSISDN_prefix="prefix";
        private String KEY_COUNTRY="country";
        private String KEY_USER_name = "user_name";
        private String KEY_USER_LASTNAME = "user_lastname";


    private int PRIVATE_MODE = 0;
    //VALORES POR DEFECTO SI NO ESTA SETEADO EL VALOR
    private int default_register_level=0;
    private boolean default_profile_foto_exist=false;

    //El nivel de registro sirve para ver si el usuario ya se registro:
    //0:usuario nuevo
    //1:usuario que registro su msisdn
    //2:usuario que culmino su registro con nombre y apellido
    // Constructor
    public SessionManager(Context context) {
        System.out.println("LOGDANNY:SESSIOINMANAGER:CONSTRUCTOR");
        _context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void saveUserId(String userid) {
        editor.putString(KEY_USER_ID, userid);
        editor.commit();
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }


    public void saveUserName(String username) {
        editor.putString(KEY_USER_name, username);
        editor.commit();
    }

    public String getUserName() {
        return pref.getString(KEY_USER_name, null);
    }

    public void saveUserLastName(String username) {
        editor.putString(KEY_USER_LASTNAME, username);
        editor.commit();
    }

    public String getUserLastName() {
        return pref.getString(KEY_USER_LASTNAME, null);
    }

    public void saveRegisterLevel(int reg_level) {
        System.out.println("LOGDANNY:SESSIOINMANAGER:saveRegisterLevel:"+reg_level);
        editor.putInt(KEY_REGISTRO, reg_level);
        editor.commit();
    }

    public int getRegisterLevel() {
        System.out.println("LOGDANNY:SESSIOINMANAGER:INGRESO getRegisterLevel");
        return pref.getInt(KEY_REGISTRO,default_register_level);

    }

    public void savePROFILE_PHOTO_EXIST(boolean _exist) {
        System.out.println("LOGDANNY:SESSIOINMANAGER:savePROFILE_PHOTO_EXIST:"+_exist);
        editor.putBoolean(KEY_PROFILE_PHOTO_EXIST, _exist);
        editor.commit();
    }
    public boolean getPROFILE_PHOTO_EXIST() {
        System.out.println("LOGDANNY:SESSIOINMANAGER:INGRESO getPROFILE_PHOTO_EXIST");
        return pref.getBoolean(KEY_PROFILE_PHOTO_EXIST,default_profile_foto_exist);
    }

    public int getKEY_MSISDN()  {
        return pref.getInt(KEY_MSISDN,0);
    }

    public void saveKEY_MSISDN(int _KEY_MSISDN) {
        System.out.println("LOGDANNY:SESSIOINMANAGER:saveKEY_MSISDN:"+_KEY_MSISDN);
        editor.putInt(KEY_MSISDN, _KEY_MSISDN);
        editor.commit();
    }

    public int getKEY_MSISDN_prefix()  {
        return pref.getInt(KEY_MSISDN_prefix,0);
    }

    public void saveKEY_MSISDN_prefix(int _KEY_MSISDN_prefix) {
        System.out.println("LOGDANNY:saveKEY_MSISDN_prefix:savePROFILE_PHOTO_EXIST:"+_KEY_MSISDN_prefix);
        editor.putInt(KEY_MSISDN_prefix,_KEY_MSISDN_prefix);
        editor.commit();


    }

    public String getKEY_COUNTRY()  {
        return pref.getString(KEY_COUNTRY, null);
    }

    public void saveKEY_COUNTRY(String _KEY_COUNTRY) {
        System.out.println("LOGDANNY:saveKEY_MSISDN_prefix:saveKEY_COUNTRY:"+_KEY_COUNTRY);
        editor.putString(KEY_COUNTRY, _KEY_COUNTRY);
        editor.commit();
    }
}
