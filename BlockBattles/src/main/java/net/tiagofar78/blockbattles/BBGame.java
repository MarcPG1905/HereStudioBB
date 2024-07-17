package net.tiagofar78.blockbattles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.tiagofar78.blockbattles.managers.ConfigManager;
import net.tiagofar78.blockbattles.managers.MessagesManager;
import net.tiagofar78.blockbattles.managers.SchematicsManager;
import net.tiagofar78.blockbattles.phases.Phase;
import net.tiagofar78.blockbattles.phases.TurnsPhase;
import net.tiagofar78.blockbattles.utils.SchedulerUtils;

public class BBGame {
    
    private int _index;
    private Location _referenceLoc;
    private boolean _isPlayer1Turn;
    private int _turnId = 0;
    
    private BBPlayer _player1;
    private BBPlayer _player2;
    
    private Phase _phase;
    
    public BBGame(int index, Location referenceLocation, Player player1, Player player2) {
        _index = index;
        _referenceLoc = referenceLocation;
        _player1 = new BBPlayer(player1);
        _player2 = new BBPlayer(player2);
        
        generateMap();
        
        ConfigManager config = ConfigManager.getInstance();
        _player1.resetPlayer(config.getPlayer1SpawnPoint().add(referenceLocation));
        _player2.resetPlayer(config.getPlayer2SpawnPoint().add(referenceLocation));
        
        startNextPhase(new TurnsPhase());
    }
    
    private void generateMap() {
        SchematicsManager.pasteMapSchematic(_referenceLoc);
    }
    
    public int getIndex() {
        return _index;
    }
    
    public void setIsPlayer1Turn(boolean value) {
        _isPlayer1Turn = value;
    }
    
    public BBPlayer getPlayer1() {
        return _player1;
    }
    
    public BBPlayer getPlayer2() {
        return _player2;
    }

//  #########################################
//  #                 Lobby                 #
//  #########################################
    
    public void removePlayer(BBPlayer player) {
        Location exitLocation = ConfigManager.getInstance().getExitLocation();
        player.getBukkitPlayer().teleport(exitLocation);
    }

//  ########################################
//  #                Phases                #
//  ########################################

    public void startNextPhase() {
        startNextPhase(_phase.next());
    }

    public void startNextPhase(Phase phase) {
        _phase = phase;
        _phase.start(this);
    }

//  ########################################
//  #                 Turn                 #
//  ########################################
    
    public void applyTurnResult(BBPlayer player1, double damage1, BBPlayer player2, double damage2) {
        player1.updateHealth(damage1);
        player2.updateHealth(damage2);
        
        if (_player1.getHealth() <= 0) {
            startNextPhase();
        }
        else if (_player2.getHealth() <= 0) {
            startNextPhase();
        }
    }
    
    public boolean isPlayerTurn(BBPlayer player) {
        return _isPlayer1Turn == player.equals(_player1);
    }
    
    public void changeTurn() {
        _isPlayer1Turn = !_isPlayer1Turn;
        
        BBPlayer player = _isPlayer1Turn ? _player1 : _player2;
        runTurnTimer(player.getBukkitPlayer().getName());
    }
    
    private void runTurnTimer(String playerName) {
        int turnSeconds = ConfigManager.getInstance().getTurnSeconds();
        _turnId++;
        
        runTurnTimer(playerName, turnSeconds, _turnId);
    }
    
    private void runTurnTimer(String playerName, int secondsLeft, int id) {
        if (_turnId != id) {
            return;
        }
        
        Player bukkitPlayer1 = _player1.getBukkitPlayer();
        Player bukkitPlayer2 = _player2.getBukkitPlayer();
        
        MessagesManager messages1 = MessagesManager.getInstanceByPlayer(bukkitPlayer1.getName());
        MessagesManager messages2 = MessagesManager.getInstanceByPlayer(bukkitPlayer2.getName());
        
        if (secondsLeft == 0) {
            if (isPlayerTurn(_player1)) {
                bukkitPlayer1.sendMessage(messages1.getLostTurnOwnMessage());
                bukkitPlayer2.sendMessage(messages2.getLostTurnOtherMessage(playerName));
            }
            else {
                bukkitPlayer1.sendMessage(messages1.getLostTurnOtherMessage(playerName));
                bukkitPlayer2.sendMessage(messages2.getLostTurnOwnMessage());
            }
            changeTurn();
            return;
        }
        
        sendActionBarMessage(bukkitPlayer1, messages1.getTurnDurationMessage(playerName, secondsLeft));
        sendActionBarMessage(bukkitPlayer2, messages2.getTurnDurationMessage(playerName, secondsLeft));
        
        Bukkit.getScheduler().runTaskLater(BlockBattles.getBlockBattles(), new Runnable() {
            
            @Override
            public void run() {
                runTurnTimer(playerName, secondsLeft - 1, id);
            }
            
        }, SchedulerUtils.TICKS_PER_SECOND);
    }
    
    private void sendActionBarMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

}