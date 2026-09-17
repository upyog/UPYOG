package org.egov.infra.web.spring.tiles;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletContext;

import org.apache.tiles.factory.AbstractTilesContainerFactory;
import org.apache.tiles.factory.BasicTilesContainerFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.servlet.ServletApplicationContext;
import org.apache.tiles.startup.AbstractTilesInitializer;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

/**
 * Vendored replacement for the (now removed) Spring Framework class
 * org.springframework.web.servlet.view.tiles3.TilesConfigurer.
 *
 * See org.egov.infra.web.spring.tiles.TilesView for background on why
 * this class is vendored locally. This is infrastructure/view-layer glue
 * code only — no eGov business logic. Revisit if/when the project moves
 * off Apache Tiles entirely.
 */
public class TilesConfigurer implements ServletContextAware, InitializingBean, DisposableBean {

    private String[] definitions;

    private ServletContext servletContext;

    private TilesInitializerAdapter tilesInitializer;

    /**
     * Set the Tiles definition list, e.g. {@code /WEB-INF/layout/tiles.xml}.
     * Matches the "definitions" property used by the old Spring TilesConfigurer.
     */
    public void setDefinitions(String[] definitions) {
        this.definitions = definitions;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServletApplicationContext preliminaryContext = new ServletApplicationContext(this.servletContext);
        this.tilesInitializer = new TilesInitializerAdapter();
        this.tilesInitializer.initialize(preliminaryContext);
    }

    @Override
    public void destroy() throws Exception {
        if (this.tilesInitializer != null) {
            this.tilesInitializer.destroy();
        }
    }

    /**
     * Bridges our configured {@code definitions} list into Tiles' own
     * initializer machinery.
     */
    private class TilesInitializerAdapter extends AbstractTilesInitializer {

        @Override
        protected AbstractTilesContainerFactory createContainerFactory(ApplicationContext context) {
            return new BasicTilesContainerFactory() {
                @Override
                protected List<ApplicationResource> getSources(ApplicationContext applicationContext) {
                    List<ApplicationResource> result = new ArrayList<>();
                    if (definitions != null) {
                        for (String definition : definitions) {
                            ApplicationResource resource = applicationContext.getResource(definition);
                            if (resource != null) {
                                result.add(resource);
                            }
                        }
                    }
                    return result;
                }
            };
        }
    }

}