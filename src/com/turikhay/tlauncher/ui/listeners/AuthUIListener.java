package com.turikhay.tlauncher.ui.listeners;

import com.turikhay.tlauncher.TLauncher;
import com.turikhay.tlauncher.minecraft.auth.Authenticator;
import com.turikhay.tlauncher.minecraft.auth.AuthenticatorException;
import com.turikhay.tlauncher.minecraft.auth.AuthenticatorListener;
import com.turikhay.tlauncher.ui.alert.Alert;
import com.turikhay.tlauncher.ui.loc.Localizable;
import java.io.IOException;

public class AuthUIListener implements AuthenticatorListener {
   private final AuthenticatorListener listener;
   private final boolean showErrorOnce;
   private boolean errorShown;

   public AuthUIListener(boolean showErrorOnce, AuthenticatorListener listener) {
      this.listener = listener;
      this.showErrorOnce = showErrorOnce;
   }

   public void onAuthPassing(Authenticator auth) {
      if (this.listener != null) {
         this.listener.onAuthPassing(auth);
      }
   }

   public void onAuthPassingError(Authenticator auth, Throwable e) {
      if (this.listener != null) {
         this.listener.onAuthPassingError(auth, e);
      }

      if (!this.showErrorOnce || !this.errorShown) {
         String langpath = "unknown";
         if (e instanceof AuthenticatorException) {
            AuthenticatorException ae = (AuthenticatorException)e;
            langpath = ae.getLangpath() == null ? "unknown" : ae.getLangpath();
            e = null;
         }

         Alert.showError(Localizable.get("auth.error.title"), Localizable.get("auth.error." + langpath), e);
         this.errorShown = true;
      }
   }

   public void onAuthPassed(Authenticator auth) {
      if (this.listener != null) {
         this.listener.onAuthPassed(auth);
      }

      this.saveProfiles();
   }

   public void saveProfiles() {
      try {
         TLauncher.getInstance().getProfileManager().saveProfiles();
      } catch (IOException var2) {
         Alert.showError("auth.profiles.save-error");
      }

   }
}
