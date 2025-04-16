package ru.edu.currency_exchange.dao;

import ru.edu.currency_exchange.models.Currency;
import ru.edu.currency_exchange.models.ExchangeRate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesDao {
    private DataSource dataSource;
    private final CurrencyDao currencyDao;

    public ExchangeRatesDao(DataSource ds) throws SQLException, ClassNotFoundException {
        this.currencyDao = new CurrencyDao(ds);
        this.dataSource = ds;
    }

    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             ResultSet resultSet = connection.prepareStatement("SELECT * FROM exchange_rates").executeQuery()) {

            while (resultSet.next()) {
                exchangeRates.add(getExchangeRateFromDb(resultSet));
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return exchangeRates;
    }

    public Optional<ExchangeRate> findById(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "select * from exchange_rates where id=?")) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(getExchangeRateFromDb(resultSet));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public void save(ExchangeRate exchangeRate) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement
                     ("Insert Into exchange_rates (base_currency_id, target_currency_id, rate) values (?,?,?)")) {

            preparedStatement.setInt(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ExchangeRate> findExchangeRateByCode(String currencyCode) throws SQLException, ClassNotFoundException {

        int codeLengthInUrl = 6;
        if (currencyCode.length() != codeLengthInUrl) {
            return Optional.empty();
        }

        String baseCurrencyString = currencyCode.substring(0, 3);
        Optional<Currency> baseCurrency = currencyDao.findCurrencyByCode(baseCurrencyString);

        if (baseCurrency.isEmpty()) {
            return Optional.empty();
        }

        String targetCurrencyString = currencyCode.substring(3);
        Optional<Currency> targetCurrency = currencyDao.findCurrencyByCode(targetCurrencyString);

        if (targetCurrency.isEmpty()) {
            return Optional.empty();
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement
                     ("SELECT * FROM exchange_rates WHERE base_currency_id=? AND target_currency_id=?")) {

            preparedStatement.setInt(1, baseCurrency.get().getId());
            preparedStatement.setInt(2, targetCurrency.get().getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(getExchangeRateFromDb(resultSet));
                }
            }
        }
        return Optional.empty();
    }


    private ExchangeRate getExchangeRateFromDb(ResultSet resultSet) throws SQLException, ClassNotFoundException {
        ExchangeRate exchangeRate = new ExchangeRate();
        // устанавливаем id курса обмена
        exchangeRate.setId(resultSet.getInt("exchange_id"));

        // получаем id базовой валюты
        int baseCurrencyId = resultSet.getInt("base_currency_id");
        // получаем id целевой валюты
        int targetCurrencyId = resultSet.getInt("target_currency_id");

        // получаем объекты валют с помощью currencyService
        Optional<Currency> baseCurrency = currencyDao.findById(baseCurrencyId);
        Optional<Currency> targetCurrency = currencyDao.findById(targetCurrencyId);

        if (baseCurrency.isEmpty() || targetCurrency.isEmpty()) {
            throw new SQLException("Валюта не найдена в базе данных");
        }

        // Устанавливаем валюты в объект exchangeRate
        exchangeRate.setBaseCurrency(baseCurrency.get());
        exchangeRate.setTargetCurrency(targetCurrency.get());

        //устанавливаем курс обмена
        exchangeRate.setRate(resultSet.getBigDecimal("rate"));

        return exchangeRate;
    }

    public void delete(ExchangeRate exchangeRate) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "delete from exchange_rates where = ?")) {

            preparedStatement.setInt(1, exchangeRate.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(ExchangeRate exchangeRate) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "update exchange_rates set rate=? where base_currency_id=? AND target_currency_id=?")) {

            preparedStatement.setBigDecimal(1, exchangeRate.getRate());
            preparedStatement.setInt(2, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setInt(3, exchangeRate.getTargetCurrency().getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


//    public void updateExchangeRate(String currenciesCode, String rate) throws SQLException, ClassNotFoundException {
//        int codeLength = 3;
//        String baseCurrencyString = currenciesCode.substring(0, codeLength);
//        Currency baseCurrency = currencyService.getCurrencyByCode(baseCurrencyString).get();
//
//        String targetCurrencyString = currenciesCode.substring(codeLength);
//        Currency targetCurrency = currencyService.getCurrencyByCode(targetCurrencyString).get();
//
//        try (Connection connection = DataBase.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(
//                     "update exchange_rates set rate=? where base_currency_id=? and target_currency_id=?")) {
//
//            preparedStatement.setBigDecimal(1, new BigDecimal(rate));
//            preparedStatement.setInt(2, baseCurrency.getId());
//            preparedStatement.setInt(3, targetCurrency.getId());
//            preparedStatement.executeUpdate();
//        }
//    }

    public BigDecimal getUsdExchangeRate(Currency from, Currency to) throws SQLException, ClassNotFoundException {
        final String USD_CODE = "USD";

        if (from.getCode().equals(USD_CODE)) {
            return findExchangeRateByCode(from.getCode() + to.getCode()).get().getRate();
        } else if (to.getCode().equals(USD_CODE)) {
            return findExchangeRateByCode(to.getCode() + from.getCode()).get().getRate();
        } else {
            BigDecimal usdA = findExchangeRateByCode(USD_CODE + from.getCode()).get().getRate();
            BigDecimal usdB = findExchangeRateByCode(USD_CODE + to.getCode()).get().getRate();
            return usdB.divide(usdA, 2, BigDecimal.ROUND_HALF_UP);
        }
    }
}
