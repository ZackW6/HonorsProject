package webSockets;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.json.GsonEncoder;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;

import game.Game;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class Connection {
    private static Thread t = null;
    
    private final SimpMessagingTemplate template;

    private static Connection instance;

    private static final Gson gson = new Gson();

    public static ConcurrentHashMap<String, Map<String, Object>> users = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Map<String, Object>> screens = new ConcurrentHashMap<>();

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
        String json = gson.toJson(screens.getOrDefault(ip, new HashMap<String, Object>()));
        byte[] bytes = new byte[]{};
        try {
            bytes = CompressJSON.compress(json);
        } catch (IOException e) {
            System.out.println("AHHHHHH");
        }
        String text = Base64.getEncoder().encodeToString(bytes);
        // template.convertAndSend("/server/"+ip, (Object)screens.getOrDefault(ip, new HashMap<String, Object>()));
        template.convertAndSend("/server/"+ip, text);
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
        }, "renderLoop");
        t.setDaemon(true);
        t.start();
    }

    public void updateUserScreen(String ip){
        if (!users.get(ip).containsKey("windowWidth")){
            return;
        }
        Map<String, Object> user = users.get(ip);
        List<int[]> viewableGameElements = Game.getViewableGameElements(((Number)user.get("windowCenterX")).intValue(), ((Number)user.get("windowCenterY")).intValue(), ((Number)user.get("windowWidth")).intValue(), ((Number)user.get("windowHeight")).intValue());
        screens.get(ip).put("Creatures", viewableGameElements);
    }
}