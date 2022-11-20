package com.Sidroded.SidrodedResumeBot.service;

import com.Sidroded.SidrodedResumeBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    static final String HELP_TEXT = "This bot was created by Daniil Deinekin (Sidroded). \n" +
            "You can use menu to get different information about me. \n\n" +
            "This bot is under development, so some features may not be available. \n" +
            "Info about menu soon. \n\n" +
            "Have a good day :з";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/portfolio", "Link on GitHub"));
        listOfCommands.add(new BotCommand("/socials","My social media"));
        listOfCommands.add(new BotCommand("/myData","Get your data store"));
        listOfCommands.add(new BotCommand("/help","How to use this bor"));
        listOfCommands.add(new BotCommand("/settings","Settings"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {}
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String massageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (massageText.toLowerCase()) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sandMassage(chatId, HELP_TEXT);
                    break;
                default:
                    sandMassage(chatId, "Sorry, command was not recognized");
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {

        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replied to user " + name);

        sandMassage(chatId, answer);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}

        sandMassage(chatId, "This is my Resume-Bot. Do you want to know what he can do? :)");
        sandMassage(chatId, "Please use special menu on left side.");
    }

    private void sandMassage(long chatId, String massageSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(massageSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error occurred" + e.getMessage());
        }
    }
}
