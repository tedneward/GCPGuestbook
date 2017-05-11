package com.example.guestbook;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.KeyFactory;

import java.util.concurrent.atomic.AtomicReference;

public class Persistence {
  private static AtomicReference<Datastore> datastore = new AtomicReference<>();

  public static Datastore getDatastore() {
    if (datastore.get() == null) {
      datastore.set(DatastoreOptions.newBuilder()
        .build()
        .getService());
    }

    return datastore.get();
  }

  public static void setDatastore(Datastore datastore) {
    Persistence.datastore.set(datastore);
  }

  public static KeyFactory getKeyFactory(Class<?> c) {
    return getDatastore().newKeyFactory().setKind(c.getSimpleName());
  }
}
