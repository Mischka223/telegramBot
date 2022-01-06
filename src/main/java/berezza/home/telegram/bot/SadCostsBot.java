package berezza.home.telegram.bot;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SadCostsBot extends TelegramLongPollingBot {
    private static final String COSTS_BUTTON = "/вписати витрати";
    private static final String TAXI_BUTTON_NAME = "/таксі";
    private static final String PRODUCT_BUTTON_NAME = "/продукти";
    private static final String INSTITUTION_BUTTON_NAME = "/кафе та ресторани";
    private static final String MOVIE_BUTTON_NAME = "/кіно";
    private static final String HRYVNIA_BUTTON_NAME = "/₴";
    private static final String EURO_BUTTON_NAME = "/€";
    private static final String DOLLAR_BUTTON_NAME = "/$";

    @Value("${telegram.bot.username}")
    private String userName;
    @Value("${telegram.bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && update.getMessage().hasText()) {
            if (message.getText().equals("/start")) {
                sendMsg(message, "ви хочете вписати чи подивитись витрати?", List.of(COSTS_BUTTON));
            }
            if (message.getText().equals(COSTS_BUTTON)) {
                sendMsg(message, "будь ласка виберіть категорію витрат", getCostsTypeButtonsNameList());
            }
            if (getCostsTypeButtonsNameList().stream().anyMatch(buttonName -> buttonName.equals(message.getText()))) {
                sendMsg(message, "будь ласка виберіть валюту", getAmountButtonList());
            }
            if (getAmountButtonList().stream().anyMatch(buttonName -> buttonName.equals(message.getText()))) {
                sendMsg(message, "введіть суму будь ласка яку ви витратили на дану категорію", Collections.emptyList());
            }
        }
    }

    private void sendMsg(Message message, String text, List<String> buttonsNames) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage, buttonsNames);
            execute(sendMessage);
        } catch (TelegramApiException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void setButtons(SendMessage sendMessage, List<String> buttonsNames) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        List<KeyboardButton> keyboardButtons = buttonsNames.stream()
                .map(KeyboardButton::new)
                .collect(Collectors.toList());

        keyboardRow.addAll(keyboardButtons);

        rows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    private List<String> getAmountButtonList() {
        return ImmutableList.of(
                DOLLAR_BUTTON_NAME,
                EURO_BUTTON_NAME,
                HRYVNIA_BUTTON_NAME
        );
    }

    private List<String> getCostsTypeButtonsNameList() {
        return ImmutableList.of(
                MOVIE_BUTTON_NAME,
                INSTITUTION_BUTTON_NAME,
                PRODUCT_BUTTON_NAME,
                TAXI_BUTTON_NAME
        );
    }
}
