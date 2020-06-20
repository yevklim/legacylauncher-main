package ru.turikhay.tlauncher.bootstrap.launcher;

import ru.turikhay.tlauncher.bootstrap.json.ToStringBuildable;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import ru.turikhay.tlauncher.bootstrap.meta.LauncherMeta;
import ru.turikhay.tlauncher.bootstrap.util.U;

import java.io.IOException;

public abstract class Launcher extends ToStringBuildable {

    protected Launcher() {
    }

    public abstract LauncherMeta getMeta() throws IOException, JsonSyntaxException;

    private final String logPrefix = '[' + getClass().getSimpleName() + ']';
    protected void log(String s) {
        U.log(logPrefix, s);
    }
}
