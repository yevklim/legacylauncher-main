package ru.turikhay.tlauncher.ui.notice;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import ru.turikhay.tlauncher.ui.images.Images;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.Future;

public abstract class NoticeImage {
    public static NoticeImage DEFAULT_IMAGE = new DirectNoticeImage(Images.loadIcon64("logo-tl"));
    static HashMap<String, NoticeImage> definedImages = new HashMap<String, NoticeImage>(){
        {
            put("default", DEFAULT_IMAGE);
            put("youtube", new DirectNoticeImage(Images.loadIcon64("logo-youtube")));
        }
    };

    NoticeImage() {
    }

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract Future<Image> getTask();

    protected ToStringBuilder toStringBuilder() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public final String toString() {
        return toStringBuilder().build();
    }
}
