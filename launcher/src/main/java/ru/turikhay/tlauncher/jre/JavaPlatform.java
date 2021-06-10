package ru.turikhay.tlauncher.jre;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.turikhay.util.OS;

import java.util.Objects;

public class JavaPlatform {
    private static final Logger LOGGER = LogManager.getLogger(JavaPlatform.class);

    public static final String CURRENT_PLATFORM = getCurrentPlatform();

    private static String getCurrentPlatform() {
        boolean is64bit = OS.Arch.x64.isCurrent();

        switch (OS.CURRENT) {
            case LINUX:
                return is64bit ? "linux" : "linux-i386";
            case WINDOWS:
                return is64bit ? "windows-x64" : "windows-x86";
            case OSX:
                if(!is64bit) {
                    LOGGER.warn("macOS x86 is not supported. How old is this computer?");
                    return null;
                }
                return "mac-os";
            default:
                LOGGER.warn("Current platform is unknown: {}", OS.CURRENT);
                return null;
        }
    }

    public static OS getOSByPlatform(String platform) {
        Objects.requireNonNull(platform, "platform");

        if(platform.startsWith("linux")) {
            return OS.LINUX;
        } else if(platform.startsWith("windows")) {
            return OS.WINDOWS;
        } else if(platform.equals("mac-os")) {
            return OS.OSX;
        } else {
            throw new IllegalArgumentException("unknown platform: " + platform);
        }
    }
}
