package kr.kaist.resl.kitchenhublauncher.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import constants.ProviderConstants;

/**
 * Created by nicolais on 4/29/15.
 */
public class ONSUtil {

    public static void update(Context context, String text) {
        ContentValues values = new ContentValues();
        values.put(ProviderConstants.ONS_ADDR, text);
        context.getContentResolver().insert(Content_URIs.CONTENT_URI_ONS_ADDR, values);
    }

    public static String get(Context context) {
        Cursor c = context.getContentResolver().query(Content_URIs.CONTENT_URI_ONS_ADDR, null, null, null, null);
        String result = null;
        if (c.moveToFirst()) result = c.getString(0);
        c.close();
        return result;
    }
}
