package kr.kaist.resl.kitchenhublauncher.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.models.ListProduct;
import kr.kaist.resl.kitchenhublauncher.utils.DBUtil;
import kr.kaist.resl.kitchenhublauncher.utils.UrnUtil;
import models.Product;

/**
 * Created by nicolais on 5/24/15.
 * <p/>
 * Product adapter for product listview on Home Screen.
 */
public class ProductAdapter extends ArrayAdapter {

    private LayoutInflater inflater;

    private Integer containerId;
    private List<ListProduct> objects = new ArrayList<ListProduct>();

    /**
     * @param context     context
     * @param containerId Container of products to load. Null will create a "Combined" view
     */
    public ProductAdapter(Context context, Integer containerId) {
        super(context, -1);
        this.containerId = containerId;
        inflater = LayoutInflater.from(context);

        loadProducts();
    }

    /**
     * Reloads products
     */
    public void loadProducts() {
        List<Product> products = DBUtil.getPresentProducts(getContext(), containerId);
        objects.clear();

        // Load and cache product names, expiration time and recalled status.
        for (Product p : products) {
            String companyName = DBUtil.getNameFromUrn(getContext(), UrnUtil.getCompanyUrn(p));
            if (companyName == null) companyName = getContext().getString(R.string.unknown);
            String itemName = DBUtil.getNameFromUrn(getContext(), UrnUtil.getItemUrn(p));
            if (itemName == null) itemName = getContext().getString(R.string.unknown);
            Long expirationTime = DBUtil.getDaysTilExpirationFromUrn(getContext(), p);
            boolean recalled = DBUtil.isRecalled(getContext(), p);

            objects.add(new ListProduct(p.getId(), companyName, itemName, expirationTime, recalled));
        }

        // Sort loaded products
        Collections.sort(objects, new SortProducts());

        notifyDataSetChanged();
    }

    /**
     * Sort ListProduct by expiration time and the following priority:
     * <p/>
     * 1. Recalled
     * 2. Expired
     * 3. Expires today
     * 4. Expires within 3 days
     * 5. More than 3 days till expiration
     */
    class SortProducts implements Comparator<ListProduct> {
        @Override
        public int compare(ListProduct p1, ListProduct p2) {
            if (p1.isRecalled() && p1.isRecalled()) {
                return compareExpirationTime(p1, p2);
            } else if (p1.isRecalled()) {
                return -1;
            } else if (p2.isRecalled()) {
                return 1;
            }

            return compareExpirationTime(p1, p2);
        }

        private int compareExpirationTime(ListProduct p1, ListProduct p2) {
            if (p1.getExpirationTime() == null & p2.getExpirationTime() == null) return 0;

            if (p1.getExpirationTime() == null) {
                if (p2.getExpirationTime() > 3) {
                    return -1;
                } else {
                    return 1;
                }
            }

            if (p2.getExpirationTime() == null) {
                if (p1.getExpirationTime() > 3) {
                    return 1;
                } else {
                    return -1;
                }
            }
            return p1.getExpirationTime().compareTo(p2.getExpirationTime());
        }
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ListProduct product = (ListProduct) getItem(position);

        if (product.isRecalled()) {
            view = inflater.inflate(R.layout.element_product_recalled, null);
        } else if (product.getExpirationTime() == null) {
            view = inflater.inflate(R.layout.element_product_warning_low, null);
        } else if (product.getExpirationTime() > 3) {
            view = inflater.inflate(R.layout.element_product, null);
        } else if (product.getExpirationTime() > 0) {
            view = inflater.inflate(R.layout.element_product_warning_low, null);
        } else if (product.getExpirationTime() > -1) {
            view = inflater.inflate(R.layout.element_product_warning_high, null);
        } else {
            view = inflater.inflate(R.layout.element_product_expired, null);
        }

        ((TextView) view.findViewById(R.id.element_text1)).setText(product.getItemName());
        ((TextView) view.findViewById(R.id.element_text2)).setText(product.getCompanyName());
        TextView t3 = (TextView) view.findViewById(R.id.element_text3);
        if (product.getExpirationTime() != null)

        {
            t3.setText(product.getExpirationTime() + "d");
        } else

        {
            t3.setText("?d");
        }

        return view;
    }
}