/*
 * Copyright 2000-2013 JetBrains s.r.o.
 * Copyright 2014-2018 AS3Boyan
 * Copyright 2014-2014 Elias Ku
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.plugins.logtalk.logtalklib;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Manage a classpath.
 *
 * Ordering is kept.  Duplicates are discarded.
 */
public class LogtalkClasspath {

  static Logger LOG = Logger.getInstance("#LogtalkClasspath");

  /**
   * An immutable empty classpath that can be re-used.
   */
  public static final LogtalkClasspath EMPTY_CLASSPATH = new LogtalkClasspath(true);

  // Any access of myOrderedEntries MUST be synchronized if this classpath is
  // used in a multi-threaded environment.
  protected Set<LogtalkClasspathEntry> myOrderedEntries;

  /**
   * Constructor used solely to create the EMPTY_CLASSPATH;
   * @param createEmpty
   */
  private LogtalkClasspath(boolean createEmpty) {
    myOrderedEntries = Collections.emptySet();
  }

  LogtalkClasspath(LogtalkClasspath initialEntries) {
    this();
    synchronized (initialEntries) {
      myOrderedEntries.addAll(initialEntries.myOrderedEntries);
    }
  }

  LogtalkClasspath(LogtalkClasspathEntry initialEntry) {
    this();
    myOrderedEntries.add(initialEntry);
  }

  LogtalkClasspath(Collection<LogtalkClasspathEntry> initialEntries) {
    this(initialEntries.size());
    myOrderedEntries.addAll(initialEntries);
  }

  LogtalkClasspath() {
    this(16);
  }

  LogtalkClasspath(int sizeHint) {
    if (sizeHint < 16)
      sizeHint  = 16;
    myOrderedEntries = new LinkedHashSet<LogtalkClasspathEntry>(2 * sizeHint);
  }

  /**
   * Add an entry to the end of this classpath.  If the entry with the same
   * URL already exists, the current one is maintained, along with its current
   * position, and the new one is ignored.
   *
   * @param item to add to the end of the classpath.
   */
  public void add(LogtalkClasspathEntry item) {
    synchronized(this) {
      if (!contains(item))
      myOrderedEntries.add(item);
    }
  }

  /**
   * Add a set of entries to this classpath. New entries
   * will be added at the end of this classpath.  Duplicate entries are ignored
   * (current entries with the same URL are maintained, with their current
   * ordering).
   *
   * @param entries to add to the end of this classpath.
   */
  public void addAll(Collection<LogtalkClasspathEntry> entries) {
    synchronized(this) {
      myOrderedEntries.addAll(entries);
    }
  }

  /**
   * Add another classpath to this one.  New entries from the other classpath
   * will be added at the end of this classpath.  Duplicate entries are ignored
   * (current entries with the same URL are maintained, with their current
   * ordering).
   *
   * @param classpath to add to this classpath
   */
  public void addAll(LogtalkClasspath classpath) {
    synchronized(this) {
      synchronized(classpath) {
        myOrderedEntries.addAll(classpath.myOrderedEntries);
      }
    }
  }

  /**
   * Remove all entries from this classpath.
   */
  public void clear() {
    synchronized(this) {
      myOrderedEntries.clear();
    }
  }

  /**
   * Determine whether this classpath contains an entry that semantically matches
   * the given entry.  (That is, it represents the same file system path.)
   *
   * @param item
   * @return
   */
  public boolean contains(LogtalkClasspathEntry item) {
    synchronized(this) {
      return myOrderedEntries.contains(item);
    }
  }

  /**
   * Determine if a given URL is represented by any Entry/Item in the classpath.
   * NOTE: This method *does NOT* attempt to normalize the URL.  Relative paths
   * will NOT match.
   *
   * @param url we are looking for.
   * @return true if an entry matches the URL; false otherwise.
   */
  public boolean containsUrl(final String url) {
    if (null == url || url.isEmpty())
      return false;

    synchronized(this) {
      // OK, this works because the hash code for a LogtalkClasspathEntry is the
      // myUrl hash code.  That makes hash lookups the same for urls and entries.
      // It also makes equals work because the LogtalkClasspathEntry's equals
      // method is overridden in the same way, to only check the url string.
      class Comparator {
        public int hashCode() { return LogtalkClasspathEntry.hashUrl(url); }
        public boolean equals(Object o) {
          LogtalkClasspathEntry that = (LogtalkClasspathEntry)o;
          if (!that.getUrl().equals(url)) return false;  // In case of hash collision.
          return true;
        }
      }
      return myOrderedEntries.contains(new Comparator());
    }
  }

