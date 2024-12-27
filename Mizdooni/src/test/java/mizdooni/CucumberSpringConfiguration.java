package mizdooni;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = MizdooniApplication.class)
public class CucumberSpringConfiguration {
}
