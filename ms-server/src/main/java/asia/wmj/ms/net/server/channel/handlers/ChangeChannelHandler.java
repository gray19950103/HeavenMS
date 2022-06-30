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

import asia.wmj.ms.net.AbstractMaplePacketHandler;
import asia.wmj.ms.tools.data.input.SeekableLittleEndianAccessor;
import asia.wmj.ms.client.MapleClient;
import asia.wmj.ms.client.autoban.AutobanFactory;
import asia.wmj.ms.net.server.Server;

/**
 *
 * @author Matze
 */
public final class ChangeChannelHandler extends AbstractMaplePacketHandler {

    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int channel = slea.readByte() + 1;
        slea.readInt();
        c.getPlayer().getAutobanManager().setTimestamp(6, Server.getInstance().getCurrentTimestamp(), 3);
        if(c.getChannel() == channel) {
                AutobanFactory.GENERAL.alert(c.getPlayer(), "CCing to same channel.");
                c.disconnect(false, false);
                return;
        } else if (c.getPlayer().getCashShop().isOpened() || c.getPlayer().getMiniGame() != null || c.getPlayer().getPlayerShop() != null) {
    		return;
    	}
        
        c.changeChannel(channel);
    }
}