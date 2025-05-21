package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PlayerRespawnListener implements Listener {
    private final CreeperCataclysmPlugin plugin;

    public PlayerRespawnListener(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(!plugin.getGameManager().isGameStarted()) return;
        if(!plugin.getGameManager().getPlayers().contains(event.getEntity())) return;
        if(plugin.getGameManager().getCurrentMap().name.equals("Scorched Earth")) return;

        Player victim = event.getEntity();
        //Set the victim's steak to 8 and arrows to 5
        ItemStack arrows;
        arrows = new ItemStack(Material.ARROW, 3);
        victim.teleport(plugin.getGameManager().getCurrentMap().defenderspawn);
        for(ItemStack item : victim.getInventory().getContents()){
            if(item == null) continue;
            if(item.getType() == Material.ARROW) {
                arrows.setAmount(arrows.getAmount() - item.getAmount());
            }
        }
        victim.getInventory().addItem(arrows);

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        plugin.getGameManager().getPlayerKillMap().put(event.getPlayer(), 0);
        if(!plugin.getGameManager().isGameStarted()){
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    if(!event.getPlayer().getInventory().contains(Material.STICK)){
                        ItemStack knockbackstick = new ItemStack(Material.STICK);
                        ItemMeta stickmeta = knockbackstick.getItemMeta();
                        assert stickmeta != null;
                        stickmeta.setDisplayName(ChatColor.RED + "Knockback Stick");
                        stickmeta.addEnchant(Enchantment.KNOCKBACK, 10, true);
                        knockbackstick.setItemMeta(stickmeta);
                        event.getPlayer().getInventory().addItem(knockbackstick);
                    }
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, PotionEffect.INFINITE_DURATION, 3));
                }
            }, 1L);
            return;
        }
        if(!plugin.getGameManager().getPlayers().contains(event.getPlayer())) return;
        Player victim = event.getPlayer();
        if(plugin.getGameManager().getDefenders().contains(victim)){
            victim.teleport(plugin.getGameManager().getCurrentMap().defenderspawn);
        }
        else{
            victim.teleport(plugin.getGameManager().getCurrentMap().attackerspawn);
        }
        victim.setHealth(victim.getHealth() + (victim.getHealth() > 16 ? (20 - victim.getHealth()) : 4));
        for (ItemStack item : victim.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() == Material.GOAT_HORN) {
                item.setAmount(item.getAmount() - 1);
            }
        }
        if(plugin.getGameManager().getDefenders().contains(victim)){
            plugin.getGameManager().getKillMap().put(victim, plugin.getGameManager().getDefenderGoldStart());
        }
        else{
            plugin.getGameManager().getKillMap().put(victim, plugin.getGameManager().getAttackerGoldStart());
        }
        plugin.getGameManager().getDamageMap().put(victim, new HashMap<>());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        if(!plugin.getGameManager().isGameStarted()) return;
        if(!plugin.getGameManager().getPlayers().contains(event.getPlayer())) return;
        plugin.getGameManager().getLeftPlayers().put(event.getPlayer().getName(), event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(!plugin.getGameManager().isGameStarted()) {
            Score deathsScore = Objects.requireNonNull(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getObjective("deaths")).getScore(event.getPlayer().getName());
            if(!(deathsScore.getScore() > 0)){
                deathsScore.setScore(0);
            }
            if(event.getPlayer().getInventory().getHelmet() != null){
                if(event.getPlayer().getInventory().getHelmet().getItemMeta() instanceof LeatherArmorMeta helmetMeta){
                    if(helmetMeta.getColor().equals(Color.RED) || helmetMeta.getColor().equals(Color.BLUE)){
                        FileConfiguration config = plugin.getPluginConfig();
                        ConfigurationSection lobby = config.getConfigurationSection("lobby");
                        assert lobby != null;
                        Location lobbySpawn = new Location(Bukkit.getWorld(Objects.requireNonNull(lobby.getString("world"))), lobby.getDouble("x"), lobby.getDouble("y"), lobby.getDouble("z"), (float)lobby.getDouble("yaw"), (float)lobby.getDouble("pitch"));
                        event.getPlayer().teleport(lobbySpawn);
                        event.getPlayer().setLevel(0);
                        event.getPlayer().setExp(0);
                        event.getPlayer().getInventory().clear();
                        event.getPlayer().setRespawnLocation(lobbySpawn, true);
                        event.getPlayer().setGameMode(GameMode.ADVENTURE);
                        ItemStack knockbackstick = new ItemStack(Material.STICK);
                        ItemMeta stickmeta = knockbackstick.getItemMeta();
                        assert stickmeta != null;
                        stickmeta.setDisplayName(ChatColor.RED + "Knockback Stick");
                        stickmeta.addEnchant(Enchantment.KNOCKBACK, 10, true);
                        knockbackstick.setItemMeta(stickmeta);
                        event.getPlayer().getInventory().addItem(knockbackstick);
                        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, PotionEffect.INFINITE_DURATION, 3));
                    }
                }
            }
            return;
        }
        if(!plugin.getGameManager().getLeftPlayers().containsKey(event.getPlayer().getName())) return;
        Player oldPLayer = plugin.getGameManager().getLeftPlayers().get(event.getPlayer().getName());
        Player newPlayer = event.getPlayer();
        //true = Defender false = Attacker
        boolean team = plugin.getGameManager().getDefenders().contains(oldPLayer);
        plugin.getGameManager().getKillMap().put(newPlayer, plugin.getGameManager().getKillMap().remove(oldPLayer));
        plugin.getGameManager().getDamageMap().put(newPlayer, plugin.getGameManager().getDamageMap().remove(oldPLayer));
        plugin.getGameManager().getPlayerKillMap().put(newPlayer, plugin.getGameManager().getPlayerKillMap().remove(oldPLayer));
        plugin.getGameManager().getTotalKills().put(newPlayer, plugin.getGameManager().getTotalKills().remove(oldPLayer));
        plugin.getGameManager().getTotalCreeperDamage().put(newPlayer, plugin.getGameManager().getTotalCreeperDamage().remove(oldPLayer));
        List<Integer> arrows = plugin.getGameManager().getArrowCooldowns().get(oldPLayer);
        plugin.getGameManager().getArrowCooldowns().remove(oldPLayer);
        int addArrows = 0;
        for(int i = arrows.size() - 1; i >= 0 ; i--){
            if(arrows.get(i) >= plugin.getGameManager().getTimeLeft()){
                arrows.remove(i);
                addArrows++;
            }
        }
        int sizeArrow = arrows.size();
        plugin.getGameManager().getArrowCooldowns().put(newPlayer, arrows);
        newPlayer.getInventory().addItem(new ItemStack(Material.ARROW, addArrows));
        if(sizeArrow > 0){
            plugin.getGameManager().arrowRespawner(newPlayer);
        }
        if(team){
            plugin.getGameManager().getDefenders().remove(oldPLayer);
            plugin.getGameManager().getDefenders().add(newPlayer);
            newPlayer.teleport(plugin.getGameManager().getCurrentMap().defenderspawn);
        }
        else{
            plugin.getGameManager().getAttackers().remove(oldPLayer);
            plugin.getGameManager().getAttackers().add(newPlayer);
            newPlayer.teleport(plugin.getGameManager().getCurrentMap().attackerspawn);
        }
        plugin.getGameManager().getPlayers().remove(oldPLayer);
        plugin.getGameManager().getPlayers().add(newPlayer);
        plugin.getGameManager().getLeftPlayers().remove(oldPLayer.getName());

    }
}
