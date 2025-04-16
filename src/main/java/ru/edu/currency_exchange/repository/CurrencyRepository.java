package ru.edu.currency_exchange.repository;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository<T> {
    Optional<T> findById(int id);
    List<T> findAll();
    void save(T t);
    void delete(T t);
    void update(T t);
}
