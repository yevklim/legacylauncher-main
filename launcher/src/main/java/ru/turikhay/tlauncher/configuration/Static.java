package ru.turikhay.tlauncher.configuration;

import ru.turikhay.util.U;

import java.util.ArrayList;

public final class Static {
    private static final String SETTINGS = "tlauncher/"+ (isLegacy()? "legacy" : "@tl_short_brand@") +".properties";
    private static final String BRAND = "@tl_brand@";
    private static final String SHORT_BRAND = "@tl_short_brand@";
    private static final String FOLDER = "minecraft";
    private static final String[] OFFICIAL_REPO = {"https://s3.amazonaws.com/Minecraft.Download/"};
    private static final String[] EXTRA_REPO = U.shuffle("https://tln4.ru/repo/", "https://repo.tlaun.ch/repo/", "https://tlauncherrepo.com/repo/");
    private static final String[] LIBRARY_REPO = {"https://libraries.minecraft.net/", "https://tln4.ru/repo/libraries/", "https://repo.tlaun.ch/repo/libraries/", "https://tlauncherrepo.com/repo/libraries/"};
    private static final String[] ASSETS_REPO;
    static {
        ArrayList<String> assets = new ArrayList<>(3);
        assets.add("https://resources.download.mcproxy.tlaun.ch/");
        assets.add("https://resources.download.mcproxy.tln4.ru/");
        U.shuffle(assets);
        assets.add(0, "https://resources.download.minecraft.net/");
        ASSETS_REPO = assets.toArray(new String[0]);
    }
    private static final String[] SERVER_LIST = {};
    private static final String[] LANG_LIST = new String[]{"en_US", "ru_RU", "uk_UA", "pt_BR", "vi", "tr_TR", "fr_FR", "id_ID", "pl_PL", "it_IT", "de_DE", "ro_RO", "zh_CN"};

    private static boolean isLegacy() {
        return getShortBrand().startsWith("legacy");
    }

    public static String getSettings() {
        return SETTINGS;
    }

    public static String getBrand() {
        return BRAND;
    }

    public static String getShortBrand() {
        return SHORT_BRAND;
    }

    public static String getFolder() {
        return FOLDER;
    }

    public static String[] getOfficialRepo() {
        return OFFICIAL_REPO;
    }

    public static String[] getExtraRepo() {
        return EXTRA_REPO;
    }

    public static String[] getLibraryRepo() {
        return LIBRARY_REPO;
    }

    public static String[] getAssetsRepo() {
        return ASSETS_REPO;
    }

    public static String[] getServerList() {
        return SERVER_LIST;
    }

    public static String[] getLangList() {
        return LANG_LIST;
    }

    private Static() {
    }
}
