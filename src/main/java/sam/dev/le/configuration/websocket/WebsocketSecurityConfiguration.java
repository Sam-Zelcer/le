package sam.dev.le.configuration.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager.Builder;
import sam.dev.le.service.websocket.WebsocketService;
import org.springframework.security.authorization.AuthorizationDecision;

import java.util.function.Supplier;

import static org.springframework.messaging.simp.SimpMessageType.MESSAGE;
import static org.springframework.messaging.simp.SimpMessageType.SUBSCRIBE;

@Configuration
@EnableWebSocketSecurity
@RequiredArgsConstructor
public class WebsocketSecurityConfiguration {

    private final WebsocketService websocketService;

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
        Builder messages = MessageMatcherDelegatingAuthorizationManager.builder();
        return messages
                .nullDestMatcher().authenticated()
                .simpDestMatchers("/app/**").hasAnyAuthority("USER", "ADMIN")
                .simpSubscribeDestMatchers("/topic/chat/get-chats/{userId}").access(this::hasUserAccessToChats)
                .simpSubscribeDestMatchers("/topic/chat/get-messages/{chatId}").access(this::hasUserAccessToPrivateChat)
                .simpSubscribeDestMatchers("/topic/user/**").hasAnyAuthority("USER", "ADMIN")
                .simpSubscribeDestMatchers("/topic/admin/**").hasAuthority("ADMIN")
                .simpTypeMatchers(MESSAGE, SUBSCRIBE).denyAll()
                .anyMessage().denyAll()
                .build();
    }

    private AuthorizationDecision hasUserAccessToPrivateChat(
            Supplier<Authentication> authenticationSupplier,
            MessageAuthorizationContext<?> messageContext
    ) {
        try {
            String destination = (String) messageContext.getMessage().getHeaders().get("simpDestination");

            if (destination != null && destination.startsWith("/topic/chat/get-messages/")) {
                Long chatId = Long.parseLong(destination.substring("/topic/chat/get-messages/".length()));

                return new AuthorizationDecision(
                        websocketService.hasUserAccessToPrivateChat(authenticationSupplier.get(), chatId)
                );
            }

            return new AuthorizationDecision(false);

        } catch (Exception e) {
            return new AuthorizationDecision(false);
        }
    }

    private AuthorizationDecision hasUserAccessToChats(
            Supplier<Authentication> authenticationSupplier,
            MessageAuthorizationContext<?> messageContext
    ) {
        try {
            String destination = (String) messageContext.getMessage().getHeaders().get("simpDestination");

            if (destination != null && destination.startsWith("/topic/chat/get-chats/")) {
                Long userId = Long.parseLong(destination.substring("/topic/chat/get-chats/".length()));

                return new AuthorizationDecision(
                        websocketService.hasUserAccessToChats(authenticationSupplier.get(), userId)
                );
            }

            return new AuthorizationDecision(false);

        } catch (Exception e) {
            return new AuthorizationDecision(false);
        }
    }
}
