package kr.kaist.resl.kitchenhublauncher.enums;

import kr.kaist.resl.kitchenhublauncher.R;

/**
 * Created by nicolais on 15. 3. 25.
 *
 * Enumerator for themes.
 * Linking theme and theme icon
 */
public enum enum_themes {
    Dark_Red(R.style.KH_Theme_Dark_Red, R.drawable.theme_dark_red),
    Light_Red(R.style.KH_Theme_Light_Red, R.drawable.theme_light_red),
    Dark_Pink(R.style.KH_Theme_Dark_Pink, R.drawable.theme_dark_pink),
    Light_Pink(R.style.KH_Theme_Light_Pink, R.drawable.theme_light_pink),
    Dark_Purple(R.style.KH_Theme_Dark_Purple, R.drawable.theme_dark_purple),
    Light_Purple(R.style.KH_Theme_Light_Purple, R.drawable.theme_light_purple),
    Dark_PurpleDeep(R.style.KH_Theme_Dark_PurpleDeep, R.drawable.theme_dark_purple_deep),
    Light_PurpleDeep(R.style.KH_Theme_Light_PurpleDeep, R.drawable.theme_light_purple_deep),
    Dark_Indigo(R.style.KH_Theme_Dark_Indigo, R.drawable.theme_dark_indigo),
    Light_Indigo(R.style.KH_Theme_Light_Indigo, R.drawable.theme_light_indigo),
    Dark_Blue(R.style.KH_Theme_Dark_Blue, R.drawable.theme_dark_blue),
    Light_Blue(R.style.KH_Theme_Light_Blue, R.drawable.theme_light_blue),
    Dark_LightBlue(R.style.KH_Theme_Dark_LightBlue, R.drawable.theme_dark_light_blue),
    Light_LightBlue(R.style.KH_Theme_Light_LightBlue, R.drawable.theme_light_light_blue),
    Dark_Cyan(R.style.KH_Theme_Dark_Cyan, R.drawable.theme_dark_cyan),
    Light_Cyan(R.style.KH_Theme_Light_Cyan, R.drawable.theme_light_cyan),
    Dark_Teal(R.style.KH_Theme_Dark_Teal, R.drawable.theme_dark_teal),
    Light_Teal(R.style.KH_Theme_Light_Teal, R.drawable.theme_light_teal),
    Dark_Green(R.style.KH_Theme_Dark_Green, R.drawable.theme_dark_green),
    Light_Green(R.style.KH_Theme_Light_Green, R.drawable.theme_light_green),
    Dark_LightGreen(R.style.KH_Theme_Dark_LightGreen, R.drawable.theme_dark_light_green),
    Light_LightGreen(R.style.KH_Theme_Light_LightGreen, R.drawable.theme_light_light_green),
    Dark_Lime(R.style.KH_Theme_Dark_Lime, R.drawable.theme_dark_lime),
    Light_Lime(R.style.KH_Theme_Light_Lime, R.drawable.theme_light_lime),
    Dark_Yellow(R.style.KH_Theme_Dark_Yellow, R.drawable.theme_dark_yellow),
    Light_Yellow(R.style.KH_Theme_Light_Yellow, R.drawable.theme_light_yellow),
    Dark_Amber(R.style.KH_Theme_Dark_Amber, R.drawable.theme_dark_amber),
    Light_Amber(R.style.KH_Theme_Light_Amber, R.drawable.theme_light_amber),
    Dark_Orange(R.style.KH_Theme_Dark_Orange, R.drawable.theme_dark_orange),
    Light_Orange(R.style.KH_Theme_Light_Orange, R.drawable.theme_light_orange),
    Dark_OrangeDeep(R.style.KH_Theme_Dark_OrangeDeep, R.drawable.theme_dark_orange_deep),
    Light_OrangeDeep(R.style.KH_Theme_Light_OrangeDeep, R.drawable.theme_light_orange_deep),
    Dark_Brown(R.style.KH_Theme_Dark_Brown, R.drawable.theme_dark_brown),
    Light_Brown(R.style.KH_Theme_Light_Brown, R.drawable.theme_light_brown),
    Dark_BlueGrey(R.style.KH_Theme_Dark_BlueGrey, R.drawable.theme_dark_blue_grey),
    Light_BlueGrey(R.style.KH_Theme_Light_BlueGrey, R.drawable.theme_light_blue_grey),
    Dark_Grey(R.style.KH_Theme_Dark_Grey, R.drawable.theme_dark_grey),
    Light_Grey(R.style.KH_Theme_Light_Grey, R.drawable.theme_light_grey);

    private int themeId;
    private int thumbnailId;

    enum_themes(int themeId, int thumbnailId) {
        this.themeId = themeId;
        this.thumbnailId = thumbnailId;
    }

    public int getThemeId() {
        return themeId;
    }

    public int getThumbnailId() {
        return thumbnailId;
    }
}
