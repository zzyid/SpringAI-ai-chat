package com.example.springaichat.entity.vo;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;

@NoArgsConstructor // 添加这个注解，表示这个类没有参数的构造方法
@Data
public class MessageVO {
    private String role;
    private String content;

    /**
     *  将Message对象转换为MessageVO对象
     * @param message
     */
    public MessageVO(Message message) {
        this.role = switch (message.getMessageType()) {
            case USER -> "user";
            case ASSISTANT -> "assistant";
            case SYSTEM -> "system";
            default -> "";
        };
        this.content = message.getText();
    }
}