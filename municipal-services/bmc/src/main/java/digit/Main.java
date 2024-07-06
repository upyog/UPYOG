package digit;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
//@Import({ TracerConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "digit", "digit.web.controllers" , "digit.config"})
//@EnableJpaRepositories(basePackages = "digit.repository")
@EntityScan(basePackages = {"digit.web.models", "digit.bmc.model"})
public class Main {


    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        
    }

}
