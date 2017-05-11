package com.example.guestbook;

import static com.example.guestbook.Persistence.getDatastore;
import static com.example.guestbook.Persistence.getKeyFactory;
import static com.google.cloud.datastore.StructuredQuery.OrderBy.desc;
import static com.google.cloud.datastore.StructuredQuery.PropertyFilter.hasAncestor;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import java.util.List;
import java.util.Objects;

public class Guestbook {
  private static final KeyFactory keyFactory = getKeyFactory(Guestbook.class);
  private final Key key;

  public final String book;

  public Guestbook(String book) {
    this.book = book == null ? "default" : book;
    key =
        keyFactory.newKey(
            this.book); // There is a 1:1 mapping between Guestbook names and Guestbook objects
  }

  public Key getKey() {
    return key;
  }

  public List<Greeting> getGreetings() {
    // This query requires the index defined in index.yaml to work because of the orderBy on date.
    EntityQuery query =
        Query.newEntityQueryBuilder()
            .setKind("Greeting")
            .setFilter(hasAncestor(key))
            .setOrderBy(desc("date"))
            .setLimit(5)
            .build();

    QueryResults<Entity> results = getDatastore().run(query);

    Builder<Greeting> resultListBuilder = ImmutableList.builder();
    while (results.hasNext()) {
      resultListBuilder.add(new Greeting(results.next()));
    }

    return resultListBuilder.build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Guestbook guestbook = (Guestbook) o;
    return Objects.equals(book, guestbook.book) && Objects.equals(key, guestbook.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(book, key);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("keyFactory", keyFactory)
        .add("book", book)
        .add("key", key)
        .toString();
  }
}
