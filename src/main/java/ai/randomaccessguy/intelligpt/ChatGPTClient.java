package ai.randomaccessguy.intelligpt;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
import java.util.List;

public class ChatGPTClient {

    public String requestChatCompletion(String classCode, String code, String prompt){

        OpenAiService service = new OpenAiService("");

        ChatCompletionRequest request = new ChatCompletionRequest();

        List<ChatMessage> messages = new ArrayList<>();

        System.out.println("Class: " + "\n" +
                "```\n" +
                classCode +
                "\n```");
        System.out.println("\n");
        System.out.println("Section of the code: " + "\n" +
                "```\n" +
                code +
                "\n```");
        System.out.println("\n");
        System.out.println("Prompt: " + "\n" +
                prompt);

        messages.add(new ChatMessage("system",
                "Given the code of the class and the section of the code subject of the request, edit the section of the code following the prompt and using the class as context. Return the edited section of the code as a code between three backtick (```)"));
        messages.add(new ChatMessage("user",
                "Class: " + "\n" +
                       "```\n" +
                       classCode +
                       "\n```" +
                       "\n" +
                       "Section of the code: " + "\n" +
                        "```\n" +
                        code +
                        "\n```" +
                        "\n" +
                       "Prompt: " + "\n" +
                       prompt));

        request.setMessages(messages);
        request.setModel("gpt-3.5-turbo-16k-0613");
        request.setTopP(0.5);

        String response = service.createChatCompletion(request).getChoices().stream().findFirst().get().getMessage().getContent();

        System.out.println(response);

        return response;
    }
}
