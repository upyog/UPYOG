package org.egov.infra.web.spring.tiles;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * Vendored replacement for the (now removed) Spring Framework class
 * org.springframework.web.servlet.view.tiles3.TilesViewResolver.
 *
 * See org.egov.infra.web.spring.tiles.TilesView for background on why
 * this class is vendored locally. This is infrastructure/view-layer glue
 * code only — no eGov business logic. Revisit if/when the project moves
 * off Apache Tiles entirely.
 */
public class TilesViewResolver extends UrlBasedViewResolver {

    public TilesViewResolver() {
        setViewClass(requiredViewClass());
    }

    @Override
    protected Class<?> requiredViewClass() {
        return TilesView.class;
    }

}