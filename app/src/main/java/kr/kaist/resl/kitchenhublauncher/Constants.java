package kr.kaist.resl.kitchenhublauncher;

/**
 * Created by nicolais on 4/1/15.
 *
 * Constants shared across application
 */
public class Constants {
    // Shared preference filename
    public static final String SHARED_PREFERENCES_NAME = "kh_preferences";

    // Current theme identifier
    public static final String PREF_THEME_RESID_ID = "theme_resid";
    // Default theme
    public static final int DEFAULT_THEME_RESID = R.style.KH_Theme_Light_Grey;

    // Settings dialog tag
    public static final String SETTINGS_DIALOG = "SETTINGS_DIALOG";

    // Broadcast for when theme is changed
    public static final String BROADCAST_THEME_CHANGED = "KH_ACTION_THEME_CHANGED";

    // Broadcast for when containers are added/deleted/updated
    public static final String BROADCAST_CONTAINER_CHANGE = "BROADCAST_CONTAINER_CHANGE";
}
