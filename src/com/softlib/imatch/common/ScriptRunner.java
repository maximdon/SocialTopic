package com.softlib.imatch.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.*;

import org.apache.log4j.Logger;

public class ScriptRunner {

  private static final String LINE_SEPARATOR = System.getProperty("line.separator","\n");

  private static final String DEFAULT_DELIMITER = ";";

  private Connection connection;
  private static Logger log = Logger.getLogger(ScriptRunner.class);
  
  private boolean stopOnError = true;
  private boolean autoCommit = false;
  private boolean sendFullScript = false;

  private String delimiter = DEFAULT_DELIMITER;
  private boolean fullLineDelimiter = false;
  private String characterSetName;

  public ScriptRunner(Connection connection) {
    this.connection = connection;
  }

  public void setCharacterSetName(String characterSetName) {
    this.characterSetName = characterSetName;
  }

  public void setStopOnError(boolean stopOnError) {
    this.stopOnError = stopOnError;
  }

  public void setAutoCommit(boolean autoCommit) {
    this.autoCommit = autoCommit;
  }

  public void setSendFullScript(boolean sendFullScript) {
    this.sendFullScript = sendFullScript;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public void setFullLineDelimiter(boolean fullLineDelimiter) {
    this.fullLineDelimiter = fullLineDelimiter;
  }

  public void runScript(String scriptFileName) throws SQLException, FileNotFoundException {
	  FileReader reader = new FileReader(scriptFileName);
	  runScript(reader);
  }
  
  public void runScript(Reader reader) throws SQLException {
    setAutoCommit();

    try {
      if (sendFullScript) {
        executeFullScript(reader);
      } else {
        executeLineByLine(reader);
      }
    } finally {
      rollbackConnection();
    }
  }

  private void executeFullScript(Reader reader) throws SQLException {
    StringBuffer script = new StringBuffer();
    try {
      BufferedReader lineReader = new BufferedReader(reader);
      String line;
      while ((line = lineReader.readLine()) != null) {
        script.append(line);
        script.append(LINE_SEPARATOR);
      }
      executeStatement(script.toString());
      commitConnection();
    } catch (Exception e) {
      String message = "Error executing: " + script + ".  Cause: " + e;
      printlnError(message);
      throw new SQLException(message, e);
    }
  }

  private void executeLineByLine(Reader reader) throws SQLException {
    StringBuffer command = new StringBuffer();
    try {
      BufferedReader lineReader = new BufferedReader(reader);
      String line;
      while ((line = lineReader.readLine()) != null) {
        command = handleLine(command, line);
      }
      commitConnection();
      checkForMissingLineTerminator(command);
    } catch (Exception e) {
      String message = "Error executing: " + command + ".  Cause: " + e;
      printlnError(message);
      throw new SQLException(message, e);
    }
  }

  public void closeConnection() {
    try {
      connection.close();
    } catch (Exception e) {
      // ignore
    }
  }

  private void setAutoCommit() throws SQLException {
    try {
      if (autoCommit != connection.getAutoCommit()) {
        connection.setAutoCommit(autoCommit);
      }
    } catch (Throwable t) {
      throw new SQLException("Could not set AutoCommit to " + autoCommit + ". Cause: " + t, t);
    }
  }

  private void commitConnection() throws SQLException {
    try {
      if (!connection.getAutoCommit()) {
        connection.commit();
      }
    } catch (Throwable t) {
      throw new SQLException("Could not commit transaction. Cause: " + t, t);
    }
  }

  private void rollbackConnection() {
    try {
      if (!connection.getAutoCommit()) {
        connection.rollback();
      }
    } catch (Throwable t) {
      // ignore
    }
  }

  private void checkForMissingLineTerminator(StringBuffer command) throws SQLException {
    if (command != null && command.toString().trim().length() > 0) {
      throw new SQLException("Line missing end-of-line terminator (" + delimiter + ") => " + command);
    }
  }

  private StringBuffer handleLine(StringBuffer command, String line) throws SQLException, UnsupportedEncodingException {
    String trimmedLine = line.trim();
    if (lineIsComment(trimmedLine)) {
      println(trimmedLine);
    } else if (commandReadyToExecute(trimmedLine)) {
      command.append(line.substring(0, line.lastIndexOf(delimiter)));
      command.append(LINE_SEPARATOR);
      println(command);
      executeStatement(command.toString());
      command.setLength(0);
    } else if (trimmedLine.length() > 0) {
      command.append(line);
      command.append(LINE_SEPARATOR);
    }
    return command;
  }

  private boolean lineIsComment(String trimmedLine) {
    return trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
  }

  private boolean commandReadyToExecute(String trimmedLine) {
    return !fullLineDelimiter && trimmedLine.endsWith(delimiter)
        || fullLineDelimiter && trimmedLine.equals(delimiter);
  }

  private void executeStatement(String command) throws SQLException, UnsupportedEncodingException {
    if(characterSetName != null){
      command = new String(command.getBytes(), characterSetName);
    }
    boolean hasResults = false;
    Statement statement = connection.createStatement();
    if (stopOnError) {
      hasResults = statement.execute(command);
    } else {
      try {
        hasResults = statement.execute(command);
      } catch (SQLException e) {
        String message = "Error executing: " + command + ".  Cause: " + e;
        printlnError(message);
      }
    }
    printResults(statement, hasResults);
    try {
      statement.close();
    } catch (Exception e) {
      // Ignore to workaround a bug in some connection pools
    }
  }


  private void printResults(Statement statement, boolean hasResults) {
    try {
      if (hasResults) {
        ResultSet rs = statement.getResultSet();
        if (rs != null) {
          ResultSetMetaData md = rs.getMetaData();
          int cols = md.getColumnCount();
          for (int i = 0; i < cols; i++) {
            String name = md.getColumnLabel(i + 1);
            print(name + "\t");
          }
          println("");
          while (rs.next()) {
            for (int i = 0; i < cols; i++) {
              String value = rs.getString(i + 1);
              print(value + "\t");
            }
            println("");
          }
        }
      }
    } catch (SQLException e) {
      printlnError("Error printing results: " + e.getMessage());
    }
  }

  private void print(Object o) {
//	  if(o != null)
//		  LogUtils.debug(log, o.toString());
  }

  private void println(Object o) {
//	  print(o);
  }

  private void printlnError(Object o) {
//	  if(o != null)
//		  LogUtils.error(log, o.toString());
  }
}
