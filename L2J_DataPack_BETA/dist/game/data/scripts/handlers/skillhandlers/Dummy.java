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
package handlers.skillhandlers;

import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.handler.ISkillHandler;
import com.l2jserver.gameserver.instancemanager.HandysBlockCheckerManager;
import com.l2jserver.gameserver.model.ArenaParticipantsHolder;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2BlockInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.EffectScope;
import com.l2jserver.gameserver.model.skills.L2Skill;
import com.l2jserver.gameserver.model.skills.L2SkillType;
import com.l2jserver.gameserver.model.stats.Formulas;

public class Dummy implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.DUMMY
	};
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		switch (skill.getId())
		{
			case 5852:
			case 5853:
			{
				final L2Object obj = targets[0];
				if (obj != null)
				{
					useBlockCheckerSkill(activeChar.getActingPlayer(), skill, obj);
				}
				break;
			}
			default:
			{
				for (L2Character target : (L2Character[]) targets)
				{
					if (Formulas.calcBuffDebuffReflection(target, skill))
					{
						skill.applyEffects(target, activeChar);
					}
					else
					{
						skill.applyEffects(activeChar, target);
					}
				}
				break;
			}
		}
		
		// Self Effect
		if (skill.hasEffects(EffectScope.SELF))
		{
			if (activeChar.isAffectedBySkill(skill.getId()))
			{
				activeChar.stopSkillEffects(true, skill.getId());
			}
			skill.applyEffects(activeChar, null, activeChar, true, false, true, 0);
		}
		
		if (skill.useSpiritShot())
		{
			activeChar.setChargedShot(activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS) ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS, false);
		}
		else
		{
			activeChar.setChargedShot(ShotType.SOULSHOTS, false);
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
	
	private final void useBlockCheckerSkill(L2PcInstance activeChar, L2Skill skill, L2Object target)
	{
		if (!(target instanceof L2BlockInstance))
		{
			return;
		}
		
		L2BlockInstance block = (L2BlockInstance) target;
		
		final int arena = activeChar.getBlockCheckerArena();
		if (arena != -1)
		{
			final ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(arena);
			if (holder == null)
			{
				return;
			}
			
			final int team = holder.getPlayerTeam(activeChar);
			final int color = block.getColorEffect();
			if ((team == 0) && (color == 0x00))
			{
				block.changeColor(activeChar, holder, team);
			}
			else if ((team == 1) && (color == 0x53))
			{
				block.changeColor(activeChar, holder, team);
			}
		}
	}
}