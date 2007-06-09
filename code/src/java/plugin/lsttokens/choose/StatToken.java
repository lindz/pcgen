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

import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.choice.SetChooser;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.Constants;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class StatToken implements ChooseLstToken
{

	private static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value == null)
		{
			// No args - use all stats - legal
			return true;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}
		List<PCStat> list = SettingsHandler.getGame().getUnmodifiableStatList();
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		TOKENS: while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			for (PCStat stat : list)
			{
				if (tokText.equals(stat.getAbb()))
				{
					continue TOKENS;
				}
			}
			Logging.errorPrint("Did not find STAT: " + tokText
				+ " used in CHOOSE: " + value);
		}
		StringBuilder sb = new StringBuilder();
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName()).append('|').append(value);
		po.setChoiceString(sb.toString());
		return true;
	}

	public String getTokenName()
	{
		return "STAT";
	}

	public ChoiceSet<PCStat> parse(LoadContext context, CDOMObject obj,
		String value) throws PersistenceLayerException
	{
		Set<PCStat> stats = context.ref.getConstructedCDOMObjects(PCSTAT_CLASS);
		// null means no args - use all stats - legal
		if (value != null)
		{
			if (value.indexOf('[') != -1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not contain [] : " + value);
				return null;
			}
			if (value.charAt(0) == '|')
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not start with | : " + value);
				return null;
			}
			if (value.charAt(value.length() - 1) == '|')
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not end with | : " + value);
				return null;
			}
			if (value.indexOf("||") != -1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments uses double separator || : " + value);
				return null;
			}
			StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
			while (tok.hasMoreTokens())
			{
				String tokText = tok.nextToken();
				PCStat stat =
						context.ref.silentlyGetConstructedCDOMObject(
							PCSTAT_CLASS, tokText);
				if (stat == null)
				{
					Logging.errorPrint("Did not find STAT: " + tokText
						+ " used in CHOOSE: " + value);
					return null;
				}
				if (!stats.remove(stat))
				{
					Logging.errorPrint("STAT: " + tokText
						+ " appeared twice in CHOOSE: " + value);
					return null;
				}
			}
		}
		return new SetChooser<PCStat>(stats);
	}
}
