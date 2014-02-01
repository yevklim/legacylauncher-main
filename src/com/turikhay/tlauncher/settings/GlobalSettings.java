package com.turikhay.tlauncher.settings;

import com.turikhay.tlauncher.TLauncher;
import com.turikhay.util.FileUtil;
import com.turikhay.util.IntegerArray;
import com.turikhay.util.MinecraftUtil;
import com.turikhay.util.U;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import joptsimple.OptionSet;

public class GlobalSettings extends Settings {
   private static final Pattern lang_pattern = Pattern.compile("lang/([\\w]+)\\.ini");
   public static final Locale DEFAULT_LOCALE;
   public static final List DEFAULT_LOCALES;
   public static final List SUPPORTED_LOCALE;
   private File file;
   private boolean saveable;
   private boolean staticProfile;
   private static boolean firstRun;
   private GlobalDefaults d;
   private Map cs = new HashMap();
   double version = 0.15D;

   static {
      DEFAULT_LOCALE = Locale.US;
      DEFAULT_LOCALES = getDefaultLocales();
      SUPPORTED_LOCALE = getSupportedLocales();
   }

   public static GlobalSettings createInstance() throws IOException {
      return createInstance((OptionSet)null);
   }

   public static GlobalSettings createInstance(OptionSet set) throws IOException {
      Object path = set != null ? set.valueOf("settings") : null;
      if (path == null) {
         URL resource = GlobalSettings.class.getResource("/settings.ini");
         if (resource != null) {
            return new GlobalSettings(resource, set);
         }
      }

      File file = path == null ? getDefaultFile() : new File(path.toString());
      if (!file.exists()) {
         firstRun = true;
      }

      return new GlobalSettings(file, set);
   }

   private GlobalSettings(URL url, OptionSet set) throws IOException {
      super(url);
      U.log("Settings URL:", url);
      this.init(set);
   }

   private GlobalSettings(File file, OptionSet set) throws IOException {
      super(file);
      U.log("Settings file:", file);
      this.init(set);
   }

   private void init(OptionSet set) throws IOException {
      this.d = new GlobalDefaults(this);
      this.cs = ArgumentParser.parse(set);
      if (this.cs.containsKey("minecraft.gamedir")) {
         this.staticProfile = true;
      }

      boolean forcedrepair = this.getDouble("settings.version") != this.version;
      this.saveable = this.input instanceof File;
      if (!this.saveable) {
         this.staticProfile = true;
      }

      Iterator var4 = this.d.getMap().entrySet().iterator();

      while(var4.hasNext()) {
         Entry curen = (Entry)var4.next();
         String key = (String)curen.getKey();
         String value = (String)this.s.get(key);
         Object defvalue = this.d.get(key);
         if (defvalue != null) {
            try {
               if (forcedrepair) {
                  throw new Exception();
               }

               if (defvalue instanceof Integer) {
                  Integer.parseInt(value);
               } else if (defvalue instanceof Boolean) {
                  this.parseBoolean(value);
               } else if (defvalue instanceof Double) {
                  Double.parseDouble(value);
               } else if (defvalue instanceof Long) {
                  Long.parseLong(value);
               } else if (defvalue instanceof GlobalSettings.ActionOnLaunch) {
                  this.parseLaunchAction(value);
               } else if (defvalue instanceof IntegerArray) {
                  IntegerArray.parseIntegerArray(value);
               } else if (defvalue instanceof GlobalSettings.ConsoleType) {
                  this.parseConsoleType(value);
               } else if (defvalue instanceof GlobalSettings.ConnectionQuality) {
                  this.parseConnectionQuality(value);
               } else if (defvalue instanceof File) {
                  this.parseFile(value);
               }
            } catch (Exception var9) {
               this.repair(key, defvalue, !this.saveable);
               value = defvalue.toString();
            }

            if (!this.saveable) {
               this.cs.put(key, value);
            }
         }
      }

      if (this.saveable) {
         this.save();
      }

   }

   public boolean isFirstRun() {
      return firstRun;
   }

