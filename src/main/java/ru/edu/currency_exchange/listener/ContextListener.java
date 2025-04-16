package ru.edu.currency_exchange.listener;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.edu.currency_exchange.dao.CurrencyDao;
import ru.edu.currency_exchange.dao.ExchangeRatesDao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

@WebListener
public class ContextListener implements ServletContextListener {

    public ContextListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        try {
            HikariDataSource ds = getDataSource(context);
            ExchangeRatesDao exchangeRatesDao = new ExchangeRatesDao(ds);
            CurrencyDao currencyDao = new CurrencyDao(ds);

            context.setAttribute("exchangeRatesDao", exchangeRatesDao);
            context.setAttribute("currencyDao", currencyDao);
            context.setAttribute("dataSource", ds);

        } catch (SQLException | ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HikariDataSource ds = (HikariDataSource) sce.getServletContext().getAttribute("dataSource");
        if (ds != null) {
            ds.close();
        }
    }

    public static HikariDataSource getDataSource(ServletContext context) throws IOException {
        Properties props = new Properties();
        try (InputStream inputStream = context.getResourceAsStream("/WEB-INF/classes/db.properties")) {
            if (inputStream == null) {
                throw new IOException("Unable to find db.properties in classpath");
            }
            props.load(inputStream);
        }

        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(props.getProperty("driver"));
        ds.setJdbcUrl(props.getProperty("url"));
        return ds;

    }
}
