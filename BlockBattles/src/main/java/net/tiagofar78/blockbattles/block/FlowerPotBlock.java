package net.tiagofar78.blockbattles.block;

import org.bukkit.Location;
import org.bukkit.Material;

import net.tiagofar78.blockbattles.BBGame;
import net.tiagofar78.blockbattles.BBPlayer;
import net.tiagofar78.blockbattles.managers.ConfigManager;

public class FlowerPotBlock extends Block {
    
    public Block createNewInstance() {
        return new FlowerPotBlock();
    }

    @Override
    public Material getMaterial() {
        return Material.FLOWER_POT;
    }

    @Override
    public void executePlacement(BBGame game, BBPlayer placer, BBPlayer otherPlayer, Location location) {
        double damage = ConfigManager.getInstance().getFlowerPotDamage();
        otherPlayer.damage(damage);
        
        game.changeTurn();
    }

}
