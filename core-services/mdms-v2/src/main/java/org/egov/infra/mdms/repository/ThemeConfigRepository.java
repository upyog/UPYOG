package org.egov.infra.mdms.repository;

import org.egov.infra.mdms.model.ThemeConfig;

public interface ThemeConfigRepository {

    void create(ThemeConfig themeConfig);

    void createStaging(ThemeConfig themeConfig);

    void update(ThemeConfig themeConfig);

    ThemeConfig search(String tenantId, String themeType);

    // Checks if employee already has a pending modification request
    boolean existsPendingTheme(String tenantId, String themeType);
}
