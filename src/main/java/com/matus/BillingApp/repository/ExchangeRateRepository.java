package com.matus.BillingApp.repository;

import com.matus.BillingApp.domain.ExchangeRate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRateRepository extends MongoRepository<ExchangeRate, String> {

    List<ExchangeRate> findByDate(String date);

}
