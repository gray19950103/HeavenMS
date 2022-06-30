package asia.wmj.ms.net.server.task;

import asia.wmj.ms.client.MapleCharacter;
import asia.wmj.ms.config.YamlConfig;
import asia.wmj.ms.net.server.world.World;
import asia.wmj.ms.tools.FilePrinter;

import java.util.Collection;

/**
 *
 * @author Shavit
 */
public class TimeoutTask extends BaseTask implements Runnable {
    @Override
    public void run() {
        long time = System.currentTimeMillis();
        Collection<MapleCharacter> chars = wserv.getPlayerStorage().getAllCharacters();
        for(MapleCharacter chr : chars) {
            if(time - chr.getClient().getLastPacket() > YamlConfig.config.server.TIMEOUT_DURATION) {
                FilePrinter.print(FilePrinter.DCS + chr.getClient().getAccountName(), chr.getName() + " auto-disconnected due to inactivity.");
                chr.getClient().disconnect(true, chr.getCashShop().isOpened());
            }
        }
    }

    public TimeoutTask(World world) {
        super(world);
    }
}
