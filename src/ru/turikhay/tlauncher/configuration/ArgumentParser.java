package ru.turikhay.tlauncher.configuration;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import ru.turikhay.tlauncher.ui.alert.Alert;

public class ArgumentParser {
   private static final Map m = new HashMap();
   private static final OptionParser parser;

   public static String getHelp() {
      StringWriter writer = new StringWriter();

      try {
         parser.printHelpOn(writer);
      } catch (IOException var2) {
      }

      writer.flush();
      return writer.toString();
   }

   public static OptionSet parseArgs(String[] args) throws OptionException, IOException {
      try {
         return parser.parse(args);
      } catch (OptionException var2) {
         Alert.showLocError("args.error", var2);
         throw var2;
      }
   }

   public static Map parse(OptionSet set) {
      HashMap r = new HashMap();
      if (set == null) {
         return r;
      } else {
         Iterator var3 = m.entrySet().iterator();

         while(var3.hasNext()) {
            Entry a = (Entry)var3.next();
            String key = (String)a.getKey();
            Object value = null;
            if (key.startsWith("-")) {
               key = key.substring(1);
               value = true;
            }

            if (set.has(key)) {
               if (value == null) {
                  value = set.valueOf(key);
               }

               r.put(a.getValue(), value);
            }
         }

         return r;
      }
   }

   static {
      m.put("directory", "minecraft.gamedir");
      m.put("profiles", "profiles");
      m.put("java-directory", "minecraft.javadir");
      m.put("version", "login.version");
      m.put("username", "login.account");
      m.put("usertype", "login.account.type");
      m.put("javaargs", "minecraft.javaargs");
      m.put("margs", "minecraft.args");
      m.put("window", "minecraft.size");
      m.put("background", "gui.background");
      m.put("fullscreen", "minecraft.fullscreen");
      m.put("-block-settings", "gui.settings.blocked");
      parser = new OptionParser();
      parser.accepts("help", "Prints help");
      parser.accepts("no-terminate", "Do not terminate Bootstrapper if started with it");
      parser.accepts("directory", "Specifies Minecraft directory").withRequiredArg();
      parser.accepts("profiles", "Specifies profile file").withRequiredArg();
      parser.accepts("java-directory", "Specifies Java directory").withRequiredArg();
      parser.accepts("version", "Specifies version to run").withRequiredArg();
      parser.accepts("username", "Specifies username").withRequiredArg();
      parser.accepts("usertype", "Specifies user type (if multiple with the same name)").withRequiredArg();
      parser.accepts("javaargs", "Specifies JVM arguments").withRequiredArg();
      parser.accepts("margs", "Specifies Minecraft arguments").withRequiredArg();
      parser.accepts("window", "Specifies window size in format: width;height").withRequiredArg();
      parser.accepts("settings", "Specifies path to settings file").withRequiredArg();
      parser.accepts("background", "Specifies background image. URL links, JPEG and PNG formats are supported.").withRequiredArg();
      parser.accepts("fullscreen", "Specifies whether fullscreen mode enabled or not").withRequiredArg();
      parser.accepts("block-settings", "Disables settings and folder buttons");
   }
}
