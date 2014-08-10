package ru.turikhay.tlauncher.ui.accounts;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ru.turikhay.tlauncher.minecraft.auth.Account;
import ru.turikhay.tlauncher.ui.center.CenterPanel;
import ru.turikhay.tlauncher.ui.loc.LocalizableButton;
import ru.turikhay.tlauncher.ui.loc.LocalizableCheckbox;
import ru.turikhay.tlauncher.ui.progress.ProgressBar;
import ru.turikhay.tlauncher.ui.scenes.AccountEditorScene;
import ru.turikhay.tlauncher.ui.swing.CheckBoxListener;
import ru.turikhay.tlauncher.ui.text.ExtendedPasswordField;

public class AccountEditor extends CenterPanel {
   private static final long serialVersionUID = 7061277150214976212L;
   private final AccountEditorScene scene;
   public final UsernameField username;
   public final ExtendedPasswordField password;
   public final LocalizableCheckbox premiumBox;
   public final LocalizableButton save;
   private final ProgressBar progressBar;

   public AccountEditor(AccountEditorScene sc) {
      super(squareInsets);
      this.scene = sc;
      ActionListener enterHandler = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AccountEditor.this.defocus();
            AccountEditor.this.scene.handler.saveEditor();
         }
      };
      this.username = new UsernameField(this, UsernameField.UsernameState.USERNAME);
      this.username.addActionListener(enterHandler);
      this.password = new ExtendedPasswordField();
      this.password.addActionListener(enterHandler);
      this.password.setEnabled(false);
      this.premiumBox = new LocalizableCheckbox("account.premium");
      this.premiumBox.addItemListener(new CheckBoxListener() {
         public void itemStateChanged(boolean newstate) {
            if (newstate && !AccountEditor.this.password.hasPassword()) {
               AccountEditor.this.password.setText((String)null);
            }

            AccountEditor.this.password.setEnabled(newstate);
            AccountEditor.this.username.setState(newstate ? UsernameField.UsernameState.EMAIL : UsernameField.UsernameState.USERNAME);
            AccountEditor.this.defocus();
         }
      });
      this.save = new LocalizableButton("account.save");
      this.save.addActionListener(enterHandler);
      this.progressBar = new ProgressBar();
      this.progressBar.setPreferredSize(new Dimension(200, 20));
      this.add(this.del(0));
      this.add(sepPan(new Component[]{this.username}));
      this.add(sepPan(new Component[]{this.premiumBox}));
      this.add(sepPan(new Component[]{this.password}));
      this.add(this.del(0));
      this.add(sepPan(new Component[]{this.save}));
      this.add(sepPan(new Component[]{this.progressBar}));
   }

   public void fill(Account account) {
      this.premiumBox.setSelected(account.isPremium());
      this.username.setText(account.getUsername());
      this.password.setText((String)null);
   }

   public void clear() {
      this.premiumBox.setSelected(false);
      this.username.setText((String)null);
      this.password.setText((String)null);
   }

   public Account get() {
      Account account = new Account();
      account.setUsername(this.username.getValue());
      if (this.premiumBox.isSelected()) {
         account.setPremium(true);
         if (this.password.hasPassword()) {
            account.setPassword(this.password.getPassword());
         }
      }

      return account;
   }

   public Insets getInsets() {
      return squareInsets;
   }

   public void block(Object reason) {
      super.block(reason);
      this.password.setEnabled(this.premiumBox.isSelected());
      if (!reason.equals("empty")) {
         this.progressBar.setIndeterminate(true);
      }

   }

   public void unblock(Object reason) {
      super.unblock(reason);
      this.password.setEnabled(this.premiumBox.isSelected());
      if (!reason.equals("empty")) {
         this.progressBar.setIndeterminate(false);
      }

   }
}