  /**
   * Tell whether this classpath is empty (has no entries).  This is a constant
   * time operation, whereas size() would not be.
   *
   * @return true if empty, false if not.
   */
  public boolean isEmpty() {
    synchronized(this) {
      return myOrderedEntries.isEmpty();
    }
  }

  /**
   * Iterate over the list and perform a task.  The lambda must return a boolean
   * value indicating whether to continue iterating through the paths, or stop
   * immediately: true to continue, false to stop.
   *
   * @param lambda functional interface for the action to perform.
   * @return what the action returned: true to keep going, or false to stop.
   */
  public boolean iterate(Lambda lambda) {
    boolean continu = true;
    synchronized(this) {
      for (LogtalkClasspathEntry entry : myOrderedEntries) {
        continu = lambda.processEntry(entry);
        if (!continu)
          break;
      }
    }
    return continu;
  }

  /**
   * Remove an entry from the classpath.
   *
   * @param item to remove.
   */
  public void remove(LogtalkClasspathEntry item) {
    synchronized(this) {
      myOrderedEntries.remove(item);
    }
  }

  /**
   * Remove all entries from this classpath that occur in the given classpath.
   * It is NOT an error of otherpath contains entries that do not exist in
   * this classpath.
   *
   * @param otherPath with entries to remove
   */
  public void removeAll(@NotNull LogtalkClasspath otherPath) {
    synchronized(this) {
      synchronized (otherPath) {
        myOrderedEntries.removeAll(otherPath.myOrderedEntries);
      }
    }
  }

  /**
   * Remove all entries from this classpath that occur in the given list.
   * It is NOT an error to specify entries that do not already exist.
   *
   * @param entries to remove.
   */
  public void removeAll(@NotNull Collection<LogtalkClasspathEntry> entries) {
    synchronized(this) {
      if (entries.isEmpty() || myOrderedEntries.isEmpty()) {
        return;
      }
      Iterator iterator = myOrderedEntries.iterator();
      while (iterator.hasNext()) {
        LogtalkClasspathEntry entry = (LogtalkClasspathEntry)iterator.next();
        if (entries.contains(entry)) {
          iterator.remove();
        }
      }
    }
  }

  /**
   * Get the number of entries in this classpath.  BEWARE: This is NOT a
   * constant time operation.  It is dependent upon the number of elements
   * in this classpath.
   *
   * @return the number of entries.
   */
  public int size() {
    synchronized(this) {
      return myOrderedEntries.size();
    }
  }

  /**
   * Get the n'th element from the list.  Do not use this inside of a tight loop,
   * because the algorithm requires a serial walk of all elements prior.
   * Use iterate() instead.
   *
   * @param i
   * @return
   */
  public LogtalkClasspathEntry get(int i) {
    synchronized (this) {
      if (i >= 0) {
        int j = 0;
        for (LogtalkClasspathEntry entry : myOrderedEntries) {
          if (j++ == i) {
            return entry;
          }
        }
      }
      throw new IndexOutOfBoundsException();
    }
  }

  /**
   * Wrapper to do a discrete bit of work while honoring the synchronized
   * aspects of the path.
   */
  public interface Lambda {
    /**
     * Process a single entry in the classpath list.  The list is already
     * synchronized when this is called.
     *
     * @param entry A class path element (path).
     * @return true if the loop should keep running, false if not.
     */
    public boolean processEntry(LogtalkClasspathEntry entry);
  }

  /**
   * Dumps this classpath to a single idea.log entry.
   * @param header to display at the start of the entry.
   */
  public void debugDump(String header) {
    class Collector implements Lambda {  // XXX: Wish this could be anonymous.
      public String myLog;
      public Collector(String header) {myLog = header;}
      @Override
      public boolean processEntry(LogtalkClasspathEntry entry) {
        myLog += "\n   " + entry.getName() + "\n      " + entry.getUrl();
        return true;
      }
    };
    Collector logCollector = new Collector(null == header ? "LogtalkClasspath dump" : header);
    iterate(logCollector);
    LOG.debug(logCollector.myLog);
  }

}
