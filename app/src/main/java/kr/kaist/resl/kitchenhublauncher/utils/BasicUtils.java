package kr.kaist.resl.kitchenhublauncher.utils;

import android.content.Context;

/**
 * Created by nicolais on 4/27/15.
 */
public class BasicUtils {

    public static String getStringResourceByName(Context c, String rname) {
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(rname, "string", packageName);
        return c.getString(resId);
    }

}
