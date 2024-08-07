package net.tiagofar78.blockbattles.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.tiagofar78.blockbattles.BBGame;
import net.tiagofar78.blockbattles.BBPlayer;
import net.tiagofar78.blockbattles.dataobjects.BBGamePlayer;
import net.tiagofar78.blockbattles.dataobjects.PlayersAndType;

public class GamesManager {
    
    private final static int GAMES_DISTANCE = 1000;
    
    private static BBGame[] games = new BBGame[ConfigManager.getInstance().getMaxGames()];
    
    /**
     * @return  0 if started successfully<br>
     *          -1 if could not start game
     */
    public static int startGame(Player player1, Player player2, boolean isRanked) {
        ConfigManager config = ConfigManager.getInstance();
        
        for (int i = 0; i < config.getMaxGames(); i++) {
            if (games[i] == null) {
                Location referenceLocation = getReferenceLocation(config, i);
                
                games[i] = new BBGame(i, isRanked, referenceLocation, player1, player2);
                return 0;
            }
        }
        
        return -1;
    }
    
    public static void removeGame(int index) {
        games[index] = null;
        
        PlayersAndType playersAndType = QueueManager.getNextGamePlayerAndType();
        if (playersAndType != null) {
            startGame(playersAndType.getPlayer1(), playersAndType.getPlayer2(), playersAndType.isRankedGame());
        }
    }
    
    public static BBGamePlayer findGameAndPlayer(Player player) {
        for (int i = 0; i < games.length; i++) {
            if (games[i] == null) {
                continue;
            }
            
            BBPlayer bbPlayer = games[i].getPlayer(player);
            if (bbPlayer != null) {
                return new BBGamePlayer(games[i], bbPlayer);
            }
        }
        
        return null;
    }
    
    public static void generateMaps() {
        ConfigManager config = ConfigManager.getInstance();

        for (int i = 0; i < config.getMaxGames(); i++) {
            SchematicsManager.pasteMapSchematic(getReferenceLocation(config, i));
        }
    }
    
    private static Location getReferenceLocation(ConfigManager config, int index) {
        return config.getReferenceLocation().add(index * GAMES_DISTANCE, 0, 0);
    }

}
