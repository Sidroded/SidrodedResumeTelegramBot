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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;
    static final String IN_DEV_TEXT = "This part is still under development.";
    static final String ABOUT_DATA = "ABOUT";
    static final String EDUCATION_DATA = "EDUCATION";
    static final String PORTFOLIO_DATA = "PORTFOLIO";
    static final String SOCIALS_DATA = "SOCIALS";
    static final String HELP_TEXT = "This bot was created by Daniil Deinekin (Sidroded). \n" +
            "You can use menu to get different information about me. \n\n" +
            "This bot is under development, so some features may not be available. \n\n" +
            "Type /start to start this bot, and see information about me. \n" +
            "Type /about to learn more about me.\n" +
            "Type /education to see info about my education.\n" +
            "Type /portfolio to see my projects.\n" +
            "Type /socials to see all my social media.\n" +
            "Type /help to see this massage again.\n";
    static final String ABOUT_TEXT = "I'm Trainee / Junior Java developer. After working in sales for a long time, " +
            "I got to know databases and information technology more, and they steal a way my to heart. " +
            "I started taking courses and reading books like \"Java the complete reference\", \"Introducing Maven\", \"Head First Java\". " +
            "All I have is Java basics and a wide smile, but I want to learn and achieve more. Eager to build a career in Java development." +
            " Ready to learn and work 24/7 to get better!";
    static final String EDUCATION_TEXT = "I studied on my own, taking online courses. Here is an example of sites where I have received my education.";
    static final String PORTFOLIO_TEXT = "";
    static final String SOCIALS_TEXT = "Glad to see you here. Here you can find any of my social networks. I look forward to your connection.";

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
        } else if(update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long massageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {
                case ABOUT_DATA:
                    aboutCommandReceived(chatId);
                    break;
                case EDUCATION_DATA:
                    educationCommandReceived(chatId);
                    break;
                case PORTFOLIO_DATA:
                    portfolioCommandReceived(chatId);
                    break;
                case SOCIALS_DATA:
                    socialsCommandReceived(chatId);
                    break;
            }
            /*if (callbackData.equals(ABOUT_DATA)) {
                aboutCommandReceived(chatId);
            }*/
        }
    }

    private void startCommandReceived(long chatId, String name) {

        SendMessage aboutMassage = new SendMessage();
        aboutMassage.setChatId(String.valueOf(chatId));
        aboutMassage.setText("As a first step, let me introduce myself. Please click /about or press the button below to show main information about me.");

        InlineKeyboardMarkup markupinLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var button = new InlineKeyboardButton();

        button.setText(EmojiParser.parseToUnicode("About me " + ":blush:"));
        button.setCallbackData(ABOUT_DATA);

        rowInLine.add(button);
        rowsInLine.add(rowInLine);
        markupinLine.setKeyboard(rowsInLine);
        aboutMassage.setReplyMarkup(markupinLine);

        String answer = EmojiParser.parseToUnicode( "Hi, " + name + ", nice to meet you!" + " :hand:");
        log.info("Replied to user " + name);

        sendMassage(chatId, answer);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        sendMassage(chatId, "My name is Daniil. And I wrote this resume-bot in Java to describe myself and my professional way.");

        try {
            execute(aboutMassage);
        } catch (TelegramApiException e) {
            log.error("Error occurred" + e.getMessage());
        }
    }

    private void aboutCommandReceived(long chatId) {

        String educationButtonText = "Go to my education, click /education or press the button below.";

        try {
            sendMassage(chatId, ABOUT_TEXT);
            Thread.sleep(1000);
            sendMassageButton(chatId, EDUCATION_DATA, "My Education", educationButtonText, ":trophy:");
        } catch (Exception ignored) {}

    }

    private void educationCommandReceived(long chatId) {

        String educationSite1 = "My first educational site called CodeGym. There I have learned basics of JavaCore. " +
                "Solved more than 500 tasks, getting real practical experience, and I'm not going to stop.\n\n" +
                "https://codegym.cc";
        String educationSite2 = "Now I am studying at EPAM online university. " +
                "I am passionate about learning and getting to know new subjects and areas. To improve is to live - this is my motto.\n\n" +
                "https://epam.com";
        String educationSite3 = "I am also constantly improving on the CodeWars website. If you want you can challenge me!\n\n" +
                "https://www.codewars.com/users/Sidroded";
        String portfolioButtonText = "Go to my portfolio, click /portfolio or press the button below.";

        try {
            sendMassage(chatId, EDUCATION_TEXT);
            Thread.sleep(2500);
            sendMassage(chatId, educationSite1);
            Thread.sleep(2000);
            sendMassage(chatId, educationSite2);
            Thread.sleep(2000);
            sendMassage(chatId, educationSite3);
            Thread.sleep(1000);
            sendMassageButton(chatId, PORTFOLIO_DATA, "My Portfolio", portfolioButtonText, ":rocket:");
        } catch (Exception ignored) {}
    }

    private void portfolioCommandReceived(long chatId) {

        String socialButtonText = "Go to my social media, click /socials or press the button below.";
        try {
            sendMassage(chatId, IN_DEV_TEXT);
            Thread.sleep(1000);
            sendMassageButton(chatId, SOCIALS_DATA, "My Social Media", socialButtonText, ":iphone:");
        } catch (Exception ignored) {}

    }

    private void socialsCommandReceived(long chatId) {

        String mySocialMedia = "My GitHub - https://github.com/Sidroded\n" +
                "My LinkedIn - https://www.linkedin.com/in/daniil-deinekin-13a980231/\n" +
                "My Twitter - https://twitter.com/sidroded\n" +
                "My Telegram - https://t.me/sidroded";
        try {
            sendMassage(chatId, SOCIALS_TEXT);
            sendMassage(chatId, mySocialMedia);

            Thread.sleep(1000);
            sendMassage(chatId, EmojiParser.parseToUnicode("You are awesome, you know? " + ":smiling_face_with_hearts:"));
        } catch (Exception ignored) {}
    }

    private void sendMassageButton(long chatId, String callBackName, String nameButton, String textButtonMassage, String emoji) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textButtonMassage);

        InlineKeyboardMarkup markupinLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var button = new InlineKeyboardButton();

        button.setText(EmojiParser.parseToUnicode(nameButton + " " + emoji));
        button.setCallbackData(callBackName);

        rowInLine.add(button);
        rowsInLine.add(rowInLine);
        markupinLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupinLine);
        try {
            execute(message);
        } catch (Exception e) {}
    }

    /*private void sendPhoto(long chatId, File photo) {
        var message = new SendPhoto()
                .setChatId(chatId)
                .setCaption("Caption")
                .setPhoto(photo);
        try {
            execute(message);
        } catch (TelegramApiException e) {}

    }*/

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
