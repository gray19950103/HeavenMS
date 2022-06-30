/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package asia.wmj.ms.net.server.channel.handlers;

import asia.wmj.ms.client.MapleCharacter;
import asia.wmj.ms.client.MapleClient;
import asia.wmj.ms.net.AbstractMaplePacketHandler;
import asia.wmj.ms.tools.MaplePacketCreator;
import asia.wmj.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author RonanLana
 */
public class UseMapleLifeHandler extends AbstractMaplePacketHandler {
    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        long timeNow = currentServerTime();
        
        if(timeNow - player.getLastUsedCashItem() < 3000) {
            player.dropMessage(5, "Please wait a moment before trying again.");
            c.announce(MaplePacketCreator.sendMapleLifeError(3));
            c.announce(MaplePacketCreator.enableActions());
            return;
        }
        player.setLastUsedCashItem(timeNow);
        
        String name = slea.readMapleAsciiString();
        if(MapleCharacter.canCreateChar(name)) {
            c.announce(MaplePacketCreator.sendMapleLifeCharacterInfo());
        } else {
            c.announce(MaplePacketCreator.sendMapleLifeNameError());
        }
        c.announce(MaplePacketCreator.enableActions());
    }
}
