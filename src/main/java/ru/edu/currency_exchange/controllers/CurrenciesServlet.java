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
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet(value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyDao currencyDao;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context  = config.getServletContext();
        currencyDao = (CurrencyDao) context.getAttribute("currencyDao");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        PrintWriter out = res.getWriter();
        List<Currency> currencies = currencyDao.findAll();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(out, currencies);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if(name == null || code == null || sign == null) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Параметры пропущены");
            return;
        }

        Optional<Currency> checkCurrency;
        try {
             checkCurrency = currencyDao.findCurrencyByCode(code);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if(checkCurrency.isPresent()) {
            res.sendError(HttpServletResponse.SC_CONFLICT, "Валюта с таким кодом уже есть");
            return;
        }

        Currency currency = new Currency();
        currency.setFullName(name);
        currency.setCode(code);
        currency.setSign(sign);

        currencyDao.save(currency);

        ObjectMapper mapper = new ObjectMapper();
        res.setStatus(HttpServletResponse.SC_CREATED);
        PrintWriter out = res.getWriter();
        mapper.writerWithDefaultPrettyPrinter().writeValue(out, currency);
    }

    public void destroy() {}

}
