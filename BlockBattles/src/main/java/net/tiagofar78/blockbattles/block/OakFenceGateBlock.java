package net.tiagofar78.blockbattles.block;

import org.bukkit.Location;
import org.bukkit.Material;

import net.tiagofar78.blockbattles.BBGame;
import net.tiagofar78.blockbattles.BBPlayer;
import net.tiagofar78.blockbattles.managers.ConfigManager;

public class OakFenceGateBlock extends Block implements Interactable {
    
    public Block createNewInstance() {
        return new OakFenceGateBlock();
    }
    
    private BBPlayer _placer;
    private int _interactionCount = 0;
    private int _turnId;

    @Override
    public Material getMaterial() {
        return Material.OAK_FENCE_GATE;
    }

    @Override
    public void executePlacement(BBGame game, BBPlayer placer, BBPlayer otherPlayer, Location location) {
        _placer = placer;
        _turnId = game.getTurnId();
        
        ConfigManager config = ConfigManager.getInstance();
        otherPlayer.damage(config.getOakFenceGateDamage());
        
        int secondsToInteract = config.getOakFenceGateSecondsToInteract();
        if (game.getTurnTimer() > secondsToInteract) {
            game.setTurnTimer(secondsToInteract);
        }
    }

    @Override
    public boolean interact(BBGame game, BBPlayer interacter, BBPlayer other, Location location) {
        ConfigManager config = ConfigManager.getInstance();
        int maxInteractions = config.getOakFenceGateMaxInteractions();
        if (_interactionCount == maxInteractions) {
            return true;
        }
        
        if (!interacter.equals(_placer)) {
            return true;
        }
        
        if (game.getTurnId() != _turnId) {
            return true;
        }
        
        _interactionCount++;
        other.damage(config.getOakFenceGateDamage());
        
        if (_interactionCount == maxInteractions) {
            game.changeTurn();
        }
        
        return false;
    }

}
