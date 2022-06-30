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
package asia.wmj.ms.server.quest.requirements;

import java.util.ArrayList;
import java.util.List;

import asia.wmj.ms.provider.MapleData;
import asia.wmj.ms.provider.MapleDataTool;
import asia.wmj.ms.server.quest.MapleQuest;
import asia.wmj.ms.server.quest.MapleQuestRequirementType;
import asia.wmj.ms.client.MapleCharacter;
import asia.wmj.ms.client.MapleJob;

/**
 *
 * @author Tyler (Twdtwd)
 */
public class JobRequirement extends MapleQuestRequirement {
	List<Integer> jobs = new ArrayList<>();
	
	public JobRequirement(MapleQuest quest, MapleData data) {
		super(MapleQuestRequirementType.JOB);
		processData(data);
	}
	
	/**
	 * 
	 * @param data 
	 */
	@Override
	public void processData(MapleData data) {
		for (MapleData jobEntry : data.getChildren()) {
			jobs.add(MapleDataTool.getInt(jobEntry));
		}
	}
	
	
	@Override
	public boolean check(MapleCharacter chr, Integer npcid) {
		for(Integer job : jobs) {
			if (chr.getJob().equals(MapleJob.getById(job)) || chr.isGM()) {
				return true;
			}
		}
		return false;
	}
}
