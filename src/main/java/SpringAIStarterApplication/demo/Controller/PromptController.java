package SpringAIStarterApplication.demo.Controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prompt-chat")
public class PromptController {
    
    private final ChatClient chatClient;

    public PromptController( ChatClient.Builder builder){
        this.chatClient = builder.build();
    }

    /*  http://localhost:8080/api/prompt-chat/zero-shot/chat */
    @GetMapping("/zero-shot/chat")
    public String zeroShot(@RequestParam String message){
        String response = chatClient.prompt()
                          .user(u -> u.text("""
                            Classify the Sentiment with the following text as exactly one of: Positive, Negative or Neutral.
                            Only respond with the Sentiment Label, nothing else.

                            Text: {message}
                            """).param("message", message))
                          .call()
                          .content();

        return response;
    }

    /*  http://localhost:8080/api/prompt-chat/few-shot/chat */
    @GetMapping("/few-shot/chat")
    public String fewShot(@RequestParam String message){
        String result = chatClient.prompt()
                          .user(u -> u.text("""
                            Tell me the time complexity of Algorithm based on the algorithm name given.
                            Only respond with the time complexity, nothing else.

                            Examples: 
                            Text: Merge Sort
                            Time Complexity: O(n log n)
                            Text: Quick Sort
                            Time Complexity: O(n log n)
                            Text: Bubble Sort
                            Time Complexity: O(n^2)
                            Text: Selection Sort
                            Time Complexity: O(n^2)
                            Text: Insertion Sort
                            Time Complexity: O(n^2)

                            Text: {AlgorithmName}
                          """).param("AlgorithmName", message))
                          .call()
                          .content();
        return result;
    }

    /* http://localhost:8080/api/prompt-chat/one-shot/chat */
    @GetMapping("/one-shot/chat")
    public String oneShot(@RequestParam String message){
        String result = chatClient.prompt()
                        .user(u -> u.text(""" 
                            Tell me a single add on product that can be recommended to a user based on the user's current cart items.
                            Only respond with the product name, nothing else. Assume we are working on a food e-commerce website.
                            
                            Example:
                            Text: "pizza", "burger", "coke"
                            Recommended Add on Product: "chips"

                            Text: {cartItems}
                            """).param("cartItems", message))
                        .call()
                        .content();

        return result;
    }
}
