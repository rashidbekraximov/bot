package com.example.bot.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class Bot extends TelegramLongPollingBot {
    Service service = new Service();

    @Override
    public String getBotUsername() {
        return " "; // Data.username;
    }

    @Override
    public String getBotToken() {
        return Data.token;
    }

    @Override
    public void onUpdateReceived(Update chat) {
        SendMessage sender = new SendMessage();
        Message message = chat.getMessage();

        // Callback handler
        if (chat.hasCallbackQuery()) {
            CallbackQuery callbackQuery = chat.getCallbackQuery();
            message = callbackQuery.getMessage();

            callbackHandler(callbackQuery, message);
            return;
        }


        sender.setChatId(message.getChatId().toString());
        // Command handler
        if (message.hasEntities()) {
            commandHandler(sender, message);
            return;
        }

        // Text message handler
        if (message.hasText()) {
            textMessageHandler(sender, message);
            return;
        }

        // Any type messages [different from the above] (sticker, files or etc)
        anyMessage(sender, message);
    }

    private void commandHandler(SendMessage sender, Message message) {
        String command = message.getEntities().get(0).getText();
        sender.setText(command);
        sender.enableNotification();
        switch (command) {
            case "/start":
                Controller.start(sender, message);
                break;
            case "/valyutalar":
                Controller.currency(sender);
                break;
        }

        send(sender);
    }

    private void textMessageHandler(SendMessage sender, Message message) {
        try {
            int number = Integer.parseInt(message.getText());
            Controller.conversion(sender, number, service);
        } catch (NumberFormatException e) {
            sender.setText("Foydalanish uchun /valyutalar ni bosing");
        }

        send(sender);
    }

    private void callbackHandler(CallbackQuery query, Message message) {
        EditMessageReplyMarkup markup = new EditMessageReplyMarkup();
        markup.setChatId(message.getChatId().toString());
        markup.setMessageId(message.getMessageId());
        Controller.currency(markup, query.getData());
        send(markup);
    }

    private void anyMessage(SendMessage sender, Message message) {
        sender.setText("Salom, " + message.getChat().getFirstName() +
                "\nBotda bu turdagi xabar bilan ishlash imkoniyati hozircha qo\u2018shimlagan!");
        send(sender);
    }

    private void send(SendMessage sender) {
        try {
            execute(sender);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void send(EditMessageReplyMarkup markup) {
        try {
            execute(markup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /* Start */
}
