package ru.edu.currency_exchange.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class DataBase {
    private static String dbPropertiesURL = "C:\\Users\\AR\\IdeaProjects\\currency_exchange\\src\\main\\resources\\db.properties";
    private static List<String> propertiesList;
    private static String driver;
    private static String url;

    static {
        propertiesList = propertiesParser();
        driver = driverLoader();
        url = urlLoader();
    }

//    private static Connection conn;
//
//    public static Connection getConnection() throws SQLException, ClassNotFoundException {
//        if (conn == null) {
//            Class.forName(driver);
//            conn = DriverManager.getConnection(url);
//        }
//        return conn;
//    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {

        Class.forName(driver);

        return DriverManager.getConnection(url);
    }

//    public static Connection getConn() {
//        return conn;
//    }

    public static List<String> propertiesParser() {
        try (BufferedReader bis = new BufferedReader(new FileReader(dbPropertiesURL))) {
            List<String> propertiesList = new ArrayList<>();
            String propertiesLine;
            while ((propertiesLine = bis.readLine()) != null) {
                propertiesList.add(propertiesLine);
            }

            return propertiesList;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String driverLoader() {
        String driverString = "driver=";
        return propertiesList.stream()
                .filter(property -> property.contains(driverString))
                .map(property -> property.split("=")[1])
                .findFirst().orElse(null);
    }

    public static String urlLoader() {
        String urlPropertyName = "url=";
        return propertiesList.stream().filter(property -> property.contains(urlPropertyName))
                .map(property -> property.split("=")[1]).findFirst().orElse("URL not found");
    }
}


