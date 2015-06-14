package kr.kaist.resl.kitchenhublauncher.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import constants.KHBroadcasts;
import constants.database.KHSchema;
import kr.kaist.resl.kitchenhublauncher.Constants;
import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.adapter.ContainerAdapter;
import kr.kaist.resl.kitchenhublauncher.adapter.NotificationAdapter;
import kr.kaist.resl.kitchenhublauncher.adapter.ProductAdapter;
import kr.kaist.resl.kitchenhublauncher.dialogs.ProductDialog;
import kr.kaist.resl.kitchenhublauncher.dialogs.RecallDetailsDialog;
import kr.kaist.resl.kitchenhublauncher.dialogs.RecallWarningDialog;
import kr.kaist.resl.kitchenhublauncher.dialogs.SettingsDialog;
import kr.kaist.resl.kitchenhublauncher.models.ListProduct;
import models.Container;
import models.Recall;

/**
 * Created by nicolais on 15. 3. 25.
 * <p/>
 * Home Activity
 * <p/>
 * Launcher to show container/products and access settings
 */
public class HomeActivity extends FragmentActivity {

    protected PowerManager.WakeLock wakeLock;

    // Clock variables
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private TextView tvTime;

    // Views and adapters
    private ListView listProducts;
    private List<ProductAdapter> pAdapters = new ArrayList<ProductAdapter>();

    private BroadcastReceiver broadcastReceiver;

    private boolean updateTheme = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applyTheme();

        setContentView(R.layout.activity_home);

        // Start services
        startProductHandler();
        startProductInformation();
        startRecall();

