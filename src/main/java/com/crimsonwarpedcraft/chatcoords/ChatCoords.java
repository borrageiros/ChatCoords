package com.crimsonwarpedcraft.chatcoords;

import io.papermc.lib.PaperLib;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for ChatCoords.
 */
public class ChatCoords extends JavaPlugin {

  @Override
  public void onEnable() {
    PaperLib.suggestPaper(this);

    saveDefaultConfig();

    if (getCommand("coords") != null) {
      getCommand("coords").setExecutor(new CoordsCommand(this));
    } else {
      getLogger().severe("Failed to register coords command! Check plugin.yml");
    }
  }
}
