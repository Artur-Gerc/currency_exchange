package ru.edu.currency_exchange.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.edu.currency_exchange.dao.CurrencyDao;
import ru.edu.currency_exchange.models.Currency;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyDao currencyDao;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        currencyDao = (CurrencyDao) context.getAttribute("currencyDao");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if(pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Валюта не указана");
            return;
        }

        // убираем /
        String code = pathInfo.substring(1).toUpperCase();
        int maxCodeLength = 3;

        if(code.length() > maxCodeLength) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Код валюты должен состоять из 3 символов");
        }

        Optional<Currency> currency;
        try {
            currency = currencyDao.findCurrencyByCode(code);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if(currency.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Такой валюты нет");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(HttpServletResponse.SC_OK);
        mapper.writerWithDefaultPrettyPrinter().writeValue(response.getWriter(), currency.get());
    }
}
