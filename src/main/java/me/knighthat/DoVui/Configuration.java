package me.knighthat.DoVui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Configuration {

	DoVui plugin;

	public Configuration(DoVui plugin) {
		this.plugin = plugin;
		khoiDong();
	}

	private File tep = null;
	private FileConfiguration config = null;

	public void khoiDong() {
		if (tep == null)
			tep = new File(plugin.getDataFolder(), "config.yml");

		if (!tep.exists()) {
			plugin.saveResource("config.yml", false);
		} else {
			kiemTra();
		}
	}

	public void taiLai() {
		if (tep == null)
			tep = new File(plugin.getDataFolder(), "config.yml");

		InputStream dulieu = plugin.getResource("config.yml");
		if (dulieu != null) {
			YamlConfiguration macdinh = YamlConfiguration
					.loadConfiguration(new InputStreamReader(dulieu, Charset.forName("UTF8")));
			config.setDefaults(macdinh);
		}
	}

	public FileConfiguration lay() {
		if (config == null)
			taiLai();
		FileInputStream file = null;
		try {
			file = new FileInputStream(new File(plugin.getDataFolder(), "config.yml"));
		} catch (FileNotFoundException e) {
			// sad
		}
		config = YamlConfiguration.loadConfiguration(new InputStreamReader(file, Charset.forName("UTF8")));
		return config;
	}

	public void luu() {
		if (config == null || tep == null)
			return;
		tep = new File(plugin.getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(tep);
		try {
			config.save(tep);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void kiemTra() {
		config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
		FileConfiguration macDinh = YamlConfiguration
				.loadConfiguration(new InputStreamReader(plugin.getResource("config.yml"), Charset.forName("UTF8")));
		final List<String> mucMoi = new ArrayList<String>();
		final List<String> mucCu = new ArrayList<String>();
		config.getConfigurationSection("").getKeys(true).forEach(muc -> {
			mucMoi.add(muc);
		});

		macDinh.getConfigurationSection("").getKeys(true).forEach(muc -> {
			mucCu.add(muc);
		});

		if (!(mucMoi.size() == mucCu.size())) {
			tep.renameTo(new File(plugin.getDataFolder(), "config.yml.old"));
			plugin.saveResource("config.yml", true);
			plugin.getServer().getConsoleSender()
					.sendMessage(plugin.chatMau("&f[&eDoan&aSo&f] &cĐã xảy ra lỗi với config.yml!"));
			plugin.getServer().getConsoleSender().sendMessage(
					plugin.chatMau("&f[&eDoan&aSo&f] &cĐang thực hiện đổi tên tệp cũ và thay thế bằng tệp mới..."));
			plugin.getServer().getConsoleSender().sendMessage(plugin.chatMau(""));
			plugin.getServer().getConsoleSender()
					.sendMessage(plugin.chatMau("&f[&eDoan&aSo&f] &cAn error occurs when loading config.yml!"));
			plugin.getServer().getConsoleSender()
					.sendMessage(plugin.chatMau("&f[&eDoan&aSo&f] &cSaving and copying to another file..."));
		}
	}
}