   public boolean isStaticProfile() {
      return this.staticProfile;
   }

   public String get(String key) {
      Object r = this.cs.containsKey(key) ? this.cs.get(key) : this.s.get(key);
      return r != null && !r.equals("") ? r.toString() : null;
   }

   public String getD(String key) {
      Object r = this.cs.containsKey(key) ? this.cs.get(key) : this.s.get(key);
      return r != null && !r.equals("") ? r.toString() : this.getDefault(key);
   }

   public void set(String key, Object value, boolean save) {
      if (!this.cs.containsKey(key)) {
         super.set(key, value, save);
      } else {
         if (value == null) {
            value = "";
         }

         this.cs.remove(key);
         this.cs.put(key, value.toString());
         if (save) {
            try {
               this.save();
            } catch (IOException var5) {
               throw new SettingsException(this, "Cannot save set value!", var5);
            }
         }

      }
   }

   public boolean isSaveable(String key) {
      return !this.cs.containsKey(key);
   }

   public boolean isSaveable() {
      return this.saveable;
   }

   public String getDefault(String key) {
      Object r = this.d.get(key);
      if (r == null) {
         return null;
      } else {
         String s = r.toString();
         return s.isEmpty() ? null : s;
      }
   }

   public int getDefaultInteger(String key) {
      try {
         return Integer.parseInt("" + this.d.get(key));
      } catch (Exception var3) {
         return 0;
      }
   }

   public int getInteger(String key, int min, int max) {
      int i = this.getInteger(key);
      return i >= min && i <= max ? i : this.getDefaultInteger(key);
   }

   public int getInteger(String key, int min) {
      return this.getInteger(key, min, Integer.MAX_VALUE);
   }

   public long getDefaultLong(String key) {
      try {
         return Long.parseLong("" + this.d.get(key));
      } catch (Exception var3) {
         return 0L;
      }
   }

   public double getDefaultDouble(String key) {
      try {
         return Double.parseDouble("" + this.d.get(key));
      } catch (Exception var3) {
         return 0.0D;
      }
   }

   public float getDefaultFloat(String key) {
      try {
         return Float.parseFloat("" + this.d.get(key));
      } catch (Exception var3) {
         return 0.0F;
      }
   }

   public boolean getDefaultBoolean(String key) {
      try {
         return Boolean.parseBoolean("" + this.d.get(key));
      } catch (Exception var3) {
         return false;
      }
   }

   public Locale getLocale() {
      Locale locale = getLocale(this.get("locale"));
      return locale != null && SUPPORTED_LOCALE.contains(locale) ? locale : DEFAULT_LOCALE;
   }

   public static Locale getSupported() {
      Locale using = Locale.getDefault();
      return SUPPORTED_LOCALE.contains(using) ? using : Locale.US;
   }

   public GlobalSettings.ActionOnLaunch getActionOnLaunch() {
      String action = this.get("minecraft.onlaunch");
      GlobalSettings.ActionOnLaunch get = GlobalSettings.ActionOnLaunch.get(action);
      return get != null ? get : GlobalSettings.ActionOnLaunch.getDefault();
   }

   public GlobalSettings.ConsoleType getConsoleType() {
      String type = this.get("gui.console");
      GlobalSettings.ConsoleType get = GlobalSettings.ConsoleType.get(type);
      return get != null ? get : GlobalSettings.ConsoleType.getDefault();
   }

   public GlobalSettings.ConnectionQuality getConnectionQuality() {
      String quality = this.get("connection");
      GlobalSettings.ConnectionQuality get = GlobalSettings.ConnectionQuality.get(quality);
      return get != null ? get : GlobalSettings.ConnectionQuality.getDefault();
   }

   private boolean parseBoolean(String b) throws Exception {
      if (b.equalsIgnoreCase("true")) {
         return true;
      } else if (b.equalsIgnoreCase("false")) {
         return false;
      } else {
         throw new Exception();
      }
   }

