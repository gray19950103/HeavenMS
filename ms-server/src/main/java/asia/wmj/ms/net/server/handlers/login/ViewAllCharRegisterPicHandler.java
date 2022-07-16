package asia.wmj.ms.net.server.handlers.login;

import asia.wmj.ms.client.MapleClient;
import java.net.InetAddress;
import java.net.UnknownHostException;

import asia.wmj.ms.net.server.world.World;
import asia.wmj.ms.net.AbstractMaplePacketHandler;
import asia.wmj.ms.net.server.Server;
import asia.wmj.ms.net.server.coordinator.session.MapleSessionCoordinator;
import asia.wmj.ms.net.server.coordinator.session.MapleSessionCoordinator.AntiMultiClientResult;
import org.apache.mina.core.session.IoSession;
import asia.wmj.ms.tools.MaplePacketCreator;
import asia.wmj.ms.tools.Randomizer;
import asia.wmj.ms.tools.data.input.SeekableLittleEndianAccessor;

public final class ViewAllCharRegisterPicHandler extends AbstractMaplePacketHandler {

    private static int parseAntiMulticlientError(AntiMultiClientResult res) {
        switch (res) {
            case REMOTE_PROCESSING:
                return 10;

            case REMOTE_LOGGEDIN:
                return 7;

            case REMOTE_NO_MATCH:
                return 17;
                
            case COORDINATOR_ERROR:
                return 8;
                
            default:
                return 9;
        }
    }
    
    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readByte();
        int charId = slea.readInt();
        slea.readInt(); // please don't let the client choose which world they should login
        
        String mac = slea.readMapleAsciiString();
        String hwid = slea.readMapleAsciiString();
        
        if (!hwid.matches("[0-9A-F]{12}_[0-9A-F]{8}")) {
            c.announce(MaplePacketCreator.getAfterLoginError(17));
            return;
        }
        
        c.updateMacs(mac);
        c.updateHWID(hwid);
        
        if (c.hasBannedMac() || c.hasBannedHWID()) {
            MapleSessionCoordinator.getInstance().closeSession(c.getSession(), true);
            return;
        }
        
        IoSession session = c.getSession();
        AntiMultiClientResult res = MapleSessionCoordinator.getInstance().attemptGameSession(session, c.getAccID(), hwid);
        if (res != AntiMultiClientResult.SUCCESS) {
            c.announce(MaplePacketCreator.getAfterLoginError(parseAntiMulticlientError(res)));
            return;
        }
        
        Server server = Server.getInstance();
        if(!server.haveCharacterEntry(c.getAccID(), charId)) {
            MapleSessionCoordinator.getInstance().closeSession(c.getSession(), true);
            return;
        }
        
        c.setWorld(server.getCharacterWorld(charId));
        World wserv = c.getWorldServer();
        if(wserv == null || wserv.isWorldCapacityFull()) {
            c.announce(MaplePacketCreator.getAfterLoginError(10));
            return;
        }
        
        int channel = Randomizer.rand(1, server.getWorld(c.getWorld()).getChannelsSize());
        c.setChannel(channel);
        
        String pic = slea.readMapleAsciiString();
        c.setPic(pic);
        
        String[] socket = server.getInetSocket(c.getWorld(), channel);
        if (socket == null) {
            c.announce(MaplePacketCreator.getAfterLoginError(10));
            return;
        }
        
        server.unregisterLoginState(c);
        c.setCharacterOnSessionTransitionState(charId);
        
        try {
            c.announce(MaplePacketCreator.getServerIP(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]), charId));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
