package org.egov.egf.web.controller.scheme;


import org.egov.commons.Scheme;
import org.egov.commons.dao.SchemeHibernateDAO;
import org.egov.services.masters.SchemeService;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * REST controller responsible for providing scheme lookup and
 * autocomplete functionality for budget and finance modules.
 *
 * <p>
 * This controller exposes AJAX endpoints that allow users to search
 * schemes by name or code while creating or updating financial records.
 * The endpoints return JSON responses that can be consumed by
 * autocomplete fields, dropdowns, and other client-side components.
 * </p>
 *
 * <h3>Primary Responsibilities</h3>
 * <ul>
 *     <li>Provide scheme search functionality using partial text matching.</li>
 *     <li>Support AJAX-based autocomplete components.</li>
 *     <li>Return scheme master data in JSON format.</li>
 * </ul>
 *
 * <h3>Supported Endpoints</h3>
 * <ul>
 *     <li>
 *         <b>GET /scheme/ajaxSchemes</b> -
 *         Search schemes by name or code.
 *     </li>
 * </ul>
 *
 * <h3>Response Format</h3>
 * <ul>
 *     <li>Returns a JSON array of matching {@link Scheme} entities.</li>
 * </ul>
 *
 * <h3>Security</h3>
 * <ul>
 *     <li>Input parameters are validated using {@code @SafeHtml}.</li>
 * </ul>
 *
 * @see Scheme
 * @see SchemeHibernateDAO
 * @see SchemeService
 */

@Controller()
@RequestMapping("/scheme")
public class SchemeRestController {


    @Autowired
    private SchemeHibernateDAO schemeHibernateDAO;



    @GetMapping(value = "/ajaxSchemes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Scheme> findSchemesByNameOrCode(@RequestParam @SafeHtml final String query) {
        final List<Scheme> schemes = schemeHibernateDAO.getSchemeByNameOrCode(query);
        return schemes;
    }



}
