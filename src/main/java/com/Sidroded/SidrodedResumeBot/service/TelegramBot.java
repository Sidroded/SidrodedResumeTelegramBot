package com.Sidroded.SidrodedResumeBot.service;

import com.Sidroded.SidrodedResumeBot.config.BotConfig;
import com.vdurmont.emoji.EmojiParser;
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
    static final String IN_DEV_TEXT = "This part is still under development.";
    static final String HELP_TEXT = "This bot was created by Daniil Deinekin (Sidroded). \n" +
            "You can use menu to get different information about me. \n\n" +
            "This bot is under development, so some features may not be available. \n\n" +
            "Type /start to start this bot, and see information about me. \n" +
            "Type /about Let me introduce myself." +
            "Type /education to see info about my education.\n" +
            "Type /portfolio to see my projects.\n" +
            "Type /socials to see all my social media.\n" +
            "Type /help to see this massage again.\n";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Show welcome massage"));
        listOfCommands.add(new BotCommand("/about","Some info about who I'm"));
        listOfCommands.add(new BotCommand("/education", "Info about education"));
        listOfCommands.add(new BotCommand("/portfolio", "Link on GitHub"));
        listOfCommands.add(new BotCommand("/socials","My social media"));
        listOfCommands.add(new BotCommand("/help","How to use this bot"));
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
                case "/about":
                    aboutCommandReceived(chatId);
                    break;
                case "/portfolio":
                    portfolioCommandReceived(chatId);
                    break;
                case "/education":
                    educationCommandReceived(chatId);
                    break;
                case "/socials":
                    socialsCommandReceived(chatId);
                    break;
                case "/help":
                    sendMassage(chatId, HELP_TEXT);
                    break;
                default:
                    sendMassage(chatId, "Sorry, command was not recognized");
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {

        String answer = EmojiParser.parseToUnicode( "Hi, " + name + ", nice to meet you!" + " :hand:");
        log.info("Replied to user " + name);

        sendMassage(chatId, answer);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        sendMassage(chatId, "My name is Daniil. And I wrote this resume-bot in Java to show you who I am.");
        sendMassage(chatId, "As a first step, let me introduce myself. Please press /about to show main information about me.");
    }

    private void aboutCommandReceived(long chatId) {
        sendMassage(chatId, IN_DEV_TEXT);
    }

    private void educationCommandReceived(long chatId) {
        sendMassage(chatId, IN_DEV_TEXT);
    }

    private void portfolioCommandReceived(long chatId) {
        sendMassage(chatId, IN_DEV_TEXT);
    }

    private void socialsCommandReceived(long chatId) {
        sendMassage(chatId, IN_DEV_TEXT);
    }

    private void sendMassage(long chatId, String massageSend) {
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
