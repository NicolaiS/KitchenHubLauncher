package kr.kaist.resl.kitchenhublauncher.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import constants.KHBroadcasts;
import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.dialogs.ONSAddrDialog;
import kr.kaist.resl.kitchenhublauncher.utils.Content_URIs;
import kr.kaist.resl.kitchenhublauncher.utils.NetworkUtil;
import kr.kaist.resl.kitchenhublauncher.utils.ONSUtil;

/**
 * Created by nicolais on 15. 3. 25.
 * <p/>
 * System information and debugging
 */
public class InfoFragment extends Fragment {

    public static final InfoFragment newInstance() {
        return new InfoFragment();
    }

    private TextView onsAddrTV;

    ContentResolver resolver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        resolver = getActivity().getContentResolver();

        View v = inflater.inflate(R.layout.fragment_info, null);

        // Load IP and MAC of wlan0
        TextView tv = (TextView) v.findViewById(R.id.info);
        tv.append(NetworkUtil.getIPAddress(true) + "\n");
        tv.append(NetworkUtil.getMACAddress("wlan0"));

        // Load ONS address
        onsAddrTV = (TextView) v.findViewById(R.id.info_ons_addr);
        String onsAddr = ONSUtil.get(getActivity());
        if (onsAddr != null) onsAddrTV.setText(onsAddr);

        // Change ONS dialog
        v.findViewById(R.id.info_ons_addr_config).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ONSAddrDialog(getActivity(), new Runnable() {
                    @Override
                    public void run() {
                        String onsAddr = ONSUtil.get(getActivity());
                        if (onsAddr != null) onsAddrTV.setText(onsAddr);
                    }
                }).show();
            }
        });

        // Refresh product information button
        v.findViewById(R.id.info_product_info_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().sendBroadcast(new Intent(KHBroadcasts.PRODUCTS_UPDATED));
            }
        });

        // Clear product information button
        v.findViewById(R.id.info_product_info_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolver.delete(Content_URIs.CONTENT_URI_ATTR_CONTAINER_ATTRIBUTES, null, null);
                resolver.delete(Content_URIs.CONTENT_URI_ATTR_CONTAINER, null, null);
                resolver.delete(Content_URIs.CONTENT_URI_ATTRIBUTE, null, null);
                resolver.delete(Content_URIs.CONTENT_URI_PRODUCT_INFO_META, null, null);
                getActivity().sendBroadcast(new Intent(KHBroadcasts.PRODUCT_INFORMATION_UPDATED));
            }
        });

        // Refresh recall data button
        v.findViewById(R.id.info_recall_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().sendBroadcast(new Intent(KHBroadcasts.PRODUCT_INFORMATION_UPDATED));
            }
        });

        // Clear recall data button
        v.findViewById(R.id.info_recall_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolver.delete(Content_URIs.CONTENT_URI_RECALL, null, null);
                getActivity().sendBroadcast(new Intent(KHBroadcasts.RECALL_UPDATED));
            }
        });

        // Clear products button
        v.findViewById(R.id.info_products_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolver.delete(Content_URIs.CONTENT_URI_PRODUCT, null, null);
                getActivity().sendBroadcast(new Intent(KHBroadcasts.PRODUCTS_UPDATED));
            }
        });

        return v;
    }

}
