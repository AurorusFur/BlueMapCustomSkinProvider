package org.aurorus.bluemapskinsrestorerprovider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.technicjelle.MCUtils;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.plugin.SkinProvider;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.property.IProperty;
import org.bukkit.plugin.java.JavaPlugin;
import com.technicjelle.UpdateChecker;
import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public final class Main extends JavaPlugin {
	UpdateChecker updateChecker;

	private SkinsRestorerAPI skinsRestorerAPI;

	public SkinsRestorerAPI getSkinsRestorerAPI() {
		return skinsRestorerAPI;
	};
	private JsonParser JSONparser = new JsonParser();

	@Override
	public void onEnable() {
		if (getServer().getPluginManager().getPlugin("SkinsRestorer") != null) {
			getLogger().info("SkinsRestorer detected! Using SkinsRestorer API...");
			skinsRestorerAPI = SkinsRestorerAPI.getApi();
		}
		getLogger().info("BlueMapSkinsRestorerProvider plugin enabled!");

		BlueMapAPI.onEnable(blueMapOnEnableListener);
	}

	private final Consumer<BlueMapAPI> blueMapOnEnableListener = blueMapAPI -> {
		SkinProvider customSkinProvider = playerUUID -> {
			String url = "http://textures.minecraft.net/texture/{TextureID}";
			String skinUrl = getSkinUrl(playerUUID);

			if (skinUrl.equals("")) {
				url = "http://textures.minecraft.net/texture/74ac5a34d18001763951398a4d2a33893c149ef24bca3a961a94abe6ae703efd";
			}

			String skinTextureId = !skinUrl.equals("") ? skinUrl.substring(skinUrl.lastIndexOf("/")) : "";
			String username = getServer().getOfflinePlayer(playerUUID).getName();
			String localUrl = url.replace("{TextureID}", skinTextureId);
			getLogger().info("Downloading skin for " + username + " from " + localUrl);
			BufferedImage img = MCUtils.downloadImage(localUrl);
			return Optional.ofNullable(img);
		};

		blueMapAPI.getPlugin().setSkinProvider(customSkinProvider);
	};

	private String getSkinUrl(UUID playerUUID) {
		if (getSkinsRestorerAPI() == null) {
			getLogger().info("Cannot get SkinsRestorerAPI");
			return "";
		}
		String playerName = getServer().getOfflinePlayer(playerUUID).getName();
		getLogger().info("Player:" + playerName + "hasSkin:" + getSkinsRestorerAPI().hasSkin(playerName));

		IProperty skinProps = getSkinsRestorerAPI().getProfile(playerUUID.toString());
		byte[] decoded = Base64.getDecoder().decode(skinProps.getValue());
		String decodedString = new String(decoded);
		JsonObject jsonObject = JSONparser.parse(decodedString).getAsJsonObject();
		String skinUrl = jsonObject.getAsJsonObject().get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").toString();

		return skinUrl.substring(1, skinUrl.length() - 1);
	};
}
