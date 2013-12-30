package com.turikhay.tlauncher.ui;

import com.turikhay.tlauncher.settings.Settings;
import javax.swing.JButton;

public class LocalizableButton extends JButton implements LocalizableComponent {
   private static final long serialVersionUID = 1L;
   static Settings l;
   private String path;
   private String r0;
   private String r1;
   private Object w0;
   private Object w1;

   public LocalizableButton() {
      this.init();
   }

   public LocalizableButton(String path) {
      this();
      this.setLabel(path);
   }

   public LocalizableButton(String path, String replace, Object with) {
      this();
      this.setLabel(path, replace, with);
   }

   public void setLabel(String path) {
      this.path = path;
      super.setText(l == null ? path : l.get(path));
   }

   public void setLabel(String path, String replace, Object with) {
      this.path = path;
      this.u();
      this.r0 = replace;
      this.w0 = with;
      super.setText(l == null ? path : l.get(path, replace, with));
   }

   public void setLabel(String path, String replace0, Object with0, String replace1, Object with1) {
      this.path = path;
      this.u();
      this.r0 = replace0;
      this.w0 = with0;
      this.r1 = replace1;
      this.w1 = with1;
      super.setText(l == null ? path : l.get(path, replace0, with0, replace1, with1));
   }

   public String getLangPath() {
      return this.path;
   }

   public void updateLocale() {
      this.setLabel(this.path, this.r0, this.w0, this.r1, this.w1);
   }

   private void u() {
      this.r0 = this.r1 = null;
      this.w0 = this.w1 = null;
   }

   private void init() {
      this.setOpaque(false);
   }
}
