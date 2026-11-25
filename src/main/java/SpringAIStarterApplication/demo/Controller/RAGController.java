package SpringAIStarterApplication.demo.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/rag")
public class RAGController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RAGController(ChatClient.Builder builder, VectorStore store ){
        this.vectorStore = store;
        this.chatClient = builder
                            .defaultSystem("""
                                
                                You are a helpful GTA VI assistant. 
                                Answer user questions using only the information provided in the retrieved GTA VI documents.

                                Provide accurate, clear, and concise answers. 
                                

                                Do not assume or hallucinate information. 
                                Keep responses relevant to GTA VI and its characters, story, locations, platforms, and features.

                            """)
                        .build();
        
                          
    }
    
    /* http://localhost:8080/api/rag/upload */

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadDocuments(@RequestParam("files") List<MultipartFile> files) throws IOException{
        int totalChunks = 0;
        List<String> processed = new ArrayList<>();

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                                     .withChunkSize(512) // Now yeah function hai, jisme voh actual ek chunk ka size likha hai jo voh token bnte time match krega, yani ek chunk 512 ka hoga as token....
                                     .withMinChunkSizeChars(100) // Now yeah hai ki minimum 100 number of chunks create hoge....
                                     .withMinChunkLengthToEmbed(50) // Now yeah function define krta hai ki, embedding model ek baar mai 50 chunks ko lekr, usko vector main convert krke, store krega vector db mai.....
                                     .withMaxNumChunks(1000) // Now yeah function btata hai ki koi bhi type ki document hogi, or jb voh split hogi into chunks toh uske liye max number of chunk 1000 bnege complete document ke liye....
                                     .build();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            TikaDocumentReader reader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));
            
            List<Document> rawDocs = reader.read();

            rawDocs.forEach(doc -> doc.getMetadata().put("source", fileName));

            List<Document> chunks = splitter.split(rawDocs);

            vectorStore.add(chunks);

            totalChunks += chunks.size();

            processed.add(fileName);

        }
        return Map.of("totalChunks", totalChunks, "processed", processed);
    }


    /* http://localhost:8080/api/rag/ask */
    @GetMapping("/ask")
    public String ask(@RequestParam String question){
        QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor.builder(vectorStore)
                                        .searchRequest(SearchRequest.builder().similarityThreshold(0.4).topK(15).build())
                                        .build();
        String response = chatClient.prompt()
                          .advisors(advisor)
                          .user(question)
                          .call()
                          .content();
        return response;
    }
}
