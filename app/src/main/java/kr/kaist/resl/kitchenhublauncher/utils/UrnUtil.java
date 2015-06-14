package kr.kaist.resl.kitchenhublauncher.utils;

import android.content.Context;
import android.util.Log;

import models.Product;

/**
 * Created by nicolais on 5/24/15.
 */
public class UrnUtil {

    private static String preSGTIN = "urn:epc:class:sgtin:";
    private static String preLGTIN = "urn:epc:class:lgtin:";

    public static String getCompanyUrn(Product p) {
        return preSGTIN + p.getCompanyPrefix() + ".*.*";
    }

    public static String getItemUrn(Product p) {
        return preSGTIN + p.getCompanyPrefix() + "." + p.getItemRefNo() + ".*";
    }

    public static String getUniqueUrn(Product p) {
        return preSGTIN + p.getCompanyPrefix() + "." + p.getItemRefNo() + "." + p.getSerial();
    }

    public static String getBatchUrn(Context context, Product p) {
        String batchNumber = DBUtil.getBatchNo(context, p);
        return preLGTIN + p.getCompanyPrefix() + "." + p.getItemRefNo() + "." + batchNumber;
    }

    public static String getCompanyUrn(String urn) {
        String[] firstSplit = urn.split(":");
        String[] secondSplit = firstSplit[firstSplit.length - 1].split("\\.");

        return preSGTIN + secondSplit[0] + ".*.*";
    }

    public static String getItemUrn(String urn) {
        String[] firstSplit = urn.split(":");
        String[] secondSplit = firstSplit[firstSplit.length - 1].split("\\.");

        return preSGTIN + secondSplit[0] + "." + secondSplit[1] + ".*";
    }

}
