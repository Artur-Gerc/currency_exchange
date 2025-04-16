package ru.edu.currency_exchange.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.edu.currency_exchange.dao.CurrencyDao;
import ru.edu.currency_exchange.dao.ExchangeRatesDao;
import ru.edu.currency_exchange.dto.Exchange;
import ru.edu.currency_exchange.models.Currency;
import ru.edu.currency_exchange.util.ExchangeError;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private CurrencyDao currencyDao;
    private ExchangeRatesDao exchangeRatesDao;
    private ExchangeError exchangeError;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();

        currencyDao = (CurrencyDao) context.getAttribute("currencyDao");
        exchangeRatesDao = (ExchangeRatesDao) context.getAttribute("exchangeRatesDao");
        exchangeError = new ExchangeError();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, RuntimeException {
        String fromString = request.getParameter("from");
        String toString = request.getParameter("to");
        String amount = request.getParameter("amount");

        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();

        if (fromString == null) {
            exchangeError.setMessage("Валюта, из которой конвертируют, не введена");
            objectWriter.writeValue(out, exchangeError);
            return;
        }

        if (toString == null) {
            exchangeError.setMessage("Валюта, в которую конвертируют, не введена");
            objectWriter.writeValue(out, exchangeError);
            return;
        }

        if (amount == null) {
            exchangeError.setMessage("Количество валюты для конвертации не задано");
            objectWriter.writeValue(out, exchangeError);
            return;
        }

        Optional<Currency> fromCurrency;
        Optional<Currency> toCurrency;

        try {
            fromCurrency = currencyDao.findCurrencyByCode(fromString.toUpperCase());
            toCurrency = currencyDao.findCurrencyByCode(toString.toUpperCase());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (fromCurrency.isEmpty()) {
            exchangeError.setMessage("Валюта, из которой конвертируют, не найдена");
            objectWriter.writeValue(out, exchangeError);
            return;
        }

        if (toCurrency.isEmpty()) {
            exchangeError.setMessage("Валюта, в которую конвертируют, не найдена");
            objectWriter.writeValue(out, exchangeError);
            return;
        }

        /*получаем итоговый курс*/
        BigDecimal convertedAmount;
        BigDecimal amountToBigDecimal;
        BigDecimal conversionRate;
        try {
            conversionRate = exchangeRatesDao.getUsdExchangeRate(fromCurrency.get(), toCurrency.get());
            amountToBigDecimal = new BigDecimal(amount);
            convertedAmount = conversionRate.multiply(amountToBigDecimal);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Exchange exchange = new Exchange();
        exchange.setBaseCurrency(fromCurrency.get());
        exchange.setTargetCurrency(toCurrency.get());
        exchange.setAmount(amountToBigDecimal);
        exchange.setConvertedAmount(convertedAmount);
        exchange.setRate(conversionRate);

        objectWriter.writeValue(out, exchange);

    }


}
