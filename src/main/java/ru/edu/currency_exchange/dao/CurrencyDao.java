package ru.edu.currency_exchange.dao;

import ru.edu.currency_exchange.models.Currency;
import ru.edu.currency_exchange.repository.CurrencyRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao  implements CurrencyRepository<Currency> {
    private DataSource dataSource;

    public CurrencyDao(DataSource ds) {
        this.dataSource = ds;
    }

    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from currencies");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Currency currency = createCurrency(resultSet);
                currencies.add(currency);
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return currencies;
    }

    public Optional<Currency> findById(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from currencies where id = ?")) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(createCurrency(resultSet));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Currency> findCurrencyByCode(String code) throws SQLException, ClassNotFoundException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from currencies where code = ?")) {

            preparedStatement.setString(1, code);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(createCurrency(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    private Currency createCurrency(ResultSet rs) throws SQLException, ClassNotFoundException {
        Currency currency = null;

        currency = new Currency();
        currency.setId(rs.getInt("id"));
        currency.setCode(rs.getString("code"));
        currency.setFullName(rs.getString("full_name"));
        currency.setSign(rs.getString("sign"));

        return currency;
    }

    public void save(Currency currency) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("insert into currencies(code, full_name, sign) values(?,?,?)")) {

            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Currency currency) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "delete from currencies where id=?")) {

            preparedStatement.setInt(1, currency.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Currency currency) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "update currencies set code=?, full_name=?, sign=?")) {

            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
