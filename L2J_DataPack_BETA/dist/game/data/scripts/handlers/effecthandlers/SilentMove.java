/*
 * Copyright (C) 2004-2014 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.EffectFlag;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Silent Move effect implementation.
 */
public final class SilentMove extends AbstractEffect
{
	public SilentMove(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.SILENT_MOVE.getMask();
	}
	
	@Override
	public boolean onActionTime(BuffInfo info)
	{
		if (info.getEffected().isDead())
		{
			return false;
		}
		
		final double manaDam = getValue() * getTicksMultiplier();
		if (manaDam > info.getEffected().getCurrentMp())
		{
			info.getEffected().sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
			return false;
		}
		
		info.getEffected().reduceCurrentMp(manaDam);
		return info.getSkill().isToggle();
	}
}
