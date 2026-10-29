package SpringAIStarterApplication.demo.Controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ChatController {
    
    private ChatClient chatClient;

    public ChatController(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }

    @GetMapping("/simpleChat/{message}")
    public String simpleChat(@PathVariable String message){
        String response = chatClient.prompt()
                          .user(message)
                          .call()
                          .content();
        
        return response;
    }
}
