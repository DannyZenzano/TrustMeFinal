package com.zenzanodanny.trustme;

import android.content.Context;

/**
 * Created by Zenzano on 6/11/2017.
 */

public class TelephoneUtils {
    public int CountryTelephoneCode;
    public String CountryStringCode;
    //Context
    private Context _context;

    public TelephoneUtils(Context micontext) {
        _context = micontext;
        CountryTelephoneCode=0;
        CountryStringCode="";
    }

    public void imprimir_Shared_Preferences(){
        SessionManager mysession1;
        mysession1= new SessionManager(_context);
        String miuid=mysession1.getUserId();
        String username1=mysession1.getUserName();
        String userlastname1=mysession1.getUserLastName();
        int reglevel1=mysession1.getRegisterLevel();
        int miprefijoLocal=mysession1.getKEY_MSISDN_prefix();
        String country=mysession1.getKEY_COUNTRY();
        int mymsisdn=mysession1.getKEY_MSISDN();
        boolean photprof1=mysession1.getPROFILE_PHOTO_EXIST();

        System.out.println("LOGDANNY:Shared_Preferences:getUserId:"+miuid);
        System.out.println("LOGDANNY:Shared_Preferences:getUserName:"+username1);
        System.out.println("LOGDANNY:Shared_Preferences:getUserLastName:"+userlastname1);
        System.out.println("LOGDANNY:Shared_Preferences:reglevel1:"+reglevel1);
        System.out.println("LOGDANNY:Shared_Preferences:getKEY_MSISDN:"+mymsisdn);
        System.out.println("LOGDANNY:Shared_Preferences:getKEY_MSISDN_prefix:"+miprefijoLocal);
        System.out.println("LOGDANNY:Shared_Preferences:getKEY_COUNTRY:"+country);
        System.out.println("LOGDANNY:Shared_Preferences:getPROFILE_PHOTO_EXIST:"+photprof1);



    }

    public int getCountryTelephoneCode(String _countryStringCode) {
        CountryTelephoneCode=0;

        String[] rl=_context.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(_countryStringCode.trim())){
                CountryTelephoneCode=Integer.parseInt(g[0]);
                break;
            }
        }
        return CountryTelephoneCode;

    }
}
