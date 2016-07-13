package ru.turikhay.tlauncher.downloader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import ru.turikhay.tlauncher.ui.logger.Logger;

public class DownloadableContainer {
   private final List handlers = Collections.synchronizedList(new ArrayList());
   private final List errors = Collections.synchronizedList(new ArrayList());
   final List list = Collections.synchronizedList(new ArrayList());
   private Logger logger;
   private final AtomicInteger sum = new AtomicInteger();
   private boolean locked;
   private boolean aborted;

   public List getList() {
      return Collections.unmodifiableList(this.list);
   }

   public void add(Downloadable d) {
      if (d == null) {
         throw new NullPointerException();
      } else {
         this.checkLocked();
         if (!this.list.contains(d)) {
            this.list.add(d);
            d.setContainer(this);
            this.sum.incrementAndGet();
         }

      }
   }

   public void addAll(Collection coll) {
      if (coll == null) {
         throw new NullPointerException();
      } else {
         int i = -1;
         Iterator var4 = coll.iterator();

         while(var4.hasNext()) {
            Downloadable d = (Downloadable)var4.next();
            ++i;
            if (d == null) {
               throw new NullPointerException("Downloadable at" + i + " is NULL!");
            }

            if (!this.list.contains(d)) {
               this.list.add(d);
               d.setContainer(this);
               this.sum.incrementAndGet();
            }
         }

      }
   }

   public List getErrors() {
      return Collections.unmodifiableList(this.errors);
   }

   public Logger getLogger() {
      return this.logger;
   }

   public boolean hasLogger() {
      return this.logger != null;
   }

   public void setLogger(Logger logger) {
      this.checkLocked();
      this.logger = logger;
   }

   public boolean isAborted() {
      return this.aborted;
   }

   void checkLocked() {
      if (this.locked) {
         throw new IllegalStateException("Downloadable is locked!");
      }
   }

   void onComplete(Downloadable d) throws RetryDownloadException {
      Iterator var3 = this.handlers.iterator();

      DownloadableContainerHandler handler;
      while(var3.hasNext()) {
         handler = (DownloadableContainerHandler)var3.next();
         handler.onComplete(this, d);
      }

      if (this.sum.decrementAndGet() <= 0) {
         var3 = this.handlers.iterator();

         while(var3.hasNext()) {
            handler = (DownloadableContainerHandler)var3.next();
            handler.onFullComplete(this);
         }
      }

   }

   void onAbort(Downloadable d) {
      this.aborted = true;
      this.errors.add(d.getError());
      if (this.sum.decrementAndGet() <= 0) {
         Iterator var3 = this.handlers.iterator();

         while(var3.hasNext()) {
            DownloadableContainerHandler handler = (DownloadableContainerHandler)var3.next();
            handler.onAbort(this);
         }
      }

   }

   void onError(Downloadable d, Throwable e) {
      this.errors.add(e);
      Iterator var4 = this.handlers.iterator();

      while(var4.hasNext()) {
         DownloadableContainerHandler handler = (DownloadableContainerHandler)var4.next();
         handler.onError(this, d, e);
      }

   }

   public static void removeDuplicates(DownloadableContainer a, DownloadableContainer b) {
      if (a.locked) {
         throw new IllegalStateException("First conatiner is already locked!");
      } else if (b.locked) {
         throw new IllegalStateException("Second container is already locked!");
      } else {
         a.locked = true;
         b.locked = true;

         try {
            List aList = a.list;
            List bList = b.list;
            ArrayList deleteList = new ArrayList();
            Iterator var6 = aList.iterator();

            label68:
            while(true) {
               if (var6.hasNext()) {
                  Downloadable aDownloadable = (Downloadable)var6.next();
                  Iterator var8 = bList.iterator();

                  while(true) {
                     if (!var8.hasNext()) {
                        continue label68;
                     }

                     Downloadable bDownloadable = (Downloadable)var8.next();
                     if (aDownloadable.equals(bDownloadable)) {
                        deleteList.add(bDownloadable);
                     }
                  }
               }

               bList.removeAll(deleteList);
               return;
            }
         } finally {
            a.locked = false;
            b.locked = false;
         }
      }
   }

   public static void removeDuplicates(List list) {
      if (list == null) {
         throw new NullPointerException();
      } else {
         if (list.size() >= 2) {
            for(int i = 0; i < list.size() - 1; ++i) {
               for(int k = i + 1; k < list.size(); ++k) {
                  removeDuplicates((DownloadableContainer)list.get(i), (DownloadableContainer)list.get(k));
               }
            }
         }

      }
   }
}
