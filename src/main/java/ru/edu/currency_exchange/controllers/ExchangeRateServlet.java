package ru.edu.currency_exchange.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.edu.currency_exchange.dao.ExchangeRatesDao;
import ru.edu.currency_exchange.models.ExchangeRate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/exchangerate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRatesDao exchangeRatesDao;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        ServletContext context = config.getServletContext();

        exchangeRatesDao = (ExchangeRatesDao) context.getAttribute("exchangeRatesDao");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();

        if (path == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String code = path.substring(1).toUpperCase();

        Optional<ExchangeRate> exchangeRate;
        try {
            exchangeRate = exchangeRatesDao.findExchangeRateByCode(code);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (exchangeRate.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Обменный курс для пары не найден");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(out, exchangeRate.get());
        out.close();
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("PATCH".equals(request.getMethod())) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo().substring(1).toUpperCase();
        String rateInRequest = readBodyRequest(request);

        if (rateInRequest == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует поле с курсом");
            return;
        }

        Optional<ExchangeRate> checkExchangeRate;
        try {
            checkExchangeRate = exchangeRatesDao.findExchangeRateByCode(path);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (checkExchangeRate.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Такой валютной пары нет");
            return;
        }

        checkExchangeRate.get().setRate(new BigDecimal(rateInRequest));
        exchangeRatesDao.update(checkExchangeRate.get());
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(out, checkExchangeRate.get());
        out.close();
    }

    public String readBodyRequest(HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("rate")) {
                    String[] s = line.split("=");
                    return s[1];
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
