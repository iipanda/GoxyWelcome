package com.github.iipanda.goxywelcome

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import pl.goxy.minecraft.api.GoxyApi
import java.time.Duration

class GoxyWelcome : JavaPlugin(), Listener, CommandExecutor {

    private lateinit var miniMessage: MiniMessage
    private var placeholderAPIEnabled = false

    override fun onEnable() {
        saveDefaultConfig()
        miniMessage = MiniMessage.miniMessage()

        placeholderAPIEnabled = server.pluginManager.getPlugin("PlaceholderAPI") != null
        if (placeholderAPIEnabled) {
            logger.info("PlaceholderAPI found! Placeholder support enabled.")
        } else {
            logger.info("PlaceholderAPI not found. Placeholder support disabled.")
        }

        server.pluginManager.registerEvents(this, this)
        getCommand("goxywelcome")?.setExecutor(this)
        logger.info("GoxyWelcome plugin enabled!")
    }

    override fun onDisable() {
        logger.info("GoxyWelcome plugin disabled!")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("goxywelcome", ignoreCase = true)) {
            if (!sender.hasPermission("goxywelcome.admin")) {
                sender.sendMessage(miniMessage.deserialize("<red>You don't have permission to use this command."))
                return true
            }

            if (args.isEmpty()) {
                sender.sendMessage(miniMessage.deserialize("<yellow>Usage: /goxywelcome reload"))
                return true
            }

            when (args[0].lowercase()) {
                "reload" -> {
                    try {
                        reloadConfig()
                        sender.sendMessage(miniMessage.deserialize("<green>GoxyWelcome configuration reloaded successfully!"))
                        logger.info("Configuration reloaded by ${sender.name}")
                    } catch (e: Exception) {
                        sender.sendMessage(miniMessage.deserialize("<red>Failed to reload configuration: ${e.message}"))
                        logger.severe("Failed to reload configuration: ${e.message}")
                    }
                    return true
                }
                else -> {
                    sender.sendMessage(miniMessage.deserialize("<red>Unknown subcommand. Usage: /goxywelcome reload"))
                    return true
                }
            }
        }
        return false
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        
        val goxyPlayer = GoxyApi.getPlayerStorage().getPlayer(player.uniqueId)
        if (goxyPlayer == null) {
            if (config.getBoolean("settings.debug", false)) {
                logger.warning("Could not get Goxy player data for ${player.name}")
            }
            return
        }
        
        val previousServer = goxyPlayer.previousServer
        val isFullJoin = previousServer == null
        
        if (config.getBoolean("settings.debug", false)) {
            logger.info("Player ${player.name} joined. Full join: $isFullJoin, Previous server: $previousServer")
        }
        
        sendChatWelcome(player, isFullJoin)
        sendTitleWelcome(player, isFullJoin)
        playSoundWelcome(player, isFullJoin)
    }

    private fun processPlaceholders(player: org.bukkit.entity.Player, message: String): String {
        var processedMessage = message.replace("{player}", player.name)
        
        if (placeholderAPIEnabled) {
            processedMessage = PlaceholderAPI.setPlaceholders(player, processedMessage)
        }
        
        return processedMessage
    }

    private fun sendChatWelcome(player: org.bukkit.entity.Player, isFullJoin: Boolean) {
        if (!config.getBoolean("chat.enabled", true)) {
            return
        }
        
        if (config.getBoolean("chat.full-joins-only", true) && !isFullJoin) {
            return
        }
        
        val messages = config.getStringList("chat.message")
        if (messages.isEmpty()) {
            return
        }
        
        messages.forEach { message ->
            val formattedMessage = processPlaceholders(player, message)
            val component = miniMessage.deserialize(formattedMessage)
            player.sendMessage(component)
        }
    }

    private fun sendTitleWelcome(player: org.bukkit.entity.Player, isFullJoin: Boolean) {
        if (!config.getBoolean("title.enabled", true)) {
            return
        }
        
        if (config.getBoolean("title.full-joins-only", true) && !isFullJoin) {
            return
        }
        
        val titleText = processPlaceholders(player, config.getString("title.title", "") ?: "")
        val subtitleText = processPlaceholders(player, config.getString("title.subtitle", "") ?: "")
        
        if (titleText.isEmpty() && subtitleText.isEmpty()) {
            return
        }
        
        val titleComponent = if (titleText.isNotEmpty()) {
            miniMessage.deserialize(titleText)
        } else {
            Component.empty()
        }
        
        val subtitleComponent = if (subtitleText.isNotEmpty()) {
            miniMessage.deserialize(subtitleText)
        } else {
            Component.empty()
        }
        
        val fadeIn = Duration.ofMillis((config.getDouble("title.fade-in", 1.0) * 1000).toLong())
        val stay = Duration.ofMillis((config.getDouble("title.stay", 2.0) * 1000).toLong())
        val fadeOut = Duration.ofMillis((config.getDouble("title.fade-out", 1.0) * 1000).toLong())
        
        val title = Title.title(titleComponent, subtitleComponent, Title.Times.times(fadeIn, stay, fadeOut))
        player.showTitle(title)
    }

    private fun playSoundWelcome(player: org.bukkit.entity.Player, isFullJoin: Boolean) {
        if (!config.getBoolean("sound.enabled", true)) {
            return
        }
        
        if (config.getBoolean("sound.full-joins-only", true) && !isFullJoin) {
            return
        }
        
        val soundName = config.getString("sound.sound", "ENTITY_PLAYER_LEVELUP") ?: "ENTITY_PLAYER_LEVELUP"
        
        try {
            val sound = Sound.valueOf(soundName)
            player.playSound(player.location, sound, 1.0f, 1.0f)
            
            if (config.getBoolean("settings.debug", false)) {
                logger.info("Played sound $soundName for ${player.name}")
            }
        } catch (e: IllegalArgumentException) {
            logger.warning("Invalid sound name in config: $soundName. Using default ENTITY_PLAYER_LEVELUP")
            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
        }
    }
}
