package demo.test.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;

import com.github.obase.webc.WebcServletContainerInitializer;

@SpringBootApplication
public class Main extends WebcServletContainerInitializer implements ServletContextInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
