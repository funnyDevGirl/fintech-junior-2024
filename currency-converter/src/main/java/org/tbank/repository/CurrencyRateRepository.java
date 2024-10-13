package org.tbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.tbank.model.CurrencyRate;
import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long>, JpaSpecificationExecutor<CurrencyRate> {
    Optional<CurrencyRate> findByCurrency(String code);
}
