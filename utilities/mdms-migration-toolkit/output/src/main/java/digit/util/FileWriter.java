package digit.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;

import static digit.constants.ErrorCodes.*;
import static digit.constants.MDMSMigrationToolkitConstants.*;

@Component
public class FileWriter {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${master.schema.files.dir}")
    private String masterSchemaFilesDirectory;

    public void writeJsonToFile(JsonNode content, String fileName) {
        try {
            objectMapper.writeValue(new File(masterSchemaFilesDirectory, fileName + JSON_EXTENSION), content);
        }catch (Exception e){
            throw new CustomException(IO_ERROR_CODE, IO_WRITE_ERROR_MESSAGE);
        }

    }

}
