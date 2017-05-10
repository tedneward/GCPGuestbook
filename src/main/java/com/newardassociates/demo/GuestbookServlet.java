package com.newardassociates.demo;

import java.io.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;

/**
 * GuestbookServlet: All-in-one servlet that presents a simple
 * "guest book", in which visitors can leave messages for others
 * to view later. Entries are automatically timestamped. GETing
 * to the servlet will display the list of entries so far, and a
 * simple form to send "name" and "message" back to itself; GETing
 * to this servlet with "name" and "message" adds to the list
 * and then display the other guest messages. Guest messages are
 * always displayed in reverse chronological order (most recent
 * first).
 * 
 * In no way am I trying to re-invent the era of the standalone
 * servlet, or the era of the servlet producing HTML output. (The
 * lack of any style whatsoever is hopefully enough to convince the
 * passers-by that this is a terrible way to build code.) Doing it
 * this way (a) helps keep the empahsis on the GCP aspects of the
 * system, and (b) helps ensure this doesn't become the core of
 * your next production Google Cloud Platform application. :-)
 */
@WebServlet(name = "guestbook", value = "/" )
public class GuestbookServlet extends HttpServlet {

  /**
   * Stored as an instance field just for convenience across the
   * multiple methods that use it; it should be null on entry into
   * the servlet and nulled out when we exit from it.
   */
  Datastore datastore;
  /**
   * Stored as an instance field just for convenience across the
   * multiple methods that use it; it should be null on entry into
   * the servlet and nulled out when we exit from it.
   */
  KeyFactory keyFactory;

  /**
   * Display the messages and a simple form to add to the list.
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    initializeDatastore();

    PrintWriter out = resp.getWriter();

    // if query params are present, process them
    processParams(req);

    printHeader(out);
    printForm(out);
    printMessages(out);

    // Now be safe and null out ds
    datastore = null;
    keyFactory = null;
  }

  private void initializeDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    keyFactory = datastore.newKeyFactory().setKind("Message");
  }

  private void printHeader(PrintWriter out) throws IOException {
    out.println("<h1>Welcome to the Guestbook</h1>");
  }

  private void processParams(HttpServletRequest req) {
    if (req.getParameter("message") != null) {
      // We have a message! Store it
      String message = req.getParameter("message");
      String from = req.getParameter("from");
      if (from == null) {
        from = "(Anonymous)";
      }

      Key key = datastore.allocateId(keyFactory.newKey());
      Entity msg = Entity.newBuilder(key)
          .set("message", StringValue.newBuilder(message).setExcludeFromIndexes(true).build())
          .set("from", StringValue.newBuilder(from).build())
          .set("created", DateTime.now())
          .build();
      datastore.put(msg);
    }
  }

  private void printForm(PrintWriter out) throws IOException {
    out.println("<h2>Please sign our guestbook</h2>");
    out.println("<form method='GET' action='/messages'>");
    out.println("Message: <input name='message' type=text /><br/>");
    out.println("From: <input name='from' type=text /><br/>");
    out.println("<input type=submit>Sign!</input>");
    out.println("</form>");
  }

  private void printMessages(PrintWriter out) throws IOException {
    out.println("<h2>Messages</h2>");
    Query<Entity> query =
        Query.newEntityQueryBuilder().
            setKind("Message").
            setOrderBy(OrderBy.asc("created")).
            build();
    PreparedQuery pq = datastore.prepare(query);
    List<Entity> messages = pq.asIterable();
    for (Entity e : messages) {
      out.println("On " + e.getProperty("created") + ", " +
        e.getProperty("from") + " wrote \"" + e.getProperty("message") + "\"");
    }
  }
}
