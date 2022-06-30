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

import asia.wmj.ms.client.MapleClient;
import asia.wmj.ms.client.inventory.Item;
import asia.wmj.ms.constants.inventory.ItemConstants;
import asia.wmj.ms.net.AbstractMaplePacketHandler;
import asia.wmj.ms.scripting.item.ItemScriptManager;
import asia.wmj.ms.server.MapleItemInformationProvider;
import asia.wmj.ms.server.MapleItemInformationProvider.ScriptedItem;
import asia.wmj.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Jay Estrella
 */
public final class ScriptedItemHandler extends AbstractMaplePacketHandler {
    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readInt(); // trash stamp, thanks RMZero213
        short itemSlot = slea.readShort(); // item slot, thanks RMZero213
        int itemId = slea.readInt();
        
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        ScriptedItem info = ii.getScriptedItemInfo(itemId);
        if (info == null) return;
        
        Item item = c.getPlayer().getInventory(ItemConstants.getInventoryType(itemId)).getItem(itemSlot);
        if (item == null || item.getItemId() != itemId || item.getQuantity() < 1) {
            return;
        }
        
        ItemScriptManager ism = ItemScriptManager.getInstance();
        ism.runItemScript(c, info);
    }
}
