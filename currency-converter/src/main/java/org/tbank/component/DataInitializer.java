package org.tbank.component;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.tbank.config.AppConfig;
import org.tbank.dto.roles.RoleCreateDTO;
import org.tbank.mapper.RoleMapper;
import org.tbank.model.Role;
import org.tbank.repository.RoleRepository;
import org.tbank.service.CurrencyRateService;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CurrencyRateService currencyRateService;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final AppConfig appConfig;

    @Override
    public void run(ApplicationArguments args) {
        currencyRateService.fetchAndSaveToDBCurrencyRates();


        RoleCreateDTO adminCreateDTO = new RoleCreateDTO(appConfig.getAdminRoleName());
        Role admin = roleMapper.toRole(adminCreateDTO);
        roleRepository.save(admin);

        RoleCreateDTO userRoleCreateDTO = new RoleCreateDTO(appConfig.getDefaultRoleName());
        Role userRole = roleMapper.toRole(userRoleCreateDTO);
        roleRepository.save(userRole);
    }
}
