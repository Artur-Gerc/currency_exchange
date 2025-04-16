package ru.edu.currency_exchange;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        Map<String, Integer> linkedHashMap = new LinkedHashMap<>();
        Map<String, Integer> hashMap = new HashMap<>();

        for (int i = 0; i < 20; i++) {
            linkedHashMap.put("Element " + i, i);
            hashMap.put("Element " + i, i);
        }

        System.out.println(linkedHashMap);
        for (Map.Entry<String, Integer> entry : linkedHashMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println(hashMap);
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

//        Connection con = DataBase.getConnection();
//        PreparedStatement ps = con.prepareStatement("select * from currencies");
//        ResultSet rs = ps.executeQuery();
//
//        List<Currency> currencies = new ArrayList<>();
//        while (rs.next()) {
//            Currency currency = new Currency();
//
//            currency.setId(rs.getInt("id"));
//            currency.setCode(rs.getString("code"));
//            currency.setFullName(rs.getString("full_name"));
//            currency.setSign(rs.getString("sign"));
//
//            currencies.add(currency);
//        }
//
//        currencies.forEach(System.out::println);
//
//        PreparedStatement ps2 = con.prepareStatement("select * from currencies where full_name like ?");
//        ps2.setString(1, "US%");
//        ResultSet resultSet = ps2.executeQuery();
//        Currency currency = null;
//        while(resultSet.next()){
//            currency = new Currency();
//            currency.setId(resultSet.getInt("id"));
//            currency.setCode(resultSet.getString("code"));
//            currency.setFullName(resultSet.getString("full_name"));
//            currency.setSign(resultSet.getString("sign"));
//        }
//
//        System.out.println();
//        System.out.println(currency);
//
//        System.out.println(DataBase.driverLoader());
//        System.out.println(DataBase.urlLoader());
//        CurrencyDao currencyDao = new CurrencyDao();

//        System.out.println(currencyDao.findAll());
//
//        System.out.println(currencyDao.findCurrencyByCode("USD"));

//        Currency currency = new Currency();
//        currency.setCode("ANY");
//        currency.setFullName("AN");
//        currency.setSign("¥");
//        currencyService.save(currency);

//        String s = "USDRUB";
//        System.out.println(s.substring(0,3));
//        System.out.println(s.substring(3));

//        ExchangeRatesDao exchangeRatesDao = new ExchangeRatesDao();
//        System.out.println(exchangeRatesDao.findAll());
//        System.out.println(exchangeRatesDao.findExchangeRateByCode(s));
//
//        ExchangeRate exchangeRate = new ExchangeRate();
//        Currency usd = currencyDao.findById(2).get();
//        Currency cny = currencyDao.findById(8).get();

//        exchangeRate.setBaseCurrency(usd);
//        exchangeRate.setTargetCurrency(cny);
//        exchangeRate.setRate(new BigDecimal("7.24"));
//        exchangeRatesService.save(exchangeRate);
//
//        exchangeRatesService.updateExchangeRate(s, "101");

//        Currency usd2 = new Currency();
//        usd2.setCode("USD3");
//        usd2.setFullName("USD3");
//        usd2.setSign("¥3");
//
//        currencyDao.save(usd2);


    }
}
