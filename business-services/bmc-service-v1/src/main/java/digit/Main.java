package digit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;
//@Import({ TracerConfiguration.class })

@SpringBootApplication
@Slf4j
public class Main {


    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        
    }

}
