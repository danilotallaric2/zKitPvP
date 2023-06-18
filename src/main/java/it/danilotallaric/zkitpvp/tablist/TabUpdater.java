package it.danilotallaric.zkitpvp.tablist;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import org.bukkit.Bukkit;

public class TabUpdater implements Runnable {

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            TabComponent component = new TabComponent();

            KitPvP.getFileManager().getConfig().getStringList("tab-list.header")
                    .forEach(header -> component.addLineToHeader(ChatUtils.formatMessage(player, header)));
            KitPvP.getFileManager().getConfig().getStringList("tab-list.footer")
                    .forEach(footer -> component.addLineToFooter(ChatUtils.formatMessage(player, footer)));

            component.sendToPlayer(player);
        });
    }

}
