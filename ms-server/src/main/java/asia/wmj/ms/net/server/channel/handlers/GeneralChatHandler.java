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
import asia.wmj.ms.client.MapleClient;
import asia.wmj.ms.client.autoban.AutobanFactory;
import asia.wmj.ms.client.command.CommandsExecutor;
import asia.wmj.ms.config.YamlConfig;
import asia.wmj.ms.net.AbstractMaplePacketHandler;
import asia.wmj.ms.tools.FilePrinter;
import asia.wmj.ms.tools.LogHelper;
import asia.wmj.ms.tools.MaplePacketCreator;
import asia.wmj.ms.tools.data.input.SeekableLittleEndianAccessor;

public final class GeneralChatHandler extends AbstractMaplePacketHandler {    
	@Override
        public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
                String s = slea.readMapleAsciiString();
                MapleCharacter chr = c.getPlayer();
                if(chr.getAutobanManager().getLastSpam(7) + 200 > currentServerTime()) {
                        c.announce(MaplePacketCreator.enableActions());
                        return;
                }
                if (s.length() > Byte.MAX_VALUE && !chr.isGM()) {
                        AutobanFactory.PACKET_EDIT.alert(c.getPlayer(), c.getPlayer().getName() + " tried to packet edit in General Chat.");
                        FilePrinter.printError(FilePrinter.EXPLOITS + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + " tried to send text with length of " + s.length());
                        c.disconnect(true, false);
                        return;
                }
                char heading = s.charAt(0);
                if (CommandsExecutor.isCommand(c, s)) {
                        CommandsExecutor.getInstance().handle(c, s);
                } else if (heading != '/') {
                        int show = slea.readByte();
                        if(chr.getMap().isMuted() && !chr.isGM()) {
                                chr.dropMessage(5, "The map you are in is currently muted. Please try again later.");
                                return;
                        }

                        if (!chr.isHidden()) {
                                chr.getMap().broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), s, chr.getWhiteChat(), show));
                                if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
                                        LogHelper.logChat(c, "General", s);
                                }
                        } else {
                                chr.getMap().broadcastGMMessage(MaplePacketCreator.getChatText(chr.getId(), s, chr.getWhiteChat(), show));
                                if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
                                        LogHelper.logChat(c, "GM General", s);
                                }
                        }

                        chr.getAutobanManager().spam(7);
                }
        }
}