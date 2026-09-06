package game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import webSockets.Connection;

@SpringBootApplication(scanBasePackages = {"game", "webSockets"})
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

        Connection.getInstance().startLoop();
    }
}