package kr.kaist.resl.kitchenhublauncher.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import constants.database.KHSchema;
import constants.database.KHSchemaAttrContainer;
import constants.database.KHSchemaAttrContainerAttribute;
import constants.database.KHSchemaAttribute;
import constants.database.KHSchemaContainer;
import constants.database.KHSchemaDataSource;
import constants.database.KHSchemaDatasourceContainer;
import constants.database.KHSchemaProduct;
import constants.database.KHSchemaProductInfoMeta;
import constants.database.KHSchemaRecall;
import models.AttrContainer;
import models.Attribute;
import models.Container;
import models.DataSource;
import models.DataSourceContainerRelation;
import models.Product;
import models.Recall;

/**
 * Created by nicolais on 4/26/15.
 * <p/>
 * Util for accessing the Shared Storage module
 */
public class DBUtil {

    /**
     * Returns all data sources
     *
     * @param context context
     * @return list of Data Sources
     */
    public static List<DataSource> getDataSources(Context context) {
        List<DataSource> result = new ArrayList<DataSource>();

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Content_URIs.CONTENT_URI_DATA_SOURCE, KHSchemaDataSource.PROJECTION_ALL, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    DataSource r = new DataSource(cursor.getInt(0), cursor.getString(1), cursor.getInt(2));
                    result.add(r);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else {
            Log.e(DBUtil.class.getName(), "No readers found!");
        }

        cursor.close();

        return result;
    }

    /**
     * Returns all containers
     *
     * @param context context
     * @return list of containers
     */
    public static List<Container> getContainers(Context context) {
        List<Container> result = new ArrayList<Container>();

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Content_URIs.CONTENT_URI_CONTAINER, KHSchemaContainer.PROJECTION_ALL, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    Container c = new Container(cursor.getInt(0), cursor.getString(1), cursor.getInt(2));
                    result.add(c);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else {
            Log.e(DBUtil.class.getName(), "No containers found!");
        }

        cursor.close();

        return result;
    }

