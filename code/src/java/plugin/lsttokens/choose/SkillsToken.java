/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.choose;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.choice.GrantedChooser;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class SkillsToken implements ChooseLstToken
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value == null)
		{
			// No args - legal
			StringBuilder sb = new StringBuilder();
			if (prefix.length() > 0)
			{
				sb.append(prefix).append('|');
			}
			sb.append(getTokenName());
			po.setChoiceString(sb.toString());
			return true;
		}
		Logging.errorPrint("CHOOSE:" + getTokenName()
			+ " may not have arguments: " + value);
		return false;
	}

	public String getTokenName()
	{
		return "SKILLS";
	}

	public ChoiceSet<?> parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (value == null)
		{
			// No args - legal
			return GrantedChooser.getGrantedChooser(Skill.class);
		}
		Logging.errorPrint("CHOOSE:" + getTokenName()
			+ " may not have arguments: " + value);
		return null;
	}

	public String unparse(LoadContext context, ChoiceSet<?> chooser)
	{
		return null;
	}
}
