/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.pcclass;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.core.PCClass;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.BonusLoader;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with MONNONSKILLHD Token
 */
public class MonnonskillhdToken extends AbstractToken implements
		PCClassLstToken, PCClassClassLstToken
{

	@Override
	public String getTokenName()
	{
		return "MONNONSKILLHD";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.addBonusList("0|MONNONSKILLHD|NUMBER|" + value);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		BonusObj bonus =
				BonusLoader.getBonus(context, pcc, "MONNONSKILLHD", "NUMBER",
					st.nextToken());
		AssociatedPrereqObject assoc =
				context.getGraphContext().grant(getTokenName(), pcc, bonus);
		assoc.addPrerequisite(getPrerequisite("PRELEVELMAX:1"));
		while (st.hasMoreTokens())
		{
			assoc.addPrerequisite(getPrerequisite(st.nextToken()));
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		AssociatedChanges<BonusObj> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					pcc, BonusObj.class);
		if (changes == null)
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (LSTWriteable b : changes.getAdded())
		{
			// TODO Validate MONNONSKILLHD?
			// TODO Validate NUMBER?
			// TODO Validate PRExxx?
			set.add(b.getLSTformat());
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}
}
