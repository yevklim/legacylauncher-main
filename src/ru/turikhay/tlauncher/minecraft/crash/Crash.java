package ru.turikhay.tlauncher.minecraft.crash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Crash {
   private String file;
   private List signatures = new ArrayList();

   void addSignature(CrashSignatureContainer.CrashSignature sign) {
      this.signatures.add(sign);
   }

   void removeSignature(CrashSignatureContainer.CrashSignature sign) {
      this.signatures.remove(sign);
   }

   void setFile(String path) {
      this.file = path;
   }

   public String getFile() {
      return this.file;
   }

   public List getSignatures() {
      return Collections.unmodifiableList(this.signatures);
   }

   public boolean hasSignature(CrashSignatureContainer.CrashSignature s) {
      return this.signatures.contains(s);
   }

   public boolean contains(String name) {
      Iterator var3 = this.signatures.iterator();

      CrashSignatureContainer.CrashSignature signature;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         signature = (CrashSignatureContainer.CrashSignature)var3.next();
      } while(!signature.getName().equals(name));

      return true;
   }

   public boolean isRecognized() {
      return !this.signatures.isEmpty();
   }
}
