package com.example.guestbook;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignGuestbookServlet extends HttpServlet {
  // Process the HTTP POST of the form
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    String guestbookName = req.getParameter("guestbookName");
    Greeting greeting = 
      new Greeting(guestbookName, req.getParameter("content"));
    greeting.save();

    resp.sendRedirect("/guestbook.jsp?guestbookName=" + guestbookName);
  }
}
