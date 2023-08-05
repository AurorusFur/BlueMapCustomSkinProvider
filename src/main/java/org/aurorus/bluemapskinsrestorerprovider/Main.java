package org.aurorus.bluemapskinsrestorerprovider;

import com.google.gson.JsonParser;
import com.technicjelle.MCUtils;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.plugin.SkinProvider;
import net.skinsrestorer.api.SkinsRestorerAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {
	private SkinsRestorerAPI skinsRestorerAPI;

	private JsonParser JSONparser = new JsonParser();
	private Logger log = getLogger();

	@Override
	public void onEnable() {
		if (getServer().getPluginManager().getPlugin("SkinsRestorer") != null) {
			log.info("SkinsRestorer detected! Using SkinsRestorer API...");
			skinsRestorerAPI = SkinsRestorerAPI.getApi();
		}
		log.info("BlueMapSkinsRestorerProvider plugin enabled!");

		BlueMapAPI.onEnable(blueMapOnEnableListener);
	}

	private final Consumer<BlueMapAPI> blueMapOnEnableListener = blueMapAPI -> {
		SkinProvider customSkinProvider = playerUUID -> {
			Player player = (Player) getServer().getOfflinePlayer(playerUUID);
			String playerName = player.getName();
			String url = skinsRestorerAPI.getSkinTextureUrl(skinsRestorerAPI.getSkinData(skinsRestorerAPI.getSkinName(playerName)));

			log.info("Downloading skin for " + playerName + " from " + url);
			BufferedImage img = MCUtils.downloadImage(url);
			return Optional.ofNullable(img);
		};

		blueMapAPI.getPlugin().setSkinProvider(customSkinProvider);
	};
}
