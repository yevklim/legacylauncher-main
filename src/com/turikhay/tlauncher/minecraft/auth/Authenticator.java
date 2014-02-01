package com.turikhay.tlauncher.minecraft.auth;

import com.google.gson.Gson;
import com.turikhay.tlauncher.TLauncher;
import com.turikhay.util.U;
import com.turikhay.util.async.AsyncThread;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import net.minecraft.launcher.Http;
import org.apache.commons.lang3.StringUtils;

public class Authenticator {
   private static final URL ROUTE_AUTHENTICATE = Http.constantURL("https://authserver.mojang.com/authenticate");
   private static final URL ROUTE_REFRESH = Http.constantURL("https://authserver.mojang.com/refresh");
   public final Account account;
   private final Authenticator instance;
   private final Gson gson;

   public Authenticator(Account account) {
      if (account == null) {
         throw new NullPointerException();
      } else {
         this.instance = this;
         this.gson = new Gson();
         this.account = account;
      }
   }

   public UUID getClientToken() {
      return TLauncher.getInstance().getProfileManager().getClientToken();
   }

   private void setClientToken(String uuid) {
      TLauncher.getInstance().getProfileManager().setClientToken(uuid);
   }

   public void pass() throws AuthenticatorException {
      if (!this.account.hasLicense()) {
         throw new IllegalArgumentException("Invalid account type!");
      } else if (this.account.getPassword() == null && this.account.getAccessToken() == null) {
         throw new AuthenticatorException("Password and token are NULL!");
      } else {
         this.log("Staring to authenticate:");
         this.log("hasUsername:", this.account.getUsername());
         this.log("hasPassword:", this.account.getPassword() != null);
         this.log("hasAccessToken:", this.account.getAccessToken() != null);
         if (this.account.getPassword() == null) {
            this.tokenLogin();
         } else {
            this.passwordLogin();
         }

         this.log("Log in successful!");
         this.log("hasUUID:", this.account.getUUID() != null);
         this.log("hasAccessToken:", this.account.getAccessToken() != null);
         this.log("hasProfiles:", this.account.getProfiles() != null);
         this.log("hasProfile:", this.account.getProfiles() != null);
         this.log("hasProperties:", this.account.getProperties() != null);
      }
   }

   protected void passwordLogin() throws AuthenticatorException {
      this.log("Loggining in with password");
      AuthenticationRequest request = new AuthenticationRequest(this);
      AuthenticationResponse response = (AuthenticationResponse)this.makeRequest(ROUTE_AUTHENTICATE, request, AuthenticationResponse.class);
      this.account.setUserID(response.getUserID() != null ? response.getUserID() : this.account.getUsername());
      this.account.setAccessToken(response.getAccessToken());
      this.account.setProfiles(response.getAvailableProfiles());
      this.account.setProfile(response.getSelectedProfile());
      this.account.setUser(response.getUser());
      this.setClientToken(response.getClientToken());
      if (response.getSelectedProfile() != null) {
         this.account.setUUID(response.getSelectedProfile().getId());
         this.account.setDisplayName(response.getSelectedProfile().getName());
      }

   }

   protected void tokenLogin() throws AuthenticatorException {
      this.log("Loggining in with token");
      RefreshRequest request = new RefreshRequest(this);
      RefreshResponse response = (RefreshResponse)this.makeRequest(ROUTE_REFRESH, request, RefreshResponse.class);
      this.setClientToken(response.getClientToken());
      this.account.setAccessToken(response.getAccessToken());
      this.account.setProfile(response.getSelectedProfile());
      this.account.setUser(response.getUser());
   }

   protected Response makeRequest(URL url, Request input, Class classOfT) throws AuthenticatorException {
      String jsonResult;
      try {
         jsonResult = input == null ? AuthenticatorService.performGetRequest(url) : AuthenticatorService.performPostRequest(url, this.gson.toJson((Object)input), "application/json");
      } catch (IOException var6) {
         throw new AuthenticatorException("Error making request, uncaught IOException", "unreachable", var6);
      }

      Response result = (Response)this.gson.fromJson(jsonResult, classOfT);
      if (result == null) {
         return null;
      } else if (StringUtils.isBlank(result.getError())) {
         return result;
      } else if ("UserMigratedException".equals(result.getCause())) {
         throw new UserMigratedException();
      } else if (result.getError().equals("ForbiddenOperationException")) {
         throw new InvalidCredentialsException();
      } else {
         throw new AuthenticatorException(result.getErrorMessage(), "internal");
      }
   }

   public void asyncPass(final AuthenticatorListener l) {
      AsyncThread.execute(new Runnable() {
         public void run() {
            if (l != null) {
               l.onAuthPassing(Authenticator.this.instance);
            }

            try {
               Authenticator.this.instance.pass();
            } catch (Exception var2) {
               Authenticator.this.log("Cannot authenticate:", var2);
               if (l != null) {
                  l.onAuthPassingError(Authenticator.this.instance, var2);
               }

               return;
            }

            if (l != null) {
               l.onAuthPassed(Authenticator.this.instance);
            }

         }
      });
   }

   protected void log(Object... o) {
      U.log("[AUTH]", o);
   }
}
