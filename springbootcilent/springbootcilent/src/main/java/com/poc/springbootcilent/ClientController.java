package com.poc.springbootcilent;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
   
    private final ChatClient chatClient;

    public ClientController(ChatClient.Builder builder, ToolCallbackProvider tools) {
        // Log all MCP tools detected by Spring
        Arrays.stream(tools.getToolCallbacks())
                .forEach(t -> log.info("Loaded MCP Tool: {}", t.getToolDefinition().name()));

        // Build ChatClient and attach all tools
        this.chatClient = builder.defaultToolCallbacks(tools).build();     
    }

    /**
     * Unified chat endpoint.
     * Accepts any user query and routes it through ChatClient.
     * Example calls:
     *   /client/chat?query=Call outlook_list_folders tool.
     *   /client/chat?query=Fetch email details for id: 123
     *   /client/chat?query=Delete email 456
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String query) {
        return chatClient.prompt()
                .user(query)
                .call()
                .content();
    }
}
