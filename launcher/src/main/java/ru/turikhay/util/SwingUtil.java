package ru.turikhay.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.turikhay.tlauncher.ui.TLauncherFrame;
import ru.turikhay.tlauncher.ui.images.Images;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.View;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.Callable;

public class SwingUtil {
    private static final Logger LOGGER = LogManager.getLogger(SwingUtil.class);

    private static final Lazy<List<Image>> favicons = Lazy.of(() -> createFaviconList("logo-tl"));

    public static List<Image> createFaviconList(String iconName) {
        ArrayList<Image> favicons = new ArrayList<>();
        final int[] sizes = new int[]{256, 128, 96, 64, 48, 32, 24, 16};
        for (int size : sizes) {
            Image image = Images.loadIcon(iconName, size);
            if (image != null) {
                favicons.add(image);
            }
        }
        return Collections.unmodifiableList(favicons);
    }

    public static List<Image> getFavicons() {
        return favicons.get();
    }

    public static void setFavicons(JFrame frame) {
        frame.setIconImages(getFavicons());
    }

    private static boolean allowSystemLookAndFeel = true;

    public static boolean initLookAndFeel() {
        if(allowSystemLookAndFeel) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                return true;
            } catch (Exception e) {
                LOGGER.warn("Can't set system L&F", e);
                allowSystemLookAndFeel = false;
                return false;
            }
        }
        return false;
    }

    public static void resetLookAndFeel() {
        allowSystemLookAndFeel = false;
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.warn("Can't set default L&F!", e);
        }
    }

    public static void initFontSize(int defSize) {
        try {
            UIDefaults e = UIManager.getDefaults();
            int minSize = defSize;
            int maxSize = defSize + 2;
            Enumeration e1 = e.keys();

            while (e1.hasMoreElements()) {
                Object key = e1.nextElement();
                Object value = e.get(key);
                if (value instanceof Font) {
                    Font font = (Font) value;
                    int size = font.getSize();
                    if (size < minSize) {
                        size = minSize;
                    } else if (size > maxSize) {
                        size = maxSize;
                    }

                    if (value instanceof FontUIResource) {
                        e.put(key, new FontUIResource(font.getName(), font.getStyle(), size));
                    } else {
                        e.put(key, new Font(font.getName(), font.getStyle(), size));
                    }
                }
            }
        } catch (Exception var9) {
            LOGGER.warn("Cannot change font size", var9);
        }

    }

    public static Cursor getCursor(int type) {
        try {
            return Cursor.getPredefinedCursor(type);
        } catch (IllegalArgumentException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static void setFontSize(JComponent comp, float size) {
        comp.setFont(comp.getFont().deriveFont(size));
    }

    public static int magnify(int i) {
        return (int) Math.rint(i * TLauncherFrame.magnifyDimensions);
    }

    public static float magnify(float i) {
        return (float) (i * TLauncherFrame.magnifyDimensions);
    }

    public static Dimension magnify(Dimension d) {
        return new Dimension(magnify(d.width), magnify(d.height));
    }

    public static Insets magnify(Insets i) {
        i.top = magnify(i.top);
        i.left = magnify(i.left);
        i.bottom = magnify(i.bottom);
        i.right = magnify(i.right);
        return i;
    }

    public static boolean isThiner(Dimension d1, Dimension d2) {
        return Math.min(d1.width, d2.width) == d1.width || Math.min(d1.height, d2.height) == d1.height;
    }

    public static Dimension getPrefSize(JComponent component, int width, int height) {
        U.requireNotNull(component, "component");
        View view;

        if(component instanceof JLabel) {
            view = (View) component.getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey);
        } else if(component instanceof JEditorPane) {
            view = ((JEditorPane) component).getUI().getRootView((JEditorPane) component);
        } else {
            throw new IllegalArgumentException();
        }

        view.setSize(width, height);
        return new Dimension((int) Math.ceil(view.getPreferredSpan(View.X_AXIS)), (int) Math.ceil(view.getPreferredSpan(View.Y_AXIS)));
    }

    // when determining for indefinite height - use 0
    public static int getPrefWidth(JComponent component, int height, int step) {
        int minHeight = getPrefHeight(component, Integer.MAX_VALUE), curHeight, width = 0;
        do {
            curHeight = getPrefHeight(component, width += step);
        } while(curHeight >= height && curHeight != minHeight);

        return width;
    }

    // when determining for indefinite width - use Integer.MAX_VALUE
    public static int getPrefHeight(JComponent component, int width) {
        return getPrefSize(component, width, 0).height;
    }

    private static final String DOTS = "...";
    public static String fitString(FontMetrics metrics, char[] chars, int prefWidth) {
        int len, width, offset = -1, dotsWidth = metrics.stringWidth(DOTS);
        if(prefWidth < dotsWidth) {
            return DOTS;
        }

        do {
            offset++;
            len = chars.length - offset;
            width = metrics.charsWidth(chars, 0, len) + (offset > 0? dotsWidth : 0);
            if(width <= dotsWidth) {
                return DOTS;
            }
        } while(width > prefWidth);

        String result = new String(chars, 0, len);
        return offset > 0? result + DOTS : result;
    }

    public static void setClipboard(String text) {
        if (text == null) {
            return;
        }

        try {
            StringSelection stringSelection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, new ClipboardOwner() {
                @Override
                public void lostOwnership(Clipboard clipboard, Transferable contents) {
                }
            });
        } catch (Exception e) {
            LOGGER.warn("Could not copy to clipboard: \"{}\"", text, e);
        }
    }

    private static final String base64s = "data:image/", base64e = ";base64,";

    public static BufferedImage base64ToImage(String source) throws IOException {
        if (!source.startsWith(base64s)) {
            throw new IllegalArgumentException("not a bse64 format");
        }

        int offset = base64s.length();

        if (source.startsWith("png", offset) || source.startsWith("jpg", offset) || source.startsWith("gif")) {
            offset += 3;
        } else if (source.startsWith("jpeg", offset)) {
            offset += 4;
        } else {
            return null;
        }

        if (!source.substring(offset, offset + base64e.length()).equals(base64e)) {
            return null;
        }

        offset += base64e.length();

        return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(source.substring(offset).getBytes(StandardCharsets.UTF_8))));
    }

    public static Image loadImage(String source) throws IOException {
        try {
            return SwingUtil.base64ToImage(source);
        } catch (IllegalArgumentException ignored) {
        }

        try {
            return ImageIO.read(new URL(source));
        } catch(MalformedURLException ignored) {
        }

        return Images.loadIconById(source);
    }

    public static void later(SwingRunnable r) {
        EventQueue.invokeLater(r);
    }

    public static void laterRunnable(Runnable r) {
        later(r::run);
    }

    public static void wait(SwingRunnable r) {
        if (EventQueue.isDispatchThread()) {
            invokeNow(r);
        } else {
            invokeAndWait(r);
        }
    }

    public static <V> V waitAndReturn(Callable<V> callable) {
        Ref<V> ref = new Ref<>();
        wait(() -> ref.setValue(callable.call()));
        return ref.getValue();
    }

    private static void invokeNow(SwingRunnable r) {
        try {
            r.run();
        } catch(SwingRunnableException e) {
            throw new SwingException(e.getCause());
        }
    }

    private static void invokeAndWait(SwingRunnable r) {
        try {
            EventQueue.invokeAndWait(r);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException invocationTargetException) {
            Throwable t;
            if(invocationTargetException.getCause() != null) {
                t = invocationTargetException.getCause();
                if(t instanceof SwingRunnableException) {
                    t = t.getCause();
                }
            } else {
                t = invocationTargetException;
            }
            throw new SwingException(t);
        }
    }

    private static Lazy<Double> SCALING_FACTOR = Lazy.of(SwingUtil::queryScalingFactor);

    public static double getScalingFactor() {
        return SCALING_FACTOR.value().orElse(1.0);
    }

    private static double queryScalingFactor() throws Exception {
        try {
            GraphicsDevice graphicsDevice = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice();
            GraphicsConfiguration graphicsConfig = graphicsDevice
                    .getDefaultConfiguration();

            AffineTransform tx = graphicsConfig.getDefaultTransform();
            double scaleX = tx.getScaleX();
            double scaleY = tx.getScaleY();

            return Math.max(scaleX, scaleY);
        } catch(NoClassDefFoundError | NoSuchMethodError t) {
            throw new Exception(t);
        }
    }

}
