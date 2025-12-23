package com.crimsonwarpedcraft.chatcoords;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for the coords command.
 */
public class CoordsCommand implements CommandExecutor {

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  private final ChatCoords plugin;

  /**
   * Creates a new CoordsCommand instance.
   *
   * @param plugin the ChatCoords plugin instance
   */
  public CoordsCommand(ChatCoords plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      String message = getMessage("player-only");
      sender.sendMessage(message);
      return true;
    }

    Player player = (Player) sender;

    if (!player.hasPermission("chatcoords.use")) {
      String message = getMessage("no-permission");
      player.sendMessage(Component.text(message).color(NamedTextColor.RED));
      return true;
    }

    Location location = player.getLocation();
    int x = location.getBlockX();
    int y = location.getBlockY();
    int z = location.getBlockZ();

    String messageTemplate = plugin.getConfig().getString("message", 
        "📍 Coordinates: X: {x} Y: {y} Z: {z} [Teleport]");
    
    if (messageTemplate == null) {
      messageTemplate = "📍 Coordinates: X: {x} Y: {y} Z: {z} [Teleport]";
    }
    
    String message = messageTemplate
        .replace("{x}", String.valueOf(x))
        .replace("{y}", String.valueOf(y))
        .replace("{z}", String.valueOf(z));

    Component finalMessage = parseMessage(message, location, player);

    Component chatMessage = Component.empty()
        .append(player.displayName())
        .append(Component.text(": "))
        .append(finalMessage);

    plugin.getServer().broadcast(chatMessage);

    return true;
  }

  private Component parseMessage(String message, Location location, Player sender) {
    String teleportButtonText = getMessage("teleport-button");
    
    if (!message.contains(teleportButtonText)) {
      return Component.text(message);
    }

    String[] parts = message.split(teleportButtonText.replace("[", "\\[").replace("]", "\\]"), 2);
    Component result = Component.empty();

    if (parts.length > 0 && !parts[0].isEmpty()) {
      result = result.append(Component.text(parts[0]));
    }

    String teleportCommand = String.format("/tp %s %d %d %d", 
        sender.getName(), 
        location.getBlockX(), 
        location.getBlockY(), 
        location.getBlockZ());
    
    String hoverText = getMessage("teleport-hover").replace("{player}", sender.getName());
    String permissionHint = getMessage("teleport-permission-hint");
    
    Component teleportButton = Component.text(teleportButtonText)
        .color(NamedTextColor.GREEN)
        .decorate(TextDecoration.BOLD)
        .clickEvent(ClickEvent.runCommand(teleportCommand))
        .hoverEvent(HoverEvent.showText(
            Component.text(hoverText)
                .color(NamedTextColor.YELLOW)
                .append(Component.newline())
                .append(Component.text(permissionHint)
                    .color(NamedTextColor.GRAY))));

    result = result.append(teleportButton);

    if (parts.length > 1 && !parts[1].isEmpty()) {
      result = result.append(Component.text(parts[1]));
    }

    return result;
  }

  private String getMessage(String key) {
    return plugin.getConfig().getString("messages." + key, 
        getDefaultMessage(key));
  }

  private String getDefaultMessage(String key) {
    return switch (key) {
      case "player-only" -> "This command can only be used by players.";
      case "no-permission" -> "You don't have permission to use this command.";
      case "teleport-button" -> "[Teleport]";
      case "teleport-hover" -> "Click to teleport to {player}'s coordinates";
      case "teleport-permission-hint" -> "Requires /tp permission";
      default -> "";
    };
  }
}
