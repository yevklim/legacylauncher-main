package ru.turikhay.tlauncher.ui.frames;

import ru.turikhay.tlauncher.configuration.LangConfiguration;
import ru.turikhay.tlauncher.ui.converter.LocaleConverter;
import ru.turikhay.tlauncher.ui.loc.LocalizableButton;
import ru.turikhay.tlauncher.ui.support.ContributorsAlert;
import ru.turikhay.tlauncher.ui.swing.extended.ExtendedComboBox;
import ru.turikhay.tlauncher.ui.swing.extended.ExtendedPanel;
import ru.turikhay.util.SwingUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;

public class UpdateFrame extends VActionFrame {

    public UpdateFrame(String version, String changelog) {
        setTitlePath("update.title");
        getHead().setText("update.head", version);

        if(changelog == null) {
            getBodyText().setText("update.body.no-text");
        } else {
            getBodyText().setText(changelog);
        }

        getFooter().setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = -1;

        c.gridx++;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        getFooter().add(new ExtendedPanel(), c);

        c.gridx++;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        getFooter().add(new ExtendedPanel(), c);

        c.gridx++;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.NONE;
        LocalizableButton yesButton = new LocalizableButton("update.footer.continue");
        yesButton.setPreferredSize(SwingUtil.magnify(new Dimension(150, 40)));
        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        getFooter().add(yesButton, c);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        updateLocale();
        pack();
    }
}
