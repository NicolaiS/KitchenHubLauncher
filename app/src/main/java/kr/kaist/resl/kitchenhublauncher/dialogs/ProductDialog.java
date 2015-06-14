package kr.kaist.resl.kitchenhublauncher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.utils.DBUtil;
import kr.kaist.resl.kitchenhublauncher.utils.UrnUtil;
import kr.kaist.resl.kitchenhublauncher.utils.ViewUtil;
import models.AttrContainer;
import models.Attribute;
import models.Product;

/**
 * Created by nicolais on 5/5/15.
 */
public class ProductDialog extends Dialog {

    public ProductDialog(Context context, Integer productId) {
        super(context);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ViewUtil.hideSystemUI(getWindow().getDecorView());

        setContentView(R.layout.dialog_product);

        Product p = DBUtil.getProduct(getContext(), productId);

        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        String itemName = DBUtil.getNameFromUrn(getContext(), UrnUtil.getItemUrn(p));
        ((TextView) findViewById(R.id.item_name)).setText(itemName);

        String companyName = DBUtil.getNameFromUrn(getContext(), UrnUtil.getCompanyUrn(p));
        ((TextView) findViewById(R.id.company_name)).setText(companyName);

        LinearLayout attrContainer = (LinearLayout) findViewById(R.id.attr_container);

        List<AttrContainer> containers = DBUtil.getAttributeContainers(getContext(), p);
        List<Attribute> attributes = DBUtil.getAttributesExcludingName(getContext(), p);

        int currentContainer = 0;
        int currentAttr = 0;
        for (int i = 0; i < containers.size() + attributes.size(); i++) {
            View child;
            if (currentContainer >= containers.size()) {
                child = getAttributeView(attributes.get(currentAttr));
                currentAttr++;
            } else if (currentAttr >= attributes.size()) {
                child = getContainerView(containers.get(currentContainer));
                currentContainer++;
            } else {
                AttrContainer c = containers.get(currentContainer);
                Attribute a = attributes.get(currentAttr);
                if (a.getSortOrder() < c.getSortOrder()) {
                    child = getAttributeView(a);
                    currentAttr++;
                } else {
                    child = getContainerView(c);
                    currentContainer++;
                }
            }
            if (child != null) attrContainer.addView(child);
        }
    }

    private View getContainerView(AttrContainer c) {
        View v = getLayoutInflater().inflate(R.layout.attr_container, null);
        ((TextView) v.findViewById(R.id.container_name)).setText(c.getName());
        LinearLayout container = (LinearLayout) v.findViewById(R.id.container);
        for (Attribute a : c.getAttributes()) {
            container.addView(getAttributeView(a));
        }
        return v;
    }

    private View getAttributeView(Attribute a) {
        View v = null;
        switch (a.getAttrTypeId()) {
            case 1:
            case 3:
            case 4:
                v = getLayoutInflater().inflate(R.layout.attr_type_1, null);
                ((TextView) v.findViewById(R.id.attr_name)).setText(a.getAttrName());
                ((TextView) v.findViewById(R.id.attr_value)).setText(a.getAttrValue());
                break;
            case 2:
                v = getLayoutInflater().inflate(R.layout.attr_type_2, null);
                ((TextView) v.findViewById(R.id.attr_name)).setText(a.getAttrName());
                ((TextView) v.findViewById(R.id.attr_value)).setText(a.getAttrValue());
                break;
            case 5:
                v = getLayoutInflater().inflate(R.layout.attr_type_5, null);
                ((TextView) v.findViewById(R.id.attr_value)).setText(a.getAttrValue());
                break;
        }
        return v;
    }


}