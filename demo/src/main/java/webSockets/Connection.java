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
    public void sendData(String ip, int[][] list) {
        template.convertAndSend(
            "/server/"+ip,
            list
        );
    }

    @MessageMapping("/data")
    private void receiveMessage(int[][] message) {

    }

    @MessageMapping("ip")
    private void recieveIP(String message) {
        users.putIfAbsent(message.replace("\"", ""), new int[][]{{0,(int)(Math.random()*100),0}});
    }

    public void startLoop(){
        if (t != null){
            return;
        }
        t = new Thread(()->{
            while (true){
                for (String ip : users.keySet()){
                    System.out.println("/server/"+ip);
                    sendData(ip, users.get(ip));
                }
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