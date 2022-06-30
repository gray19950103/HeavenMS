/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

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
import asia.wmj.ms.client.MapleCharacter.FameStatus;
import asia.wmj.ms.client.autoban.AutobanFactory;
import asia.wmj.ms.client.MapleClient;
import asia.wmj.ms.net.AbstractMaplePacketHandler;
import asia.wmj.ms.tools.FilePrinter;
import asia.wmj.ms.tools.MaplePacketCreator;
import asia.wmj.ms.tools.data.input.SeekableLittleEndianAccessor;

public final class GiveFameHandler extends AbstractMaplePacketHandler {
    
    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter target = (MapleCharacter) c.getPlayer().getMap().getMapObject(slea.readInt());
        int mode = slea.readByte();
        int famechange = 2 * mode - 1;
        MapleCharacter player = c.getPlayer();
        if (target == null || target.getId() == player.getId() || player.getLevel() < 15) {
            return;
        } else if (famechange != 1 && famechange != -1) {
            AutobanFactory.PACKET_EDIT.alert(c.getPlayer(), c.getPlayer().getName() + " tried to packet edit fame.");
            FilePrinter.printError(FilePrinter.EXPLOITS + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + " tried to fame hack with famechange " + famechange);
            c.disconnect(true, false);
            return;
        }
        
        FameStatus status = player.canGiveFame(target);
        if (status == FameStatus.OK) {
            if (target.gainFame(famechange, player, mode)) {
                if (!player.isGM()) {
                    player.hasGivenFame(target);
                }
            } else {
                player.message("Could not process the request, since this character currently has the minimum/maximum level of fame.");
            }
        } else {
            c.announce(MaplePacketCreator.giveFameErrorResponse(status == FameStatus.NOT_TODAY ? 3 : 4));
        }
    }
}