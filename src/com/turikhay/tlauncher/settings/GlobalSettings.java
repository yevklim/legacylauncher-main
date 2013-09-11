package com.turikhay.tlauncher.settings;

import com.turikhay.tlauncher.util.MinecraftUtil;
import com.turikhay.tlauncher.util.U;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class GlobalSettings extends Settings {
   public static final File file = MinecraftUtil.getNativeOptionsFile();
   public static final boolean firstRun;
   private Map d = new HashMap();
   private double version = 0.12D;

   static {
      firstRun = !file.exists();
   }

   public GlobalSettings() throws IOException {
      super(file);
      this.d.put("settings.version", this.version);
      this.d.put("login.auto", false);
      this.d.put("login.auto.timeout", 3);
      this.d.put("minecraft.size.width", 925);
      this.d.put("minecraft.size.height", 525);
      this.d.put("minecraft.versions.snapshots", true);
      this.d.put("minecraft.versions.beta", true);
      this.d.put("minecraft.versions.alpha", true);
      this.d.put("gui.sun", true);
      boolean forcedrepair = this.getDouble("settings.version") != this.version;
      Iterator var3 = this.d.entrySet().iterator();

      while(true) {
         while(var3.hasNext()) {
            Entry curen = (Entry)var3.next();
            String key = (String)curen.getKey();
            String value = (String)this.s.get(key);
            Object defvalue = this.d.get(key);
            if (!forcedrepair && value != null) {
               try {
                  if (defvalue instanceof Integer) {
                     Integer.parseInt(value);
                  } else if (defvalue instanceof Boolean) {
                     this.parseBoolean(value);
                  } else if (defvalue instanceof Double) {
                     Double.parseDouble(value);
                  } else if (defvalue instanceof Long) {
                     Long.parseLong(value);
                  }
               } catch (Exception var8) {
                  this.repair(key, defvalue);
               }
            } else {
               this.repair(key, defvalue);
            }
         }

         this.save();
         return;
      }
   }

   public String get(String key) {
      String r = (String)this.s.get(key);
      return r == "" ? null : r;
   }

   public String getDefault(String key) {
      String r = "" + this.d.get(key);
      return r == "" ? null : r;
   }

   public int getDefaultInteger(String key) {
      try {
         return Integer.parseInt("" + this.d.get(key));
      } catch (Exception var3) {
         return 0;
      }
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

   private boolean parseBoolean(String b) throws Exception {
      switch(b.hashCode()) {
      case 3569038:
         if (b.equals("true")) {
            return true;
         }
         break;
      case 97196323:
         if (b.equals("false")) {
            return false;
         }
      }

      throw new Exception();
   }

   private void repair(String key, Object value) throws IOException {
      U.log("Field \"" + key + "\" in GlobalSettings is invalid.");
      this.set(key, value, false);
   }
}
