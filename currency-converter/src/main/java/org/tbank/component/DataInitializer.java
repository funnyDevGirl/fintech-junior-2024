package org.tbank.component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.tbank.enums.RoleType;
import org.tbank.model.Role;
import org.tbank.repository.RoleRepository;
import org.tbank.service.CurrencyRateService;

@Slf4j
@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CurrencyRateService currencyRateService;
    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        currencyRateService.fetchAndSaveToDBCurrencyRates();

        initRoles();
    }

    private void initRoles() {
        for (RoleType roleType : RoleType.values()) {
            Role role = new Role(roleType);

            log.info("Role '{}' begins initialization", role.getName());

            roleRepository.save(role);
        }

        log.info("{} roles have been successfully initialized", RoleType.values().length);
    }
}
