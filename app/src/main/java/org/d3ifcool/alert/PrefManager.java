package org.d3ifcool.alert;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "welcome_screen";
    private static final String PREF_NAME2 = "input_company_profile";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_COMPLETE_INPUT_COMPANY_DATA = "IsCompleteInputCompanyData";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public void setCompleteInputCompanyData(boolean isComplete) {
        editor.putBoolean(IS_COMPLETE_INPUT_COMPANY_DATA, isComplete);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
    public boolean isCompleteInputCompanyData() {
        return pref.getBoolean(IS_COMPLETE_INPUT_COMPANY_DATA, false);
    }
}