package berezza.home.telegram.bot;

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
import java.util.List;

@Component
public class SadCostsBot extends TelegramLongPollingBot {
    private static final String TAXI_BUTTON_NAME = "/таксі";
    private static final String PRODUCT_BUTTON_NAME ="/продукти";
    private static final String INSTITUTION_BUTTON_NAME = "/кафе та ресторани";
    private static final String MOVIE_BUTTON_NAME = "/кіно";
    private static final String HRYVNIA_BUTTON_NAME = "/₴";
    private static final String EURO_BUTTON_NAME = "/€";
    private static final String DOLLAR_BUTTON_NAME ="/$";

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
            switch (message.getText()){
                case "/витрати":
                    sendMsg(message,"Вибери будь ласка категорію витрат");
            }

        }
    }



    private void sendMsg(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            setCostsTypesButtons(sendMessage);
            execute(sendMessage);
        }catch (TelegramApiException exception){
            System.out.println(exception);
        }
    }

    private void setCostsTypesButtons(SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        keyboardRow.add(new KeyboardButton(PRODUCT_BUTTON_NAME));
        keyboardRow.add(new KeyboardButton(TAXI_BUTTON_NAME));
        keyboardRow.add(new KeyboardButton(INSTITUTION_BUTTON_NAME));
        keyboardRow.add(new KeyboardButton(MOVIE_BUTTON_NAME));
        rows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    private void setCurrencyTypeButton(SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        keyboardRow.add(new KeyboardButton(DOLLAR_BUTTON_NAME));
        keyboardRow.add(new KeyboardButton(EURO_BUTTON_NAME));
        keyboardRow.add(new KeyboardButton(HRYVNIA_BUTTON_NAME));
        rows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }
}
