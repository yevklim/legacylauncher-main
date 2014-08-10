package ru.turikhay.tlauncher.ui.versions;

import java.awt.Component;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JList;
import net.minecraft.launcher.updater.VersionSyncInfo;
import ru.turikhay.tlauncher.ui.images.ImageCache;
import ru.turikhay.tlauncher.ui.images.ImageIcon;
import ru.turikhay.tlauncher.ui.loc.Localizable;
import ru.turikhay.tlauncher.ui.swing.VersionCellRenderer;

public class VersionListCellRenderer extends VersionCellRenderer {
   private final VersionHandler handler;
   private final ImageIcon downloading;

   VersionListCellRenderer(VersionList list) {
      this.handler = list.handler;
      this.downloading = ImageCache.getIcon("down.png", 16, 16);
   }

   public Component getListCellRendererComponent(JList list, VersionSyncInfo value, int index, boolean isSelected, boolean cellHasFocus) {
      JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (value.isInstalled() && !value.isUpToDate()) {
         label.setText(label.getText() + ' ' + Localizable.get("version.list.needsupdate"));
      }

      if (this.handler.downloading != null) {
         Iterator var8 = this.handler.downloading.iterator();

         while(var8.hasNext()) {
            VersionSyncInfo compare = (VersionSyncInfo)var8.next();
            if (compare.equals(value)) {
               label.setIcon(this.downloading);
               label.setDisabledIcon(this.downloading);
               break;
            }
         }
      }

      return label;
   }
}
