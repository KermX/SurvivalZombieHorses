package me.kermx.survivalzombiehorses;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public final class SurvivalZombieHorses extends JavaPlugin implements Listener {
    private final Random random = new Random();
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (rightClicked instanceof SkeletonHorse || rightClicked instanceof ZombieHorse) {
            if (rightClicked instanceof SkeletonHorse) {
                SkeletonHorse skeletonHorse = (SkeletonHorse) rightClicked;

                if (!((AbstractHorse) skeletonHorse).isTamed()) {
                    return;
                }

                event.setCancelled(true);

                if (itemInHand.getAmount() >= 1) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);

                    // Spawn particles at the horse's location
                    Location horseLocation = skeletonHorse.getLocation();
                    horseLocation.getWorld().spawnParticle(Particle.SMOKE_NORMAL, horseLocation, 10);

                    double jumpStrength = skeletonHorse.getJumpStrength();
                    double maxHealth = skeletonHorse.getMaxHealth();
                    double speed = skeletonHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();

                    // Check if the horse should transform
                    if (shouldTransform()) {
                        skeletonHorse.remove();
                        ZombieHorse zombieHorse = skeletonHorse.getWorld().spawn(horseLocation, ZombieHorse.class);
                        horseLocation.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, horseLocation, 15);
                        zombieHorse.setTamed(true);
                        zombieHorse.setOwner(player);
                        zombieHorse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                        zombieHorse.setJumpStrength(jumpStrength);
                        zombieHorse.setMaxHealth(maxHealth);
                        zombieHorse.setHealth(maxHealth);
                        zombieHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
                        player.sendMessage(ChatColor.GREEN + "You've turned your skeleton horse into a zombie horse!");
                    } else {
                        player.sendMessage(ChatColor.GRAY + "The horse shudders but doesn't change.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have enough rotten flesh to apply to the horse.");
                }
            } else if (rightClicked instanceof ZombieHorse) {
                ZombieHorse zombieHorse = (ZombieHorse) rightClicked;
                if (itemInHand.getType() == Material.SHEARS) {
                    event.setCancelled(true);

                    if (shouldTransform()) {
                        Location horseLocation = zombieHorse.getLocation();
                        zombieHorse.remove();
                        SkeletonHorse skeletonHorse = zombieHorse.getWorld().spawn(horseLocation, SkeletonHorse.class);

                        // Apply attributes, inventory, and other settings
                        double jumpStrength = skeletonHorse.getJumpStrength();
                        double maxHealth = skeletonHorse.getMaxHealth();
                        double speed = skeletonHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();

                        // Apply settings to the skeleton horse
                        skeletonHorse.setTamed(true);
                        skeletonHorse.setOwner(player);
                        skeletonHorse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                        skeletonHorse.setJumpStrength(jumpStrength);
                        skeletonHorse.setMaxHealth(maxHealth);
                        skeletonHorse.setHealth(maxHealth);
                        skeletonHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);

                        // Spawn particles at the horse's location
                        horseLocation.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, horseLocation, 15);

                        player.sendMessage(ChatColor.GREEN + "You've turned a zombie horse into a skeleton horse!");
                    } else {
                        player.sendMessage(ChatColor.GRAY + "The horse shudders but doesn't change.");
                    }
                }
            }
        }
    }

    private boolean shouldTransform() {
        return random.nextDouble() <= 0.1; // 10% chance of transformation
    }


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