    /**
     * Returns all recall notifications
     *
     * @param context context
     * @return list of recall notifications
     */
    public static List<Recall> getRecallNotifications(Context context) {
        List<Recall> result = new ArrayList<Recall>();

        ContentResolver resolver = context.getContentResolver();
        Cursor c = resolver.query(Content_URIs.CONTENT_URI_RECALL, KHSchemaRecall.PROJECTION_ALL, null, null, null);

        if (c.moveToFirst()) {
            do {
                try {
                    Date issueDate = new Date(c.getLong(3));
                    Boolean accepted = c.getInt(7) > 0;
                    Recall r = new Recall(c.getInt(0), c.getString(1), c.getString(2), issueDate, c.getString(4), c.getString(5), c.getString(6), accepted, c.getInt(8));
                    result.add(r);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        } else {
            Log.e(DBUtil.class.getName(), "No recall notifications found!");
        }

        c.close();

        return result;
    }

    /**
     * Request specific recall notification1
     *
     * @param context  context
     * @param recallId primary key of recall notification
     * @return found recall notification or null
     */
    public static Recall getRecallNotification(Context context, Integer recallId) {
        Recall r = null;

        ContentResolver resolver = context.getContentResolver();
        Cursor c = resolver.query(Content_URIs.CONTENT_URI_RECALL, KHSchemaRecall.PROJECTION_ALL, null, null, null);

        if (c.moveToFirst()) {
            try {
                Date issueDate = new Date(c.getLong(3));
                Boolean accepted = c.getInt(7) > 0;
                r = new Recall(c.getInt(0), c.getString(1), c.getString(2), issueDate, c.getString(4), c.getString(5), c.getString(6), accepted, c.getInt(8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(DBUtil.class.getName(), "No recall notification with id " + recallId + " found!");
        }

        c.close();

        return r;
    }

    /**
     * Insert data source
     *
     * @param context   context
     * @param ipaddress IP of data source
     * @param typeId    Type ID of data source
     */
    public static void addDataSource(Context context, String ipaddress, Integer typeId) {
        ContentResolver resolver = context.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(KHSchemaDataSource.CN_IP_ADDRESS, ipaddress);
        values.put(KHSchemaDataSource.CN_TYPE_ID, typeId);

        resolver.insert(Content_URIs.CONTENT_URI_DATA_SOURCE, values);
    }

    /**
     * Insert container
     *
     * @param context context
     * @param name    Name of container
     * @param typeId  Type ID of container
     */
    public static Uri addContainer(Context context, String name, Integer typeId) {
        ContentResolver resolver = context.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(KHSchemaContainer.CN_NAME, name);
        values.put(KHSchemaContainer.CN_TYPE_ID, typeId);

        return resolver.insert(Content_URIs.CONTENT_URI_CONTAINER, values);
    }

    /**
     * Link container to data sources
     *
     * @param context     context
     * @param containerId Primary key of container
     * @param relations   Relations of data sources
     */
    public static void addRelations(Context context, Integer containerId, List<DataSourceContainerRelation> relations) {
        ContentResolver resolver = context.getContentResolver();

        for (DataSourceContainerRelation rc : relations) {
            ContentValues values = new ContentValues();
            values.put(KHSchemaDatasourceContainer.CN_CONTAINER_ID, containerId);
            values.put(KHSchemaDatasourceContainer.CN_DATA_SOURCE_ID, rc.getDatasource().getId());
            values.put(KHSchemaDatasourceContainer.CN_ANTENNA, rc.getAntenna());
            resolver.insert(Content_URIs.CONTENT_URI_DSC, values);
        }
    }

    /**
     * Remove ContainerDataSource relations
     *
     * @param context   context
     * @param relations relations to be deleted
     */
    public static void deleteRelations(Context context, List<DataSourceContainerRelation> relations) {
        ContentResolver resolver = context.getContentResolver();
        for (DataSourceContainerRelation rc : relations) {
            resolver.delete(Content_URIs.CONTENT_URI_DSC, KHSchema.CN_ID + " = ?", new String[]{Integer.toString(rc.getId())});
        }
    }

    /**
     * Update container
     *
     * @param context context
     * @param id      Primary key of container
     * @param name    new container name
     * @param typeId  new container type ID
     */
    public static void updateContainer(Context context, Integer id, String name, Integer typeId) {
        ContentResolver resolver = context.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(KHSchemaContainer.CN_NAME, name);
        values.put(KHSchemaContainer.CN_TYPE_ID, typeId);

        resolver.update(Content_URIs.CONTENT_URI_CONTAINER, values, KHSchema.CN_ID + " = ?", new String[]{Integer.toString(id)});
    }

    /**
     * Update data source
     *
     * @param context   context
     * @param id        Primary key of data source
     * @param ipaddress new IP of data source
     * @param typeId    new type ID of data source
     */
    public static void updateDataSource(Context context, Integer id, String ipaddress, Integer typeId) {
        ContentResolver resolver = context.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(KHSchemaDataSource.CN_IP_ADDRESS, ipaddress);
        values.put(KHSchemaDataSource.CN_TYPE_ID, typeId);

        resolver.update(Content_URIs.CONTENT_URI_DATA_SOURCE, values, KHSchema.CN_ID + " = ?", new String[]{Integer.toString(id)});
    }

    /**
     * Return data source relations for specific container
     *
     * @param context context
     * @param c       container
     * @return list of data source relations
     */
    public static List<DataSourceContainerRelation> getRelatedDataSources(Context context, Container c) {
        List<DataSourceContainerRelation> result = new ArrayList<DataSourceContainerRelation>();

        String[] projection = new String[]{KHSchema.CN_ID, KHSchemaDatasourceContainer.CN_DATA_SOURCE_ID, KHSchemaDatasourceContainer.CN_ANTENNA};
        String selection = KHSchemaDatasourceContainer.CN_CONTAINER_ID + " = ?";
        String[] selectionArgs = new String[]{Integer.toString(c.getId())};

        ContentResolver resolver = context.getContentResolver();
        Cursor c1 = resolver.query(Content_URIs.CONTENT_URI_DSC, projection, selection, selectionArgs, null);

        String selection2 = KHSchema.CN_ID + " = ?";

        if (c1.moveToFirst()) {
            do {
                DataSourceContainerRelation rc = new DataSourceContainerRelation(c1.getInt(0), c1.getInt(1), -1, c1.getInt(2));

                Cursor c2 = null;

                try {
                    c2 = resolver.query(Content_URIs.CONTENT_URI_DATA_SOURCE, KHSchemaDataSource.PROJECTION_ALL, selection2, new String[]{Integer.toString(rc.getDatasource().getId())}, null);
                    if (c2.moveToFirst()) {
                        DataSource r = new DataSource(c2.getInt(0), c2.getString(1), c2.getInt(2));
                        rc.setDatasource(r);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (c2 != null) c2.close();

                result.add(rc);
            } while (c1.moveToNext());
        } else {
            Log.e(DBUtil.class.getName(), "No containers found!");
        }

        c1.close();

        return result;
    }

    /**
     * Delete data source
     *
     * @param context      context
     * @param dataSourceId Primary key of data source
     */
    public static void deleteDataSource(Context context, Integer dataSourceId) {
        ContentResolver resolver = context.getContentResolver();

        resolver.delete(Content_URIs.CONTENT_URI_DSC, KHSchemaDatasourceContainer.CN_DATA_SOURCE_ID + " = ?", new String[]{Integer.toString(dataSourceId)});
        resolver.delete(Content_URIs.CONTENT_URI_DATA_SOURCE, KHSchema.CN_ID + " = ?", new String[]{Integer.toString(dataSourceId)});
    }

    /**
     * Delete container
     *
     * @param context     context
     * @param containerId primary key of container
     */
    public static void deleteContainer(Context context, Integer containerId) {
        ContentResolver resolver = context.getContentResolver();

        resolver.delete(Content_URIs.CONTENT_URI_DSC, KHSchemaDatasourceContainer.CN_CONTAINER_ID + " = ?", new String[]{Integer.toString(containerId)});
        resolver.delete(Content_URIs.CONTENT_URI_CONTAINER, KHSchema.CN_ID + " = ?", new String[]{Integer.toString(containerId)});
    }

    /**
     * Get all products marked "present" for specific container
     *
     * @param context     context
     * @param containerId primary key of container. null will return all present products
     * @return list of present products
     */
    public static List<Product> getPresentProducts(Context context, Integer containerId) {
        List<Product> results = new ArrayList<Product>();

        String selection = KHSchemaProduct.CN_PRESENT + " = ?";
        String[] selectionArgs = new String[]{Integer.toString(1)};
        if (containerId != null) {
            selection += " AND " + KHSchemaProduct.CN_CONTAINER_ID + " = ?";
            selectionArgs = new String[]{Integer.toString(1), Integer.toString(containerId)};
        }

        ContentResolver resolver = context.getContentResolver();
        Cursor c = resolver.query(Content_URIs.CONTENT_URI_PRODUCT, KHSchemaProduct.PROJECTION_ALL, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            do {
                Integer intPresent = c.getInt(7);
                Boolean present = intPresent > 0 ? true : false;
                Product p = new Product(c.getInt(0), c.getInt(1), c.getInt(2), c.getString(3), c.getString(4), c.getString(5), c.getInt(6), present, c.getLong(8));
                results.add(p);
            } while (c.moveToNext());
        }
        c.close();

        return results;
    }

    /**
     * Get specific product
     *
     * @param context   context
     * @param productId primary key of product
     * @return Product. null if none is found
     */
    public static Product getProduct(Context context, Integer productId) {
        Product result = null;

        String selection = KHSchema.CN_ID + " = ?";
        String[] selectionArgs = new String[]{Integer.toString(productId)};

        ContentResolver resolver = context.getContentResolver();
        Cursor c = resolver.query(Content_URIs.CONTENT_URI_PRODUCT, KHSchemaProduct.PROJECTION_ALL, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            Integer intPresent = c.getInt(7);
            Boolean present = intPresent > 0 ? true : false;
            result = new Product(c.getInt(0), c.getInt(1), c.getInt(2), c.getString(3), c.getString(4), c.getString(5), c.getInt(6), present, c.getLong(8));
        }
        c.close();

        return result;
    }

    /**
     * Check if product is recalled
     *
     * @param context context
     * @param p       product to be checked
     * @return true if product is recalled. false if not.
     */
    public static boolean isRecalled(Context context, Product p) {
        List<String> urns = new ArrayList<String>(2);
        String itemUrn = UrnUtil.getItemUrn(p);
        if (itemUrn != null) urns.add(itemUrn);
        String batchUrn = UrnUtil.getBatchUrn(context, p);
        if (batchUrn != null) urns.add(batchUrn);

        String selection = KHSchemaRecall.CN_URN + " = ?";
        for (String urn : urns) {
            ContentResolver resolver = context.getContentResolver();
            Cursor c = resolver.query(Content_URIs.CONTENT_URI_RECALL, new String[]{KHSchema.CN_ID}, selection, new String[]{urn}, null);
            if (c.moveToFirst()) {
                c.close();
                return true;
            }
            c.close();
        }

        return false;
    }

    /**
     * Get batch number of product
     *
     * @param context context
     * @param p       product
     * @return batch number of product. null if none is found.
     */
    public static String getBatchNo(Context context, Product p) {
        ContentResolver resolver = context.getContentResolver();
        String urn = UrnUtil.getUniqueUrn(p);

        String selection = KHSchemaProductInfoMeta.CN_URN + " = ?";
        String[] selectionArgs = new String[]{urn};
        Cursor c = resolver.query(Content_URIs.CONTENT_URI_PRODUCT_INFO_META, new String[]{KHSchema.CN_ID}, selection, selectionArgs, null);
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Integer pimId = c.getInt(0);
        c.close();

        selection = KHSchemaAttribute.CN_PIM_ID + " = ? AND " + KHSchemaAttribute.CN_ATTR_KEY + " = ?";
        selectionArgs = new String[]{Integer.toString(pimId), "attr_batch_no"};
        c = resolver.query(Content_URIs.CONTENT_URI_ATTRIBUTE, new String[]{KHSchemaAttribute.CN_ATTR_VALUE}, selection, selectionArgs, null);
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        String result = Integer.toString(c.getInt(0));
        c.close();

        return result;
    }

    /**
     * Returns primary key of ProductInfoMeta of URN
     *
     * @param context context
     * @param urn     URN
     * @return primary key of ProductInfoMeta. null if none is found.
     */
    public static Integer getProductInfoMetaId(Context context, String urn) {
        ContentResolver resolver = context.getContentResolver();

        String selection = KHSchemaProductInfoMeta.CN_URN + " = ?";
        String[] selectionArgs = new String[]{urn};
        Cursor c = resolver.query(Content_URIs.CONTENT_URI_PRODUCT_INFO_META, new String[]{KHSchema.CN_ID}, selection, selectionArgs, null);
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Integer pimId = c.getInt(0);
        c.close();

        return pimId;
    }

    /**
     * Return all attributes, excluding attributes of type "attr_name", of product
     *
     * @param context context
     * @param product product
     * @return list of attributes, excluding attributes of type "attr_name"
     */
    public static List<Attribute> getAttributesExcludingName(Context context, Product product) {
        List<Attribute> results = new ArrayList<Attribute>();

        results.addAll(getAttributesExcludingName(context, UrnUtil.getCompanyUrn(product)));
        results.addAll(getAttributesExcludingName(context, UrnUtil.getItemUrn(product)));
        results.addAll(getAttributesExcludingName(context, UrnUtil.getUniqueUrn(product)));
        results.addAll(getAttributesExcludingName(context, UrnUtil.getBatchUrn(context, product)));
        Collections.sort(results, new sortAttributes());

        return results;
    }

    /**
     * Return all attributes, excluding attributes of type "attr_name", of URN
     *
     * @param context context
     * @param urn     URN
     * @return list of attributes, excluding attributes of type "attr_name"
     */
    public static List<Attribute> getAttributesExcludingName(Context context, String urn) {
        List<Attribute> results = new ArrayList<Attribute>();

        Integer pimId = getProductInfoMetaId(context, urn);
        if (pimId == null) return results;

        ContentResolver resolver = context.getContentResolver();
        String selection = KHSchemaAttribute.CN_PIM_ID + " = ?";
        String[] selectionArgs = new String[]{Integer.toString(pimId)};
        Cursor c = resolver.query(Content_URIs.CONTENT_URI_ATTRIBUTE, KHSchemaAttribute.PROJECTION_ALL, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            do {
                if (!c.getString(2).equals("attr_name")) {
                    Attribute attr = new Attribute(c.getInt(0), c.getString(2), c.getString(3), c.getString(4), c.getInt(5), c.getString(6), c.getInt(7));
                    results.add(attr);
                }
            } while (c.moveToNext());
        }
        c.close();

        return results;
    }

    /**
     * Get all attribute container of product
     *
     * @param context context
     * @param product product
     * @return list of all attribute containers
     */
    public static List<AttrContainer> getAttributeContainers(Context context, Product product) {
        List<AttrContainer> results = new ArrayList<AttrContainer>();

        results.addAll(getAttributeContainers(context, UrnUtil.getCompanyUrn(product)));
        results.addAll(getAttributeContainers(context, UrnUtil.getItemUrn(product)));
        results.addAll(getAttributeContainers(context, UrnUtil.getUniqueUrn(product)));
        results.addAll(getAttributeContainers(context, UrnUtil.getBatchUrn(context, product)));
        Collections.sort(results, new sortAttrContainer());

        return results;
    }

    /**
     * Get all attribute container of URN
     *
     * @param context context
     * @param urn     URN
     * @return list of all attribute containers
     */
    public static List<AttrContainer> getAttributeContainers(Context context, String urn) {
        List<AttrContainer> results = new ArrayList<AttrContainer>();

        Integer pimId = getProductInfoMetaId(context, urn);
        if (pimId == null) return results;

        ContentResolver resolver = context.getContentResolver();
        String selection = KHSchemaAttrContainer.CN_PIM_ID + " = ?";
        String[] selectionArgs = new String[]{Integer.toString(pimId)};
        Cursor c = resolver.query(Content_URIs.CONTENT_URI_ATTR_CONTAINER, KHSchemaAttrContainer.PROJECTION_ALL, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            do {
                AttrContainer attrContainer = new AttrContainer(c.getInt(0), c.getString(2), c.getInt(3));
                String[] projection = KHSchemaAttrContainerAttribute.PROJECTION_ALL;
                selection = KHSchemaAttrContainerAttribute.CN_ATTR_CONTAINER_ID + " = ?";
                selectionArgs = new String[]{Integer.toString(attrContainer.getId())};
                Cursor c2 = resolver.query(Content_URIs.CONTENT_URI_ATTR_CONTAINER_ATTRIBUTES, projection, selection, selectionArgs, null);
                if (c2.moveToFirst()) {
                    do {
                        Attribute attr = new Attribute(c2.getInt(0), c2.getString(2), c2.getString(3), c2.getString(4), c2.getInt(5), c2.getString(6), c2.getInt(7));
                        attrContainer.getAttributes().add(attr);
                    } while (c2.moveToNext());
                }
                c2.close();
                Collections.sort(attrContainer.getAttributes(), new sortAttributes());
                results.add(attrContainer);
            } while (c.moveToNext());
        }
        c.close();

        return results;
    }

    /**
     * Look up attribute of type "attr_name" from URN
     *
     * @param context context
     * @param urn     URN
     * @return URN name. null if none is found
     */
    public static String getNameFromUrn(Context context, String urn) {
        String name = null;

        Integer pimId = getProductInfoMetaId(context, urn);
        if (pimId == null) return name;

        ContentResolver resolver = context.getContentResolver();
        String selection = KHSchemaAttribute.CN_PIM_ID + " = ? AND " + KHSchemaAttribute.CN_ATTR_KEY + " = ?";
        String[] selectionArgs = new String[]{Integer.toString(pimId), "attr_name"};
        Cursor c = resolver.query(Content_URIs.CONTENT_URI_ATTRIBUTE, new String[]{KHSchemaAttribute.CN_ATTR_VALUE}, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            name = c.getString(0);
        }
        c.close();

        return name;
    }

    /**
     * Get days for which product is to expire
     *
     * @param context context
     * @param p       product
     * @return days from current time to product expiration. null if none is found.
     */
    public static Long getDaysTilExpirationFromUrn(Context context, Product p) {
        Long result = null;

        String[] urn = new String[2];
        urn[0] = UrnUtil.getUniqueUrn(p);
        urn[1] = UrnUtil.getBatchUrn(context, p);

        for (int i = 0; i < urn.length; i++) {
            if (result != null) continue;
            Integer pimId = getProductInfoMetaId(context, urn[i]);
            if (pimId == null) return result;

            ContentResolver resolver = context.getContentResolver();
            String selection = KHSchemaAttribute.CN_PIM_ID + " = ? AND " + KHSchemaAttribute.CN_ATTR_KEY + " = ?";
            String[] selectionArgs = new String[]{Integer.toString(pimId), "attr_expiration_date"};
            Cursor c = resolver.query(Content_URIs.CONTENT_URI_ATTRIBUTE, new String[]{KHSchemaAttribute.CN_ATTR_VALUE, KHSchemaAttribute.CN_VALUE_FORMAT}, selection, selectionArgs, null);
            if (c.moveToFirst()) {
                try {
                    Date expDate = new SimpleDateFormat(c.getString(1)).parse(c.getString(0));
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date nowDate = formatter.parse(formatter.format(new Date()));

                    c.close();
                    return TimeUnit.DAYS.convert(expDate.getTime() - nowDate.getTime(), TimeUnit.MILLISECONDS);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            c.close();
        }

        return result;
    }

    /**
     * Get names of recalled product
     *
     * @param context  context
     * @param recallId primary key of recall notification
     * @return Array of string. 0 is company name and 1 is item name. Names are null if none are found.
     */
    public static String[] getRecallNames(Context context, Integer recallId) {
        String[] result = new String[2];

        ContentResolver resolver = context.getContentResolver();
        String[] projection = new String[]{KHSchemaRecall.CN_URN};
        String selection = KHSchema.CN_ID + " = ?";
        String[] selectionArgs = new String[]{Integer.toString(recallId)};
        Cursor c = resolver.query(Content_URIs.CONTENT_URI_RECALL, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            String urn = c.getString(0);
            result[0] = DBUtil.getNameFromUrn(context, UrnUtil.getCompanyUrn(urn));
            result[1] = DBUtil.getNameFromUrn(context, UrnUtil.getItemUrn(urn));
        }
        c.close();

        return result;
    }

    /**
     * Mark recall accepted
     *
     * @param context  context
     * @param recallId Primary key of recall notification
     */
    public static void setRecallAccepted(Context context, Integer recallId) {
        ContentResolver resolver = context.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(KHSchemaRecall.CN_ACCEPTED, true);

        String selection = KHSchema.CN_ID + " = ?";
        String[] selectionArgs = new String[]{Integer.toString(recallId)};
        resolver.update(Content_URIs.CONTENT_URI_RECALL, values, selection, selectionArgs);
    }

    /**
     * Sort attributes by sort order
     */
    static class sortAttributes implements Comparator<Attribute> {
        @Override
        public int compare(Attribute a1, Attribute a2) {
            return a1.getSortOrder().compareTo(a2.getSortOrder());
        }
    }

    /**
     * Sort attribute containers by sort order
     */
    static class sortAttrContainer implements Comparator<AttrContainer> {
        @Override
        public int compare(AttrContainer a1, AttrContainer a2) {
            return a1.getSortOrder().compareTo(a2.getSortOrder());
        }
    }
}
