package sam.dev.le.service.chat;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import sam.dev.le.repository.chat.MessageRepository;
import sam.dev.le.repository.chat.PrivateChatRepository;
import sam.dev.le.repository.dtos.message.GetMessage;
import sam.dev.le.repository.dtos.message.GetMessagesRequest;
import sam.dev.le.repository.dtos.message.GetMessagesResponse;
import sam.dev.le.repository.dtos.message.SendMessageRequest;
import sam.dev.le.repository.entitys.chat.Message;
import sam.dev.le.repository.entitys.chat.PrivateChat;
import sam.dev.le.repository.entitys.user.User;
import sam.dev.le.repository.user.UserRepository;
import sam.dev.le.service.jwt.JWTService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final PrivateChatRepository privateChatRepository;
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final SimpMessagingTemplate messagingTemplate;

    public String sendMessage(SendMessageRequest request, String authHeader) {
        try {
            if (
                    request.getChatId()==null ||
                            request.getText().isEmpty()
            ) return null;

            Optional<User> optionalUser = userRepository
                    .findById(jwtService.extractUsersId(authHeader.substring(7)));
            if (optionalUser.isEmpty()) return null;

            Optional<PrivateChat> optionalPrivateChat = privateChatRepository
                    .findById(request.getChatId());
            if (optionalPrivateChat.isEmpty()) return null;

            if (
                    !optionalUser.get().equals(optionalPrivateChat.get().getFirstUser()) &&
                            !optionalUser.get().equals(optionalPrivateChat.get().getSecondUser())
            ) return null;

            Message message = new Message();
            message.setText(request.getText());
            message.setSender(optionalUser.get());
            message.setPrivateChat(optionalPrivateChat.get());
            message.setAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

            messageRepository.save(message);
            messagingTemplate.convertAndSend(
                    "/topic/chat/get-messages/"+message.getPrivateChat().getId(),
                    new GetMessage(message.getSender().getUsername(), message.getText(), message.getAt())
            );
            return "message was sent";

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public GetMessagesResponse getMessages(GetMessagesRequest request, String authHeader) {
        try {
            if (request.getChatId()==null) return null;

            Optional<PrivateChat> optionalPrivateChat = privateChatRepository
                    .findById(request.getChatId());
            if (optionalPrivateChat.isEmpty()) return null;

            Optional<User> optionalUser = userRepository
                    .findById(jwtService.extractUsersId(authHeader.substring(7)));

            if (
                    optionalUser.isEmpty() ||
                            !optionalUser.get().equals(optionalPrivateChat.get().getFirstUser()) &&
                            !optionalUser.get().equals(optionalPrivateChat.get().getSecondUser())
            ) return null;

            Optional<List<Message>> messages;
            Pageable pageable = PageRequest.of(0, 50, Sort.by("id").descending());
            if (request.getLastMessageId()==null) {
                messages = messageRepository
                        .findLast50MessagesByPrivateChat(
                                optionalPrivateChat.get(),
                                pageable
                        );
            } else {
                messages = messageRepository
                        .findLast50ExtraMessagesByPrivateChat(
                                optionalPrivateChat.get(),
                                request.getLastMessageId(),
                                pageable
                        );
            }
            if (messages.isEmpty()) return null;
            List<GetMessage> mappedMessages = messages.get().stream()
                    .map(
                            message -> new GetMessage(
                                    message.getSender().getUsername(),
                                    message.getText(),
                                    message.getAt()
                            )
                    ).toList();
            return new GetMessagesResponse(mappedMessages, messages.get().getLast().getId());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
