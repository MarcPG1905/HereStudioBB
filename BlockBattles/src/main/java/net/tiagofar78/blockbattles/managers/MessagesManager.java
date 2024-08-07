package net.tiagofar78.blockbattles.managers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import net.tiagofar78.blockbattles.BBResources;

public class MessagesManager {

    public static void load() {
        // Just needs to enter this class to initialize static values
    }

    private static Hashtable<String, MessagesManager> instance = initializeLanguageMessages();

    private static Hashtable<String, MessagesManager> initializeLanguageMessages() {
        ConfigManager config = ConfigManager.getInstance();

        Hashtable<String, MessagesManager> languagesMessages = new Hashtable<>();

        List<String> availableLanguages = config.getAvailableLanguages();
        String defaultLanguage = config.getDefaultLanguage();

        if (availableLanguages.size() == 0) {
            availableLanguages.add(defaultLanguage);
        }

        for (String language : availableLanguages) {
            languagesMessages.put(language, new MessagesManager(language));
        }

        return languagesMessages;
    }

    public static MessagesManager getInstance(String language) {
        if (language == null) {
            language = ConfigManager.getInstance().getDefaultLanguage();
        }

        return instance.get(language);
    }

    public static MessagesManager getInstanceByPlayer(String playerName) {
        String language = MessagesManager.getPlayerLanguage(playerName);
        if (language == null) {
            language = ConfigManager.getInstance().getDefaultLanguage();
        }

        return MessagesManager.getInstance(language);
    }

    private static String getPlayerLanguage(String playerName) {
        return ConfigManager.getInstance().getDefaultLanguage();
    }

//  ########################################
//  #              Scoreboard              #
//  ########################################
    
    private String _scoreboardTitle;
    private String _playerHealthLine;
    private String _serverShopLine;

//  ########################################
//  #               Warnings               #
//  ########################################
    
    private String _joinedQueueMessage;
    private String _leftQueueMessage;
    private String _mapsLoadedMessage;
    private String _lostTurnOwnMessage;
    private String _lostTurnOtherMessage;
    private String _turnDurationMessage;
    private String _resultTitle;
    private String _resultSubtitle;
    private String _rankedWinnerResultSubtitle;
    private String _rankedLoserResultSubtitle;
    private String _gameResultMessage;

//  ########################################
//  #                Errors                #
//  ########################################
    
    private String _notAllowedMessage;
    private String _alreadyInQueueMessage;
    private String _notInQueueMessage;
    private String _cantExecuteInGameMessage;

//  #######################################
//  #                Items                #
//  #######################################

    private Hashtable<String, String> _itemsNames;
    private Hashtable<String, List<String>> _itemsLores;
    
    private MessagesManager(String language) {
        YamlConfiguration messages = BBResources.getYamlLanguage(language);
        
        String scoreboardPath = "Scoreboard.";
        _scoreboardTitle = createMessage(messages.getString(scoreboardPath + "Title"));
        _playerHealthLine = createMessage(messages.getString(scoreboardPath + "SideBar.HealthLine"));
        _serverShopLine = createMessage(messages.getString(scoreboardPath + "SideBar.ShopLine"));
        
        String warningPath = "Messages.Warnings.";
        _joinedQueueMessage = createMessage(messages.getString(warningPath + "JoinedQueue"));
        _leftQueueMessage = createMessage(messages.getString(warningPath + "LeftQueue"));
        _mapsLoadedMessage = createMessage(messages.getString(warningPath + "MapsLoaded"));
        _lostTurnOwnMessage = createMessage(messages.getString(warningPath + "LostTurnOwn"));
        _lostTurnOtherMessage = createMessage(messages.getString(warningPath + "LostTurnOther"));
        _turnDurationMessage = createMessage(messages.getString(warningPath + "TurnDuration"));
        _resultTitle = createMessage(messages.getString(warningPath + "ResultTitle"));
        _resultSubtitle = createMessage(messages.getString(warningPath + "ResultSubtitle"));
        _rankedWinnerResultSubtitle = createMessage(messages.getString(warningPath + "RankedWinnerResultSubtitle"));
        _rankedLoserResultSubtitle = createMessage(messages.getString(warningPath + "RankedLoserResultSubtitle"));
        _gameResultMessage = createMessage(messages.getString(warningPath + "GameResult"));
        
        String errorPath = "Messages.Errors.";
        _notAllowedMessage = createMessage(messages.getString(errorPath + "NotAllowed"));
        _alreadyInQueueMessage = createMessage(messages.getString(errorPath + "AlreadyInQueue"));
        _notInQueueMessage = createMessage(messages.getString(errorPath + "NotInQueue"));
        _cantExecuteInGameMessage = createMessage(messages.getString(errorPath + "CantExecuteInGame"));

        _itemsNames = new Hashtable<>();
        _itemsLores = new Hashtable<>();

        String itemsPath = "Items";
        for (String itemConfigName : getItemsConfigNames(messages, itemsPath)) {
            String itemPath = itemsPath + "." + itemConfigName + ".";

            String itemName = messages.getString(itemPath + "Name");
            if (itemName != null) {
                _itemsNames.put(itemConfigName, createMessage(itemName));
            }

            List<String> itemLore = messages.getStringList(itemPath + "Lore");
            if (itemLore != null) {
                _itemsLores.put(itemConfigName, createMessage(itemLore));
            }
        }
    }

