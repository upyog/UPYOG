package org.egov.infra.web.spring.tiles;

import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletRequest;
import org.apache.tiles.request.servlet.ServletUtil;

import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Vendored replacement for the (now removed) Spring Framework class
 * org.springframework.web.servlet.view.tiles3.TilesView.
 *
 * Spring dropped its Tiles 3 integration module starting with Spring
 * Framework 5.3/6.x. Apache Tiles itself is unmaintained upstream, so no
 * official Jakarta-namespace successor exists. This class is adapted from
 * the last available Spring Framework (Apache License 2.0) source of that
 * module, updated only to use jakarta.servlet.* instead of javax.servlet.*
 * and the com.github.tntim96.apache.tiles fork's API.
 *
 * This is infrastructure/view-layer glue code only — no eGov business logic.
 * Revisit if/when the project moves off Apache Tiles entirely.
 */
public class TilesView extends AbstractUrlBasedView {

    private TilesContainer container;

    @Override
    protected void initApplicationContext() {
        super.initApplicationContext();
        ServletContext servletContext = getServletContext();
        ApplicationContext tilesApplicationContext = ServletUtil.getApplicationContext(servletContext);
        this.container = TilesAccess.getContainer(tilesApplicationContext);
    }

    @Override
    protected void renderMergedOutputModel(
            Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Request tilesRequest = createTilesRequest(request, response);
        if (!this.container.isValidDefinition(getUrl(), tilesRequest)) {
            throw new ServletException("Could not find tiles definition '" + getUrl() + "'");
        }
        exposeModelAsRequestAttributes(model, request);
        this.container.render(getUrl(), tilesRequest);
    }

    /**
     * Create a Tiles {@link Request} wrapping the current servlet request/response.
     */
    protected Request createTilesRequest(HttpServletRequest request, HttpServletResponse response) {
        ApplicationContext tilesApplicationContext = ServletUtil.getApplicationContext(getServletContext());
        return new ServletRequest(tilesApplicationContext, request, response);
    }

}