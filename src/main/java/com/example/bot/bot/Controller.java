package com.example.bot.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller {

    private static Currency originalCurrency;
    private static Currency targetCurrency;

    public static void start(SendMessage sender, Message message) {
        Chat chat = message.getChat();
        sender.setText(String.format("Salom, %s! Bizning botga xush kelibsiz!", chat.getFirstName()));

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(new KeyboardRow(keyboardButtons(
                "Valyutalar"
        )));

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setKeyboard(rows);

        sender.setReplyMarkup(markup);
    }

    public static void conversion(SendMessage sender, int number, Service service) {
        if (originalCurrency != null && targetCurrency != null) {
            double original = service.getCurrency(originalCurrency.name());
            double target = service.getCurrency(targetCurrency.name());

            sender.setText(String.format("%d %s = %f %s",
                    number, originalCurrency, original / target * number, targetCurrency));
        } else {
            sender.setText("Valyutalarni tanlang!");
        }
    }

    public static void currency(SendMessage sender) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (Currency currency : Currency.values())
            buttons.add(Arrays.asList(
                    inlineButton(currency.name(), String.format("original=%s", currency)),
                    inlineButton(currency.name(), String.format("target=%s", currency))
            ));

        sender.setReplyMarkup(new InlineKeyboardMarkup(buttons));
        sender.setText("Valyutani tanlang");
    }

    public static void currency(EditMessageReplyMarkup sender, String data) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        String[] call_data = data.split("=");
        setSavedCurrency(call_data);

        for (Currency currency : Currency.values())
            buttons.add(Arrays.asList(
                    inlineButton(getCurrentCurrency(originalCurrency, currency), String.format("original=%s", currency)),
                    inlineButton(getCurrentCurrency(targetCurrency, currency) + " ", String.format("target=%s", currency))
            ));

        sender.setReplyMarkup(new InlineKeyboardMarkup(buttons));
    }

    private static InlineKeyboardButton inlineButton(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }

    private static String getCurrentCurrency(Currency saved, Currency current) {
        return saved == current ? current.name() + " \u2705" : current.name();
    }

    private static void setSavedCurrency(String[] values) {
        if (values[0].equals("original"))
            originalCurrency = Currency.valueOf(values[1]);
        else
            targetCurrency = Currency.valueOf(values[1]);
    }

    private static List<KeyboardButton> keyboardButtons(String ...names) {
        List<KeyboardButton> buttons = new ArrayList<>();

        for (String name : names)
            buttons.add(new KeyboardButton(name));

        return buttons;
    }
}
