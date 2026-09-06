package webSockets;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

@Controller
public class Connection {
    private static Thread t = null;
    
    private final SimpMessagingTemplate template;

    private static Connection instance;

    public static HashMap<String, int[][]> users = new HashMap<>();

    private Connection(SimpMessagingTemplate template) {
        System.out.println("HEEELOOOOO");
        this.template = template;
        instance = this;
    }

    public static Connection getInstance(){
        if (instance == null){
            instance = new Connection(null);
        }
        if (instance != null){
            System.out.println("HERETO");
        }
        return instance;
    }

    @SendTo("/server/data")
    public void sendData(int[][] list) {
        template.convertAndSend(
            "/server/data",
            list
        );
    }

    @MessageMapping("/data")
    private void receiveMessage(int[][] message) {

    }

    public void startLoop(){
        if (t != null){
            return;
        }
        t = new Thread(()->{
            while (true){
                sendData(new int[][]{{0,0,0},{0,0,0}});
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
}