    private String createMessage(String rawMessage) {
        return rawMessage.replace("&", "§");
    }

    private List<String> createMessage(List<String> rawMessage) {
        List<String> message = new ArrayList<>(rawMessage);

        for (int i = 0; i < message.size(); i++) {
            message.set(i, createMessage(message.get(i)));
        }

        return message;
    }

//  ########################################
//  #              Scoreboard              #
//  ########################################
    
    public String getScoreboardTitle() {
        return _scoreboardTitle;
    }
    
    public String getPlayerHealthLine(String playerName, String color, double health) {
        double roundedHealth = Math.round(health * (double) 100) / (double) 100;
        String sHealth = Double.toString(roundedHealth);
        return _playerHealthLine.replace("{PLAYER}", playerName).replace("{COLOR}", color).replace("{HEALTH}", sHealth);
    }
    
    public String getServerShopLine() {
        return _serverShopLine;
    }

//  ########################################
//  #               Warnings               #
//  ########################################
    
    public String getJoinedQueueMessage() {
        return _joinedQueueMessage;
    }
    
    public String getLeftQueueMessage() {
        return _leftQueueMessage;
    }
    
    public String getMapsLoadedMessage() {
        return _mapsLoadedMessage;
    }
    
    public String getLostTurnOwnMessage() {
        return _lostTurnOwnMessage;
    }
    
    public String getLostTurnOtherMessage(String playerName) {
        return _lostTurnOtherMessage.replace("{PLAYER}", playerName);
    }
    
    public String getTurnDurationMessage(String playerName, int seconds) {
        return _turnDurationMessage.replace("{PLAYER}", playerName).replace("{SECONDS}", Integer.toString(seconds));
    }
    
    public String getResultTitle(String winnerName) {
        return _resultTitle.replace("{WINNER}", winnerName);
    }
    
    public String getResultSubtitle(double health) {
        return _resultSubtitle.replace("{HEALTH}", Double.toString(health));
    }
    
    public String getRankedWinnerResultSubtitle(int points) {
        return _rankedWinnerResultSubtitle.replace("{POINTS}", Integer.toString(points));
    }
    
    public String getRankedLoserResultSubtitle(int points) {
        return _rankedLoserResultSubtitle.replace("{POINTS}", Integer.toString(points));
    }
    
    public String getGameResultMessage(String winnerName, String loserName) {
        return _gameResultMessage.replace("{WINNER}", winnerName).replace("{LOSER}", loserName);
    }

//  ########################################
//  #                Errors                #
//  ########################################
    
    public String getNotAllowedMessage() {
        return _notAllowedMessage;
    }
    
    public String getAlreadyInQueueMessage() {
        return _alreadyInQueueMessage;
    }
    
    public String getNotInQueueMessage() {
        return _notInQueueMessage;
    }
    
    public String getCantExecuteInGameMessage() {
        return _cantExecuteInGameMessage;
    }

//  #######################################
//  #                Items                #
//  #######################################

    private List<String> getItemsConfigNames(YamlConfiguration messages, String itemsPath) {
        return messages.getConfigurationSection(itemsPath).getKeys(true).stream().filter(
                key -> !key.contains(".")
        ).toList();
    }

    public String getItemName(String configName) {
        return _itemsNames.get(configName);
    }

    public List<String> getItemLore(String configName) {
        List<String> lore = _itemsLores.get(configName);        
        return lore == null ? null : new ArrayList<>(lore);
    }

}