   private void parseLaunchAction(String b) throws Exception {
      if (!GlobalSettings.ActionOnLaunch.parse(b)) {
         throw new Exception();
      }
   }

   private void parseConsoleType(String b) throws Exception {
      if (!GlobalSettings.ConsoleType.parse(b)) {
         throw new Exception();
      }
   }

   private void parseConnectionQuality(String b) throws Exception {
      if (!GlobalSettings.ConnectionQuality.parse(b)) {
         throw new Exception();
      }
   }

   private void parseFile(String s) throws Exception {
      if (!(new File(s)).canRead()) {
         throw new Exception();
      }
   }

   private void repair(String key, Object value, boolean unsaveable) throws IOException {
      U.log("Field \"" + key + "\" in GlobalSettings is invalid.");
      this.set(key, value.toString(), false);
      if (unsaveable) {
         this.cs.put(key, value);
      }

   }

   public static File getDefaultFile() {
      return MinecraftUtil.getSystemRelatedFile(TLauncher.getSettingsFile());
   }

   public File getFile() {
      return this.file == null ? getDefaultFile() : this.file;
   }

   public void setFile(File f) {
      if (f == null) {
         throw new IllegalArgumentException("File cannot be NULL!");
      } else {
         U.log("Set settings file: " + f.toString());
         this.file = f;
      }
   }

   public void save() throws IOException {
      if (this.input instanceof File) {
         File file = (File)this.input;
         StringBuilder r = new StringBuilder();
         boolean first = true;
         synchronized(this.s) {
            String key;
            Object value;
            for(Iterator var6 = this.s.entrySet().iterator(); var6.hasNext(); r.append(key + this.DELIMITER_CHAR + value.toString().replace(this.NEWLINE_CHAR, "\\" + this.NEWLINE_CHAR))) {
               Entry curen = (Entry)var6.next();
               key = (String)curen.getKey();
               value = curen.getValue();
               if (value == null) {
                  value = "";
               }

               if (!first) {
                  r.append(this.NEWLINE_CHAR);
               } else {
                  first = false;
               }
            }

            String towrite = r.toString();
            FileOutputStream os = new FileOutputStream(file);
            OutputStreamWriter ow = new OutputStreamWriter(os, "UTF-8");
            ow.write(towrite);
            ow.close();
         }
      }
   }

   public int[] getWindowSize() {
      int[] d_sizes = new int[]{925, 530};

      int[] w_sizes;
      try {
         w_sizes = IntegerArray.parseIntegerArray(this.get("minecraft.size")).toArray();
      } catch (Exception var4) {
         w_sizes = d_sizes;
      }

      if (w_sizes[0] < d_sizes[0] || w_sizes[1] < d_sizes[1]) {
         w_sizes = d_sizes;
      }

      return w_sizes;
   }

   public static List getSupportedLocales() {
      U.log("Searching for supported locales...");
      File file = FileUtil.getRunningJar();
      ArrayList locales = new ArrayList();

      try {
         URL jar = file.toURI().toURL();
         ZipInputStream zip = new ZipInputStream(jar.openStream());

         while(true) {
            ZipEntry e = zip.getNextEntry();
            if (e == null) {
               return (List)(locales.isEmpty() ? DEFAULT_LOCALES : locales);
            }

            String name = e.getName();
            if (name.startsWith("lang/")) {
               Matcher mt = lang_pattern.matcher(name);
               if (mt.matches()) {
                  U.log("Found locale:", mt.group(1));
                  locales.add(getLocale(mt.group(1)));
               }
            }
         }
      } catch (Exception var7) {
         U.log("Cannot get locales!", var7);
         return DEFAULT_LOCALES;
      }
   }

   public static Locale getLocale(String locale) {
      if (locale == null) {
         return null;
      } else {
         Locale[] var4;
         int var3 = (var4 = Locale.getAvailableLocales()).length;

         for(int var2 = 0; var2 < var3; ++var2) {
            Locale cur = var4[var2];
            if (cur.toString().equals(locale)) {
               return cur;
            }
         }

         return null;
      }
   }

