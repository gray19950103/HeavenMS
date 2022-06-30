package asia.wmj.ms.net.server.task;

import asia.wmj.ms.net.server.PlayerStorage;
import asia.wmj.ms.net.server.Server;
import asia.wmj.ms.net.server.channel.Channel;
import asia.wmj.ms.server.maps.MapleMapManager;

/**
 * @author Resinate
 */
public class RespawnTask implements Runnable {
    
    @Override
    public void run() {
        for (Channel ch : Server.getInstance().getAllChannels()) {
            PlayerStorage ps = ch.getPlayerStorage();
            if (ps != null) {
                if (!ps.getAllCharacters().isEmpty()) {
                    MapleMapManager mapManager = ch.getMapFactory();
                    if (mapManager != null) {
                        mapManager.updateMaps();
                    }
                }
            }
        }
    }
}
