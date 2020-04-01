package com.smsGenerator.Implementation;

import com.smsGenerator.service.MessageUpdateService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

import static com.smsGenerator.utils.SimvolReplaceMap.MY_MAP;

@Service
public class MessageUpdateServiceImpl implements MessageUpdateService {

    final Random random = new Random();

    @Override
    public String generateNewMessage(String message, Boolean updateMessageFlag) {
        if(!updateMessageFlag) {
            return message;
        }
        return updateMessage(message);
    }

    private String updateMessage(String message) {
        Map<Character, Character> map = MY_MAP;
        StringBuilder messageBuilder = new StringBuilder(message);

        for (int i = 0; i < random.nextInt(message.length()/2) +message.length()/3; i++) {
            int simvolIndex = random.nextInt(message.length());
            Character ch = map.get(messageBuilder.charAt(simvolIndex));
            if(ch!=null) {
                messageBuilder.setCharAt(simvolIndex, ch);
            }
        }
        return messageBuilder.toString();
    }
}
