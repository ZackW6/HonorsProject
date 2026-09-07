package webSockets;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import game.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class Connection {
    private static Thread t = null;
    
    private final SimpMessagingTemplate template;

    private static Connection instance;

    public static HashMap<String, Map<String, Object>> users = new HashMap<>();
    public static HashMap<String, Map<String, Object>> screens = new HashMap<>();

    private Connection(SimpMessagingTemplate template) {
        this.template = template;
        instance = this;
    }

    public static Connection getInstance(){
        if (instance == null){
            instance = new Connection(null);
        }
        return instance;
    }

    public void sendData(String ip) {
        template.convertAndSend(
            "/server/"+ip,
            (Object)screens.getOrDefault(ip, new HashMap<String, Object>())
        );
    }

    @MessageMapping("/data")
    private void receiveMessage(Map<String, Object> message) {
        users.put((String)message.get("totalPath"), message);
    }

    @MessageMapping("ip")
    private void recieveIP(String message) {
        users.putIfAbsent(message.replace("\"", ""), new HashMap<String, Object>());
        screens.putIfAbsent(message.replace("\"", ""), new HashMap<String, Object>());
    }

    public void startLoop(){
        if (t != null){
            return;
        }
        t = new Thread(()->{
            while (true){
                for (String ip : users.keySet()){
                    updateUserScreen(ip);
                    sendData(ip);
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

    public void updateUserScreen(String ip){
        if (!users.get(ip).containsKey("windowWidth")){
            return;
        }
        Map<String, Object> user = users.get(ip);
        List<int[]> viewableGameElements = Test.getViewableGameElements(((Number)user.get("windowZeroX")).intValue(), ((Number)user.get("windowZeroY")).intValue(), ((Number)user.get("windowWidth")).intValue(), ((Number)user.get("windowHeight")).intValue());
        screens.get(ip).put("Creatures", viewableGameElements);
    }
}