        // Keep screen awake
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getLocalClassName());
        this.wakeLock.acquire();

        // Initialize settings button
        findViewById(R.id.top_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingsDialog(false);
            }
        });

        // Initialize clock
        tvTime = (TextView) findViewById(R.id.text_time);
        updateTime();

        // Show themes dialog again if theme was just changed
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(Constants.KH_ACTION_THEME_CHANGED, false)) {
                showSettingsDialog(true);
            }
        }

        // Initialize broadcastreceiver
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleBroadcast(intent);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Constants.KH_ACTION_THEME_CHANGED);
        filter.addAction(Constants.BROADCAST_CONTAINER_CHANGE);
        filter.addAction(KHBroadcasts.PRODUCTS_UPDATED);
        filter.addAction(KHBroadcasts.PRODUCT_INFORMATION_UPDATED);
        filter.addAction(KHBroadcasts.RECALL_UPDATED);
        registerReceiver(broadcastReceiver, filter);

        // Initialize container/product list
        initLists();

        // Initialize notifications
        initNotifications();
    }

    // Set value to reload themes dialog if theme is changed
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.KH_ACTION_THEME_CHANGED, updateTheme);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);

        this.wakeLock.release();

        super.onDestroy();
    }

    /**
     * Initializes notification
     * Should only be called once in onCreate
     */
    private void initNotifications() {
        // Load notification listview and set adapter
        final ListView nList = (ListView) findViewById(R.id.notification_list);
        nList.setAdapter(new NotificationAdapter(this));

        // Initialize notification button
        View nIcon = findViewById(R.id.top_notifications);
        nIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotifications();
            }
        });

        // Initialize "close notification" button
        findViewById(R.id.notification_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideTotifications();
            }
        });

        // Initialize onItemClick notification list to show RecallDetailsDialog
        nList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Recall r = (Recall) nList.getAdapter().getItem(pos);
                new RecallDetailsDialog(HomeActivity.this, r.getId()).show();
            }
        });

        // Load notification data and hide notifications
        updateNotifications();
        hideTotifications();
    }

    /**
     * Shows notifications
     */
    private void showNotifications() {
        findViewById(R.id.notification_view).setVisibility(View.VISIBLE);

        findViewById(R.id.home_overlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideTotifications();
            }
        });
        findViewById(R.id.home_overlay).setBackgroundColor(0xAA000000);
    }

    /**
     * Hides notifications
     */
    private void hideTotifications() {
        findViewById(R.id.notification_view).setVisibility(View.GONE);

        findViewById(R.id.home_overlay).setOnClickListener(null);
        findViewById(R.id.home_overlay).setClickable(false);

        findViewById(R.id.home_overlay).setBackgroundColor(0);
    }

    /**
     * Load notifications from database to notification list
     */
    private void updateNotifications() {
        ListView notificationList = (ListView) findViewById(R.id.notification_list);
        NotificationAdapter nAdapter = (NotificationAdapter) notificationList.getAdapter();

        nAdapter.loadNotifications();

        // Only show count icon if any notifications are present
        if (nAdapter.getCount() > 0) {
            findViewById(R.id.notification_count_view).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.notification_count)).setText(Integer.toString(nAdapter.getCount()));
        } else {
            findViewById(R.id.notification_count_view).setVisibility(View.GONE);
        }

        ((TextView) findViewById(R.id.notification_count_list)).setText(Integer.toString(nAdapter.getCount()));
    }

    /**
     * Handle broadcasts
     *
     * @param intent intent from broadcast
     */
    private void handleBroadcast(Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_TIME_TICK)) {
            // Update clock and make sure services are running
            updateTime();
            startProductHandler();
            startProductInformation();
            startRecall();
        } else if (action.equals(Constants.KH_ACTION_THEME_CHANGED)) {
            // Apply new theme
            Fragment settingsDialog = getSupportFragmentManager().findFragmentByTag(Constants.SETTINGS_DIALOG);
            if (settingsDialog != null) ((DialogFragment) settingsDialog).dismiss();
            updateTheme = true;
            recreate();
        } else if (action.equals(Constants.BROADCAST_CONTAINER_CHANGE)) {
            // Reload list if containers change
            initLists();
        } else if (action.equals(KHBroadcasts.PRODUCTS_UPDATED) || action.equals(KHBroadcasts.PRODUCT_INFORMATION_UPDATED)) {
            // Reload products if a change to products or product information have occured
            refreshListProducts();
        } else if (action.equals(KHBroadcasts.RECALL_UPDATED)) {
            // Reload products and update notifications if a recall have been issued.
            refreshListProducts();
            updateNotifications();

            // Show recall warning dialog
            int recallId = intent.getIntExtra(KHSchema.CN_ID, -1);
            if (recallId != -1) {
                new RecallWarningDialog(this, recallId).show();
            }
        }
    }

    /**
     * Apply saved theme
     */
    public void applyTheme() {
        SharedPreferences sPref = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        int themeID = sPref.getInt(Constants.PREF_THEME_RESID_ID, Constants.DEFAULT_THEME_RESID);
        setTheme(themeID);
    }

    private void updateTime() {
        tvTime.setText(sdf.format(new Date()));
    }

    /**
     * Initialize container and product list
     */
    private void initLists() {
        ListView listContainers = (ListView) findViewById(R.id.list_containers);
        listProducts = (ListView) findViewById(R.id.list_products);

        ContainerAdapter containerAdapter = new ContainerAdapter(this);

        /**
         * Initialize product adapters for all containers
         */
        pAdapters.clear();
        for (int i = 0; i < containerAdapter.getCount(); i++) {
            Integer containerId = ((Container) containerAdapter.getItem(i)).getId();
            pAdapters.add(new ProductAdapter(this, containerId));
        }

        // Initialize container adapter to change product list depending on selected container.
        listContainers.setAdapter(containerAdapter);
        listContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                  @Override
                                                  public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                                                      listProducts.setAdapter(pAdapters.get(position));
                                                  }
                                              }
        );

        // Initialize container selection to "combined"
        listContainers.performItemClick(containerAdapter.getView(0, null, null), 0, containerAdapter.getItemId(0));

        // Open Product details dialog when clicking a product in product list
        listProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ListAdapter adapter = listProducts.getAdapter();
                if (adapter != null) {
                    ListProduct p = (ListProduct) adapter.getItem(position);
                    new ProductDialog(HomeActivity.this, p.getProductId()).show();
                }
            }
        });
    }

    /**
     * Reloads all product adapters
     */
    private void refreshListProducts() {
        for (ProductAdapter adapter : pAdapters) {
            adapter.loadProducts();
        }
    }

    /**
     * Show settings dialog
     *
     * @param goToColors Go directly to colors/theme tab
     */
    public void showSettingsDialog(boolean goToColors) {
        SettingsDialog settingsDialog = new SettingsDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KH_ACTION_THEME_CHANGED, goToColors);
        settingsDialog.setArguments(bundle);
        settingsDialog.show(getSupportFragmentManager(), Constants.SETTINGS_DIALOG);
    }

    /**
     * Starts product handler service if not already started
     */
    private void startProductHandler() {
        Intent i = new Intent();
        i.setComponent(new ComponentName("kr.kaist.resl.kitchenhubproducthandler", "kr.kaist.resl.kitchenhubproducthandler.service.ProductHandlerService"));
        startService(i);
    }

    /**
     * Starts product information service if not already started
     */
    private void startProductInformation() {
        Intent i = new Intent();
        i.setComponent(new ComponentName("kr.kaist.resl.kitchenhubproductinformation", "kr.kaist.resl.kitchenhubproductinformation.service.ProductInformationService"));
        startService(i);
    }

    /**
     * Starts recall service if not already started
     */
    private void startRecall() {
        Intent i = new Intent();
        i.setComponent(new ComponentName("kr.kaist.resl.kitchenhubrecall", "kr.kaist.resl.kitchenhubrecall.service.RecallService"));
        startService(i);
    }

}