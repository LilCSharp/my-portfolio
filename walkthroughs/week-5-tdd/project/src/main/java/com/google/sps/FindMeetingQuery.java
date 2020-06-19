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

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Stack;

public final class FindMeetingQuery {

  private Node root = null;
  private Collection<TimeRange> times = new ArrayList<TimeRange>();

  private class Node {
    Node left, right;
    int start, end;

    private Node(int start, int end) {
      this.start = start;
      this.end = end;
    }
  }

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    
    if (events == null || request == null) {
      throw new IllegalArgumentException();
    }

    Collection<String> attendees = request.getAttendees();
    Collection<String> optionals = request.getOptionalAttendees();
    boolean invalid = false;
    int count = 0;

    for (Event available: events) {
    
      for (String people: available.getAttendees()) {
        if (!attendees.contains(people)) {
          invalid = true;
          break;
        }
        count++;
      }

      if (!invalid) {
        root = insert(new Node(available.getWhen().start(), available.getWhen().end()), root);
      }

      invalid = false;
    }

    getTimes(root, 0, (int) request.getDuration());
    
    root = null;

    if (count != 0){
      for (Event available: events) {
    
        Collection<String> groups = available.getAttendees();

        for (String people: optionals) {
          if(groups.contains(people)) {
            root = insert(new Node(available.getWhen().start(), available.getWhen().end()), root);
            break;
          }
        }
      }

      ArrayList<Node> meetings = new ArrayList<Node>();

      for(TimeRange meets: times) {
        meetings.add(new Node(meets.start(), meets.end()));
      }

      getOptionals(root, meetings, (int) request.getDuration());
    } else {

      times = new ArrayList<TimeRange>();

      for (Event available: events) {
    
        Collection<String> groups = available.getAttendees();

        for (String people: optionals) {
          if(groups.contains(people)) {
            root = insert(new Node(available.getWhen().start(), available.getWhen().end()), root);
            break;
          }
        }
      }
      
      getTimes(root, 0, (int) request.getDuration());
    }

    return times;
  }

  private Node insert(Node node, Node curr) {
    
    if (curr == null) {
      curr = node;
    } else if (node.start < curr.start) {
       curr.left = insert(node, curr.left);
    } else if (node.start > curr.start) {
       curr.right = insert(node, curr.right);
    } else {
       if (node.end > curr.end) {
         curr = node;
       }
    }

    return curr;
  }

  private void getTimes(Node curr, int end, int duration) {
    Stack<Node> stack = new Stack<Node>();

    if (duration < TimeRange.END_OF_DAY) {
      while(curr != null || !stack.isEmpty()) {
        if (curr != null) {
          stack.push(curr);
          curr = curr.left;
        } else {
          curr = stack.pop();

          if (curr.start - end >= duration) {
            times.add(TimeRange.fromStartDuration(end, curr.start - end));
          }

          if (curr.end > end) {
            end = curr.end;
          }

          curr = curr.right;
        }
      }

      if (end - TimeRange.END_OF_DAY + 1 < 0) {
        times.add(TimeRange.fromStartDuration(end, TimeRange.END_OF_DAY + 1 - end));
      }
    }
  }

  private void getOptionals(Node curr, ArrayList<Node> list, int duration) {
    Stack<Node> stack = new Stack<Node>();

    if (duration < TimeRange.END_OF_DAY) {
      while(curr != null || !stack.isEmpty()) {
        if (curr != null) {
          stack.push(curr);
          curr = curr.left;
        } else {
          curr = stack.pop();

          for (int i = 0; i < list.size(); i++) {
            if (curr.start >= list.get(i).start && curr.start < list.get(i).end && curr.end >= list.get(i).end) {
              list.get(i).end = curr.start;
            } else if (curr.start < list.get(i).start && curr.end >= list.get(i).start) {
              list.get(i).start = curr.end;
            } else if (curr.start > list.get(i).start && curr.end < list.get(i).end) {
              int temp = list.get(i).end;
              list.get(i).end = curr.start;
              list.add(i, new Node(curr.end, temp));
              i--;
            }
          }

          curr = curr.right;
        }
      }

      ArrayList<TimeRange> modified = new ArrayList<TimeRange>();
      boolean found = false;

      for (int i = 0; i < list.size(); i++) {
        if (list.get(i).end - list.get(i).start >= duration) {
          modified.add(TimeRange.fromStartDuration(list.get(i).start, list.get(i).end - list.get(i).start));
          found = true;
        }
      }

      if (found) {
        times = modified;
      }
    }
  }
}