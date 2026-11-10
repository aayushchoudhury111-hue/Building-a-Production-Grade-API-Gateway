package in.chillitz.gatekeeper.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication
public class ApiManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiManagementApplication.class, args);
    }

}
