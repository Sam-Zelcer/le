package sam.dev.le.controller.chat;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import sam.dev.le.repository.dtos.chat.CreatePrivateChatRequest;
import sam.dev.le.repository.dtos.chat.GetPrivateChatsResponse;
import sam.dev.le.repository.dtos.message.GetMessage;
import sam.dev.le.repository.dtos.message.GetMessagesRequest;
import sam.dev.le.repository.dtos.message.GetMessagesResponse;
import sam.dev.le.repository.dtos.message.SendMessageRequest;
import sam.dev.le.service.chat.MessageService;
import sam.dev.le.service.chat.PrivateChatService;

import java.util.List;

@RestController
@RequestMapping("/private-chat")
@AllArgsConstructor
public class PrivateChatController {

    private final PrivateChatService privateChatService;
    private final MessageService messageService;

    @GetMapping("/get-chats")
    public List<GetPrivateChatsResponse> getChats(
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return privateChatService.getChats(authHeader);
    }

    @PostMapping("/create-chat")
    public String createChat(
            @RequestBody @Valid
            CreatePrivateChatRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return privateChatService.createPrivateChat(request, authHeader);
    }

    @PostMapping("/send-message")
    public String sendMessage(
            @RequestBody @Valid
            SendMessageRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return messageService.sendMessage(request, authHeader);
    }

    @PostMapping("/get-messages")
    public GetMessagesResponse getMessages(
            @RequestBody @Valid
            GetMessagesRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return messageService.getMessages(request, authHeader);
    }
}
