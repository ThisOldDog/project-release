package pres.teemo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pres.teemo.config.RunnerProperties;
import pres.teemo.task.Runner;

@EnableConfigurationProperties(RunnerProperties.class)
@SpringBootApplication
public class ReleaseApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ReleaseApplication.class)
                .profiles("1.6-beta")
                .run(args)
                .getBean(Runner.class)
                .run();
    }
}

