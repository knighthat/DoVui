package me.knighthat.DoVui;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DoVui extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {

		getServer().getPluginManager().registerEvents(this, this);

		vongLap();
	}

	Configuration cfg = new Configuration(this);

	public String chatMau(String a) {
		return ChatColor.translateAlternateColorCodes('&', a);
	}

	public String boMau(String a) {
		return ChatColor.stripColor(a);
	}

	public String chu(String a) {
		return cfg.lay().getString(a);
	}

	public int so(String a) {
		return cfg.lay().getInt(a);
	}

	public boolean giaTri(String a) {
		return cfg.lay().getBoolean(a);
	}

	int soVong = 0;
	int toiDa = 0;
	String cauTraLoi = null;
	HashMap<String, Long> thoiGian = new HashMap<String, Long>();
	HashMap<Player, Boolean> thamGia = new HashMap<Player, Boolean>();

	public int soVong() {
		Random nn = new Random();
		cfg.lay().getConfigurationSection("CauHoi").getKeys(false).forEach(muc -> {
			toiDa++;
		});
		if (giaTri("NgauNhien")) {
			soVong = nn.nextInt(toiDa);
		} else {
			soVong++;
			if (soVong > toiDa)
				soVong = 1;
		}
		return soVong;
	}

	public void vongLap() {
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				toiDa = 0;
				thoiGian.clear();
				cauTraLoi = null;
				thamGia.clear();
				soVong();
				for (Player p : getServer().getOnlinePlayers()) {
					p.sendMessage(chatMau(chu("CauHoi." + soVong + ".CauHoi")));
					p.sendMessage(chatMau(chu("HuongDan")));
				}
				cauTraLoi = boMau(chatMau(chu("CauHoi." + soVong + ".CauTraLoi")));

				thoiGian.put(cauTraLoi, System.currentTimeMillis() + (1000 * so("ThoiGian")));
			}
		}, 20L * so("VongLap"), 20L * so("VongLap"));
	}

	public String thoiGian(int thoiGian) {
		int phut;
		int giay;
		int tinhPhut = (int) thoiGian / 60;
		if (tinhPhut > 0) {
			phut = (int) thoiGian / 60;
			giay = thoiGian - (phut * 60);
			return String.valueOf(phut) + " " + cfg.lay().getString("Phut") + " " + String.valueOf(giay) + " "
					+ cfg.lay().getString("Giay");
		}
		return String.valueOf(thoiGian) + " " + cfg.lay().getString("Giay");
	}

	@EventHandler
	public void traLoiCauHoi(AsyncPlayerChatEvent e) {
		String doanChat = e.getMessage();
		Player player = e.getPlayer();
		if (doanChat.startsWith("!")) {
			e.setCancelled(true);
			if (thamGia.containsKey(player)) {
				player.sendMessage(chatMau(cfg.lay().getString("DaThamGia")));
			} else if (!(cauTraLoi == null)) {
				if (thoiGian.get(cauTraLoi) > System.currentTimeMillis()) {
					doanChat = doanChat.replace("! ", "").replace("!", "");
					int traLoiTrong = (int) (thoiGian.get(cauTraLoi) - System.currentTimeMillis()) / 1000;
					traLoiTrong = so("ThoiGian") - traLoiTrong;
					if (doanChat.contains(cauTraLoi)) {
						for (String i : cfg.lay().getStringList("CauHoi." + soVong + ".PhanThuong")) {
							if (i.contains("TINNHAN:")) {
								player.sendMessage(chatMau(
										i.replace("TINNHAN:", "").replace("{THOIGIAN}", thoiGian(traLoiTrong))));
							} else if (i.contains("THONGBAO:")) {
								player.sendMessage(chatMau(
										i.replace("THONGBAO:", "").replace("{BIETDANH}", player.getDisplayName())
												.replace("{TEN}", player.getName())));
							} else {
								getServer().dispatchCommand(getServer().getConsoleSender(),
										i.replace("{NGUOICHOI}", player.getName()));
							}
						}
					}
				} else {
					player.sendMessage(chatMau(chu("HetThoiGian")));
				}
				cauTraLoi = null;
				thoiGian.clear();
			}
		}
	}
}
