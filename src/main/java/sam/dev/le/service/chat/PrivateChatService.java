package sam.dev.le.service.chat;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import sam.dev.le.repository.chat.PrivateChatRepository;
import sam.dev.le.repository.dtos.chat.ReturnNewPrivateChat;
import sam.dev.le.repository.user.UserRepository;
import sam.dev.le.repository.dtos.chat.CreatePrivateChatRequest;
import sam.dev.le.repository.dtos.chat.GetPrivateChatsResponse;
import sam.dev.le.repository.entitys.chat.PrivateChat;
import sam.dev.le.repository.entitys.user.User;
import sam.dev.le.service.jwt.JWTService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class PrivateChatService {

    private final UserRepository userRepository;
    private final PrivateChatRepository privateChatRepository;
    private final JWTService jwtService;
    private final SimpMessagingTemplate messagingTemplate;

    public List<GetPrivateChatsResponse> getChats(String authHeader) {
        try {
            if (authHeader.isEmpty()) throw new RuntimeException("missing authentication header");

            Optional<User> optionalUser = userRepository.findById(
                    jwtService.extractUsersId(authHeader.substring(7))
            );
            if (optionalUser.isEmpty()) throw new RuntimeException("user doesn't exist");

            Optional<List<PrivateChat>> privateChats = privateChatRepository
                    .findUsersPrivateChats(optionalUser.get());

            return privateChats.map(chats -> chats.stream()
                    .map(
                            privateChat -> new GetPrivateChatsResponse(
                                    privateChat.getId(),
                                    optionalUser.get().equals(privateChat.getFirstUser()) ?
                                            privateChat.getSecondUser().getUsername() :
                                            privateChat.getFirstUser().getUsername()
                            )
                    ).toList()).orElse(null);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String createPrivateChat(CreatePrivateChatRequest request, String authHeader) {
        try {
            if (request.getSecondUserNameId()==null) return "mustn't provide second user id as null";

            String[] secondUserNameId = request.getSecondUserNameId().split(":", 2);

            Optional<User> optionalFirstUser = userRepository
                    .findById(jwtService.extractUsersId(authHeader.substring(7)));
            Optional<User> optionalSecondUser = userRepository
                    .findById(Long.parseLong(secondUserNameId[1]));

            if (
                    optionalFirstUser.isEmpty() ||
                            optionalSecondUser.isEmpty()
            ) return "second user doesn't exist, or your account no longer exist";

            if (
                    !optionalSecondUser.get().getUsername().equals(secondUserNameId[0])
            ) return "second user was provided in incorrect way";

            if (optionalFirstUser.equals(optionalSecondUser)) return "you can't create chat with yourself";

            if (
                    privateChatRepository.isPrivateChatAlreadyExist(
                            optionalFirstUser.get(),
                            optionalSecondUser.get()
                    ).isPresent()
            ) return "you already have chat with this user";

            PrivateChat chat = new PrivateChat();
            chat.setFirstUser(optionalFirstUser.get());
            chat.setSecondUser(optionalSecondUser.get());
            chat.setId(privateChatRepository.save(chat).getId());

            messagingTemplate.convertAndSend(
                    "/topic/chat/get-chats/"+chat.getFirstUser().getId(),
                    new ReturnNewPrivateChat(chat.getId(), chat.getSecondUser().getUsername()
                    )
            );
            messagingTemplate.convertAndSend(
                    "/topic/chat/get-chats/"+chat.getSecondUser().getId(),
                    new ReturnNewPrivateChat(chat.getId(), chat.getFirstUser().getUsername()
                    )
            );
            return "chat was created";

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
