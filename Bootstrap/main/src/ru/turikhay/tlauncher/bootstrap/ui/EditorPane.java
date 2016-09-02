package ru.turikhay.tlauncher.bootstrap.ui;

import ru.turikhay.tlauncher.bootstrap.util.OS;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.net.URL;

public class EditorPane extends JEditorPane {
    private final String textColor;

    {
        Color color = new JLabel().getForeground();
        textColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public EditorPane(Font font) {
        if (font != null) {
            setFont(font);
        } else {
            font = getFont();
        }

        StyleSheet css = new StyleSheet();
        css.importStyleSheet(getClass().getResource("styles.css"));
        css.addRule("body { font-family: " + font.getFamily() + "; font-size: " + font.getSize() + "pt; color: " + textColor + "; } " + "a { color: " + textColor + "; text-decoration: underline; }");
        HTMLEditorKit html = new HTMLEditorKit();
        html.setStyleSheet(css);
        getDocument().putProperty("IgnoreCharsetDirective", Boolean.TRUE);
        setMargin(new Insets(0, 0, 0, 0));
        setEditorKit(html);
        setEditable(false);
        setOpaque(false);
        addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    URL url = e.getURL();
                    if (url != null) {
                        OS.openUrl(url);
                    }
                }
            }
        });
    }

    private EditorPane() {
        this(new JLabel().getFont());
    }

    public EditorPane(String type, String text) {
        this();
        setContentType(type);
        setText(text);
    }
}
