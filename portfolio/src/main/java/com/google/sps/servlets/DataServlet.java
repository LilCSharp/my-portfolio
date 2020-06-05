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
import java.util.List;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/** Servlet that returns some example content. TODO: modify this file to handle
comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException { 

    String limit = request.getParameter("limit");
    int max = Integer.parseInt(limit);
    int count = 1;

    Query query = new Query("Task").addSort("time", 
        SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Task> tasks = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      
      if (count > max) {
        break;
      }

      long id = entity.getKey().getId();
      String username = (String) entity.getProperty("name");
      String textDate = (String) entity.getProperty("date");
      String words = (String) entity.getProperty("text");

      Task task = new Task(id, username, textDate, words);
      
      tasks.add(task);

      count++;
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(tasks));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws IOException {
    
    String text = request.getParameter("chatBox");
    String name = request.getParameter("nameBox");
    String date = "";
    String time;

    if (text == null) {
        text = "";
    }
    if (name == null || name.length() == 0 || name.charAt(0) == ' ') {
        name = "Anonymous";
    }

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy ");
    DateTimeFormatter utcTime = DateTimeFormatter.ofPattern("HH:mm:s M/dd/yyyy");
    LocalDateTime now = LocalDateTime.now();
    date = dtf.format(now).toString();
    time = utcTime.format(now).toString();

    Entity comments = new Entity("Task");
    comments.setProperty("name", name);
    comments.setProperty("date", date);
    comments.setProperty("text", text);
    comments.setProperty("time", time);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(comments);

    response.sendRedirect("/pages/chat.html");
  }
}