   private static List getDefaultLocales() {
      List l = new ArrayList();
      l.add(getLocale("en_US"));
      l.add(getLocale("ru_RU"));
      l.add(getLocale("uk_UA"));
      return l;
   }

   public static enum ActionOnLaunch {
      HIDE,
      EXIT;

      public static boolean parse(String val) {
         if (val == null) {
            return false;
         } else {
            GlobalSettings.ActionOnLaunch[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
               GlobalSettings.ActionOnLaunch cur = var4[var2];
               if (cur.toString().equalsIgnoreCase(val)) {
                  return true;
               }
            }

            return false;
         }
      }

      public static GlobalSettings.ActionOnLaunch get(String val) {
         GlobalSettings.ActionOnLaunch[] var4;
         int var3 = (var4 = values()).length;

         for(int var2 = 0; var2 < var3; ++var2) {
            GlobalSettings.ActionOnLaunch cur = var4[var2];
            if (cur.toString().equalsIgnoreCase(val)) {
               return cur;
            }
         }

         return null;
      }

      public String toString() {
         return super.toString().toLowerCase();
      }

      public static GlobalSettings.ActionOnLaunch getDefault() {
         return HIDE;
      }
   }

   public static enum ConnectionQuality {
      GOOD(2, 5, 10, 15000),
      NORMAL(5, 10, 5, 45000),
      BAD(10, 20, 1, 120000);

      private final int minTries;
      private final int maxTries;
      private final int maxThreads;
      private final int timeout;
      private final int[] configuration;

      private ConnectionQuality(int minTries, int maxTries, int maxThreads, int timeout) {
         this.minTries = minTries;
         this.maxTries = maxTries;
         this.maxThreads = maxThreads;
         this.timeout = timeout;
         this.configuration = new int[]{minTries, maxTries, maxThreads};
      }

      public static boolean parse(String val) {
         if (val == null) {
            return false;
         } else {
            GlobalSettings.ConnectionQuality[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
               GlobalSettings.ConnectionQuality cur = var4[var2];
               if (cur.toString().equalsIgnoreCase(val)) {
                  return true;
               }
            }

            return false;
         }
      }

      public static GlobalSettings.ConnectionQuality get(String val) {
         GlobalSettings.ConnectionQuality[] var4;
         int var3 = (var4 = values()).length;

         for(int var2 = 0; var2 < var3; ++var2) {
            GlobalSettings.ConnectionQuality cur = var4[var2];
            if (cur.toString().equalsIgnoreCase(val)) {
               return cur;
            }
         }

         return null;
      }

      public int[] getConfiguration() {
         return this.configuration;
      }

      public int getMinTries() {
         return this.minTries;
      }

      public int getMaxTries() {
         return this.maxTries;
      }

      public int getMaxThreads() {
         return this.maxThreads;
      }

      public int getTimeout() {
         return this.timeout;
      }

      public String toString() {
         return super.toString().toLowerCase();
      }

      public static GlobalSettings.ConnectionQuality getDefault() {
         return GOOD;
      }
   }

   public static enum ConsoleType {
      GLOBAL,
      MINECRAFT,
      NONE;

      public static boolean parse(String val) {
         if (val == null) {
            return false;
         } else {
            GlobalSettings.ConsoleType[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
               GlobalSettings.ConsoleType cur = var4[var2];
               if (cur.toString().equalsIgnoreCase(val)) {
                  return true;
               }
            }

            return false;
         }
      }

      public static GlobalSettings.ConsoleType get(String val) {
         GlobalSettings.ConsoleType[] var4;
         int var3 = (var4 = values()).length;

         for(int var2 = 0; var2 < var3; ++var2) {
            GlobalSettings.ConsoleType cur = var4[var2];
            if (cur.toString().equalsIgnoreCase(val)) {
               return cur;
            }
         }

         return null;
      }

      public String toString() {
         return super.toString().toLowerCase();
      }

      public static GlobalSettings.ConsoleType getDefault() {
         return NONE;
      }
   }
}
