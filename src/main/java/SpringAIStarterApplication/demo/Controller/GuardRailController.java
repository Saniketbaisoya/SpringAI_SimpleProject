package SpringAIStarterApplication.demo.Controller;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guardrail")
public class GuardRailController {
    
    private final ChatClient chatClient;

    public GuardRailController(ChatClient.Builder builder){
        this.chatClient = builder
        .defaultSystem("You are an Helpful and Safe AI Assistant")
        .defaultAdvisors(SafeGuardAdvisor.builder()
                         .sensitiveWords(List.of("Kill", "hack", "exploit", "violence", "weapons", "attack"))
                         .build())
        .build();
    }

    // http://localhost:8080/api/guardrail/chat
    @GetMapping("/chat")
    public String chat(@RequestParam String message){
        String response = chatClient.prompt()
                          .user(message)
                          .call()
                          .content();
        return response;
    }

}
