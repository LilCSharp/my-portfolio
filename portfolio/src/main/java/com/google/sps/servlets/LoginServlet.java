// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Task;
import java.io.IOException;
import java.util.ArrayList;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException {
    
    response.setContentType("text/html");

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = 
        "https://calebwheeler-step-2020.ue.r.appspot.com/pages/chat.html";
      String logoutUrl = userService.createLogoutURL(
        urlToRedirectToAfterUserLogsOut);

      response.getWriter().println("<p>Hello " + userEmail + "!</p>");
      response.getWriter().println(
        "<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
    } else {
      String urlToRedirectToAfterUserLogsIn = 
        "https://calebwheeler-step-2020.ue.r.appspot.com/pages/chat.html";
      String loginUrl = userService.createLoginURL(
        urlToRedirectToAfterUserLogsIn);
      
      response.getWriter().println("<p>Hello stranger.</p>");
      response.getWriter().println("<p>Login <a href=\"" + loginUrl + 
        "\">here</a>.</p>");
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
   throws IOException {

    String text = request.getParameter("chatBox");
    String name = request.getParameter("nameBox");
    String date = "";
    String time, email;
      
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/login");
      return;
    }

    email = userService.getCurrentUser().getEmail();

    if (text == null) {
      text = "";
    }

    if (name == null || name.length() == 0) {
      name = "Anonymous";
    }

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy ");
    DateTimeFormatter utcTime = DateTimeFormatter.ofPattern("HH:mm:s M/dd/yyyy");
    LocalDateTime now = LocalDateTime.now();
    date = dtf.format(now).toString();
    time = utcTime.format(now).toString();

    Entity comments = new Entity("Task");
    //comments.setProperty("name", name);
    comments.setProperty("date", date);
    comments.setProperty("text", text);
    comments.setProperty("time", time);
    comments.setProperty("email", email);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(comments);

    response.sendRedirect("/pages/chat.html");
  }
}