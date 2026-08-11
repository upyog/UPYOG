package org.upyog.reconciliation.validation;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReconciliationValidationEngine {

    // Note: KieContainer would typically be configured via a @Configuration class.
    // For now, we stub it or assume it's injected.
    @Autowired(required = false)
    private KieContainer kieContainer;

    public ValidationResult validate(List<Map<String, Object>> dataset, String tenantId, String module) {
        ValidationResult result = new ValidationResult();

        if (kieContainer == null) {
            // Stubbed behavior if container is not fully set up
            return result;
        }

        KieSession kieSession = kieContainer.newKieSession();

        // Inject globals for feedback collection
        kieSession.setGlobal("validationResult", result);
        kieSession.setGlobal("tenantId", tenantId);
        kieSession.setGlobal("module", module);

        for (Map<String, Object> record : dataset) {
            kieSession.insert(record);
        }

        kieSession.fireAllRules();
        kieSession.dispose();

        return result;
    }
}
