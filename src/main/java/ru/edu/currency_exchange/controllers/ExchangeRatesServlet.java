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
import ru.edu.currency_exchange.dao.ExchangeRatesDao;
import ru.edu.currency_exchange.models.Currency;
import ru.edu.currency_exchange.models.ExchangeRate;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangerates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRatesDao exchangeRatesDao;
    private CurrencyDao currencyDao;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();

        exchangeRatesDao = (ExchangeRatesDao) context.getAttribute("exchangeRatesDao");
        currencyDao = (CurrencyDao) context.getAttribute("currencyDao");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<ExchangeRate> exchangeRatesList = exchangeRatesDao.findAll();

        if (exchangeRatesList.isEmpty()) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(out, exchangeRatesList);

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String baseCurrencyCodeString = request.getParameter("baseCurrencyCode");
        String targetCurrencyCodeString = request.getParameter("targetCurrencyCode");
        String exchangeRateString = request.getParameter("rate");

        if(baseCurrencyCodeString == null || targetCurrencyCodeString == null || exchangeRateString == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Пропущен требуемый параметр");
            return;
        }

        Optional<Currency> baseCurrency;
        Optional<Currency> targetCurrency;

        try {
            baseCurrency = currencyDao.findCurrencyByCode(baseCurrencyCodeString);
            targetCurrency = currencyDao.findCurrencyByCode(targetCurrencyCodeString);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if(baseCurrency.isEmpty() || targetCurrency.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Таких валют нет в системе");
            return;
        }

        Optional<ExchangeRate> exchangeRate;
        try {
            exchangeRate = exchangeRatesDao.findExchangeRateByCode(baseCurrencyCodeString + targetCurrencyCodeString);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if(exchangeRate.isPresent()){
            response.sendError(HttpServletResponse.SC_CONFLICT, "Валютная пара уже есть в системе");
            return;
        }

        ExchangeRate newExchangeRate = new ExchangeRate();
        newExchangeRate.setBaseCurrency(baseCurrency.get());
        newExchangeRate.setTargetCurrency(targetCurrency.get());
        newExchangeRate.setRate(new BigDecimal(exchangeRateString));

        exchangeRatesDao.save(newExchangeRate);

        PrintWriter out = response.getWriter();
        response.setStatus(HttpServletResponse.SC_CREATED);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(out, newExchangeRate);
    }
}
