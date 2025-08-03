# GoxyWelcome

A welcome message plugin for server networks using the Goxy proxy that distinguishes between genuine server joins and internal server transfers.

## Why GoxyWelcome?

Standard welcome plugins show messages on every player connection, creating spam when players move between servers in a network. GoxyWelcome uses the **Goxy API** to detect "full joins" (genuine network connections) vs "server transfers" (moving between servers), only showing welcome messages when appropriate.

## Features

- **🎯 Smart Join Detection** - Uses Goxy API to distinguish full joins from server transfers
- **💬 Rich Chat Messages** - Messages with MiniMessage formatting
- **🏆 Title & Subtitle Display** - Configurable timing and MiniMessage support
- **🔊 Sound Effects** - Play sounds on join
- **📝 PlaceholderAPI Support** - Full support for PlaceholderAPI placeholders in all messages
- **⚙️ Flexible Configuration** - Enable/disable features independently, choose full joins vs all joins

## Prerequisites

- A server running Paper/Spigot 1.18 or above with Goxy plugin installed
- Java 17 or above

## Configuration

```yaml
# Chat welcome message settings
chat:
  enabled: true
  # Show welcome message for full joins (not transfers from other servers)
  full-joins-only: true
  # Message lines
  message:
    - "<gray>------------------------------------"
    - ""
    - "<green>Welcome to the server, {player}!"
    - "<yellow>We hope you enjoy your stay!"
    - "<blue>Type /help for assistance."
    # You can use all MiniMessage features like click and hover actions
    - "<click:open_url:https://example.com><hover:show_text:'Click to visit our website'>Visit our website!</hover></click>"
    - ""
    - "<gray>------------------------------------"

# Title welcome message settings
title:
  enabled: true
  # Show title for full joins only
  full-joins-only: true
  # Main title text
  title: "<gold><bold>Welcome!</bold></gold>"
  # Subtitle text
  subtitle: "<gray>Enjoy your stay, {player}!</gray>"
  # Title display timings (in seconds)
  fade-in: 1.0
  stay: 2.0
  fade-out: 1.0

# Sound welcome message settings
sound:
  enabled: true
  # Show sound for full joins only
  full-joins-only: true
  # Sound to play (Bukkit Sound enum name - see https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html)
  sound: "ENTITY_PLAYER_LEVELUP"

# Global settings
settings:
  # Debug mode for troubleshooting
  debug: false
```

## MiniMessage Formatting

All text uses MiniMessage for rich formatting including colors, gradients, hover effects, and click actions. Test your messages at [webui.advntr.dev](https://webui.advntr.dev).

```yaml
message:
  - "<gradient:red:blue>Welcome {player}!</gradient>"
  - "<rainbow>Enjoy your colorful stay!</rainbow>"
```

## PlaceholderAPI Support

GoxyWelcome supports PlaceholderAPI placeholders in all messages (chat, title, and subtitle). The plugin automatically detects if PlaceholderAPI is installed.

**Available placeholders:**

- `{player}` - Player name (always available)
- Any PlaceholderAPI placeholder (e.g., `%player_displayname%`, `%vault_eco_balance%`) when PlaceholderAPI is installed

```yaml
message:
  - "Welcome back, %player_displayname%!"
  - "Your balance: %vault_eco_balance%"
  - "Online players: %server_online%"
```

## License

This project is licensed under the MIT License.
