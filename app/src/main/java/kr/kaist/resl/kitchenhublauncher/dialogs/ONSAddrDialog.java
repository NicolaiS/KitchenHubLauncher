package kr.kaist.resl.kitchenhublauncher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.conn.util.InetAddressUtils;

import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.utils.ONSUtil;

/**
 * Created by nicolais on 4/27/15.
 * <p/>
 * Dialog for editing ONS address
 */
public class ONSAddrDialog extends Dialog {

    private EditText et;

    public ONSAddrDialog(Context context, final Runnable postSuccess) {
        super(context);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_ons_addr);

        // Hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        et = (EditText) findViewById(R.id.edittext);
        String oldAddr = ONSUtil.get(getContext());
        if (oldAddr != null) et.setText(oldAddr);

        /**
         * On accept click
         * Check entered values.
         * Save, execute postSuccess and dismiss if values are valid.
         */
        findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newAddr = et.getText().toString().trim();
                if (InetAddressUtils.isIPv4Address(newAddr)) {
                    ONSUtil.update(getContext(), newAddr);
                    postSuccess.run();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.error_not_ip), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
