package ru.turikhay.tlauncher.ui.frames;

import ru.turikhay.tlauncher.TLauncher;
import ru.turikhay.tlauncher.managers.ProfileManager;
import ru.turikhay.tlauncher.ui.alert.Alert;
import ru.turikhay.tlauncher.ui.editor.EditorFileField;
import ru.turikhay.tlauncher.ui.explorer.FileExplorer;
import ru.turikhay.tlauncher.ui.loc.LocalizableButton;
import ru.turikhay.tlauncher.ui.loc.LocalizableLabel;
import ru.turikhay.tlauncher.ui.swing.extended.ExtendedPanel;
import ru.turikhay.util.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class NewFolderFrame extends VActionFrame {
    private final TLauncher t;

    public NewFolderFrame(final TLauncher t, File file) {
        super(SwingUtil.magnify(500));

        this.t = t;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitlePath("newfolder.title");
        getHead().setText("newfolder.head");
        getBodyText().setText("newfolder.body");

        FileExplorer dirExplorer;
        try {
            dirExplorer = FileExplorer.newExplorer();
            dirExplorer.setFileSelectionMode(FileExplorer.DIRECTORIES_ONLY);
            dirExplorer.setFileHidingEnabled(false);
        } catch (Exception e) {
            dirExplorer = null;
        }

        GridBagConstraints c;

        final EditorFileField fileField = new EditorFileField("newfolder.select.prompt", "newfolder.select.browse", dirExplorer, false, false);
        fileField.setSettingsValue(file.getAbsolutePath());
        ExtendedPanel fileFieldShell = new ExtendedPanel();
        fileFieldShell.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        fileFieldShell.add(new LocalizableLabel("newfolder.select.title"), c);
        ++c.gridy;
        fileFieldShell.add(fileField, c);
        getBody().add(fileFieldShell);

        getFooter().setLayout(new GridBagLayout());

        c = new GridBagConstraints();
        c.gridx = -1;
        c.gridx++;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.VERTICAL;
        LocalizableButton cancelButton = new LocalizableButton("newfolder.button.cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        getFooter().add(cancelButton, c);

        c.gridx++;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        getFooter().add(new ExtendedPanel(), c);

        c.gridx++;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.VERTICAL;
        final LocalizableButton okButton = new LocalizableButton("newfolder.button.ok");
        okButton.setPreferredSize(SwingUtil.magnify(new Dimension(150, 40)));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeFolder(fileField.isValueValid() ? fileField.getSelectedFile() : null);
            }
        });
        getFooter().add(okButton, c);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                okButton.requestFocus();
            }
        });

        pack();
    }

    private void changeFolder(File folder) {
        if (folder == null) {
            Alert.showLocError("newfolder.select.error");
            return;
        }

        File currentFolder = MinecraftUtil.getWorkingDirectory();
        if (folder.getAbsolutePath().startsWith(currentFolder.getAbsolutePath())) {
            Alert.showLocError("newfolder.select.error.title", "newfolder.select.error.inside", null);
            return;
        }

        t.getSettings().set("minecraft.gamedir", folder.getAbsolutePath());
        U.log("Changed folder with NewFolderFrame:", folder);
        dispose();
    }

    public static boolean shouldWeMoveFrom(File currentDir) {
        if (currentDir == null) {
            return true;
        }

        if (!currentDir.isDirectory()) {
            try {
                FileUtil.createFolder(currentDir);
            } catch (Exception e) {
                U.log(currentDir, "is not accessible");
                currentDir.delete();
                return true;
            }
            return false;
        }

        if (!(currentDir.canRead() && currentDir.canWrite() && currentDir.canExecute())) {
            U.log(currentDir, "is not readable/writable/executable");
            return true;
        }

        File[] list = currentDir.listFiles();

        if (list == null) {
            U.log(currentDir, "has null listing?!");
            return true;
        }

        if (list.length == 0) {
            return false;
        }

        File profileFile = new File(currentDir, ProfileManager.DEFAULT_PROFILE_FILENAME);
        U.log(currentDir, "has profile file:", profileFile.isFile());
        return !profileFile.isFile();
    }

    public static File selectDestination() {
        ArrayList<File> suggestions = new ArrayList<File>();

        if (OS.WINDOWS.isCurrent()) {
            suggestions.addAll(Arrays.asList(
                    new File("D:\\Games\\Minecraft"),
                    new File("C:\\Games\\Minecraft")
            ));
        }

        suggestions.addAll(Arrays.asList(
                MinecraftUtil.getSystemRelatedDirectory("minecraft")
        ));

        suggestions.addAll(Arrays.asList(
                MinecraftUtil.getSystemRelatedDirectory("Minecraft", false),
                MinecraftUtil.getSystemRelatedDirectory("tlauncher/" + TLauncher.getBrand())
        ));

        for (File suggestion : suggestions) {
            if (!shouldWeMoveFrom(suggestion)) {
                return suggestion;
            }
        }

        return null;
    }
}
