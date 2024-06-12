package digit.web.controllers;

import digit.web.models.ErrorRes;
import digit.web.models.SchemaMigrationRequest;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import digit.TestConfiguration;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
* API tests for SchemaApiController
*/
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(SchemaApiController.class)
@Import(TestConfiguration.class)
public class SchemaApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void migrateSchemaDefinitionSuccess() throws Exception {
        mockMvc.perform(post("/schema/v1/_migrate").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
    }

    @Test
    public void migrateSchemaDefinitionFailure() throws Exception {
        mockMvc.perform(post("/schema/v1/_migrate").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isBadRequest());
    }

}
