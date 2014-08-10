package ru.turikhay.tlauncher.ui.editor;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusListener;
import javax.swing.JComponent;
import ru.turikhay.tlauncher.ui.block.Blocker;

public class EditorFieldHandler extends EditorHandler {
   private final EditorField field;
   private final JComponent comp;

   public EditorFieldHandler(String path, JComponent component, FocusListener focus) {
      super(path);
      if (component == null) {
         throw new NullPointerException("comp");
      } else if (!(component instanceof EditorField)) {
         throw new IllegalArgumentException();
      } else {
         if (focus != null) {
            this.addFocus(component, focus);
         }

         this.comp = component;
         this.field = (EditorField)component;
      }
   }

   public EditorFieldHandler(String path, JComponent comp) {
      this(path, comp, (FocusListener)null);
   }

   public JComponent getComponent() {
      return this.comp;
   }

   public String getValue() {
      return this.field.getSettingsValue();
   }

   protected void setValue0(String s) {
      this.field.setSettingsValue(s);
   }

   public boolean isValid() {
      return this.field.isValueValid();
   }

   private void addFocus(Component comp, FocusListener focus) {
      comp.addFocusListener(focus);
      if (comp instanceof Container) {
         Component[] var6;
         int var5 = (var6 = ((Container)comp).getComponents()).length;

         for(int var4 = 0; var4 < var5; ++var4) {
            Component curComp = var6[var4];
            this.addFocus(curComp, focus);
         }
      }

   }

   public void block(Object reason) {
      Blocker.blockComponents(reason, this.getComponent());
   }

   public void unblock(Object reason) {
      Blocker.unblockComponents(reason, this.getComponent());
   }
}
