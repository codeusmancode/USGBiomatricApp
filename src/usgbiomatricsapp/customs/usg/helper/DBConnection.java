/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usgbiomatricsapp.customs.usg.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import usgbiomatricsapp.customs.Global;
import usgbiomatricsapp.customs.usg.pojo.Employee;

/**
 *
 * @author usmanriaz
 */
public class DBConnection {

    /**
     *
     * private static DBConnection _instance; private static String driverClass
     * = "oracle.jdbc.driver.OracleDriver"; private static String
     * connectionString = "jdbc:oracle:thin:@193.168.0.26:1521:PROD"; private
     * static Connection connection;
     *
     * public static Connection getConnection() throws Exception{ if (connection
     * == null){ Class.forName(driverClass); connection =
     * DriverManager.getConnection(connectionString, "custusg", "custusg"); }
     * return connection; }
     *
     */
    private static DBConnection _instance;
    private static String driverClass = "oracle.jdbc.driver.OracleDriver";
    //private static String connectionString = "jdbc:oracle:thin:@193.168.0.6:1521:ORCL";//test
    
    private static String connectionString = "jdbc:oracle:thin:@193.168.0.7:1521:ORCL";//prod
    private static Connection connection;

    public static DBConnection getInstance() {
        if (_instance == null) {
            _instance = new DBConnection();
        }
        return _instance;
    }

    private DBConnection() {
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection Closed.");
            } catch (Exception ex) {
                System.out.println("Error Closing Connection:"+ex.getMessage());
            } finally {
                connection = null;
                System.out.println("Connection nullified.");
            }

        }
    }

    public Connection getConnection() {
        if (connection == null) {
            try {
                
                Class.forName(driverClass);
                connection = DriverManager.getConnection(connectionString, "USPR", "HTCDESIRE");
                System.out.println("CONNECTION OPENED");
            } catch (Exception ex) {
                connection = null;
                System.out.println("DBConnection(getConnection()):"+ex.getMessage());
            }

        }
        return connection;
    }

//    public static Connection getConnection() throws Exception {
//        if (connection == null) {
//            System.out.println("creating new connection");
//            Class.forName(driverClass);
//            connection = DriverManager.getConnection(connectionString, "USPR", "HTCDESIRE");
//        } else {
//            System.out.println("reusing connection");
//        }
//        return connection;
//    }
//    public static void closeConnection() throws Exception {
//        if (connection != null) {
//            connection.close();
//            connection = null;
//        }
//    }
}
