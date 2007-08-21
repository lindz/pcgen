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
package plugin.lsttokens.add;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.factory.GrantFactory;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.ReferenceChoiceSet;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.AddLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class SkillToken extends AbstractToken implements AddLstToken
{

	private static final Class<Skill> SKILL_CLASS = Skill.class;

	public boolean parse(PObject target, String value, int level)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		String countString;
		String items;
		if (pipeLoc == -1)
		{
			countString = "1";
			items = value;
		}
		else
		{
			if (pipeLoc != value.lastIndexOf(Constants.PIPE))
			{
				Logging.errorPrint("Syntax of ADD:" + getTokenName()
					+ " only allows one | : " + value);
				return false;
			}
			countString = value.substring(0, pipeLoc);
			items = value.substring(pipeLoc + 1);
		}
		target.addAddList(level, getTokenName() + "(" + items + ")"
			+ countString);
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "SKILL";
	}

	public boolean parse(LoadContext context, PObject obj, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		int count;
		String items;
		if (pipeLoc == -1)
		{
			count = 1;
			items = value;
		}
		else
		{
			String countString = value.substring(0, pipeLoc);
			try
			{
				count = Integer.parseInt(countString);
				if (count < 1)
				{
					Logging.errorPrint("Count in ADD:" + getTokenName()
						+ " must be > 0");
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Invalid Count in ADD:" + getTokenName()
					+ ": " + countString);
				return false;
			}
			items = value.substring(pipeLoc + 1);
		}

		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			return false;
		}
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);

		boolean foundAny = false;
		boolean foundOther = false;

		List<CDOMReference<Skill>> refs = new ArrayList<CDOMReference<Skill>>();
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<Skill> ref;
			if (Constants.LST_ANY.equalsIgnoreCase(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(SKILL_CLASS);
			}
			else
			{
				foundOther = true;
				ref =
						TokenUtilities.getTypeOrPrimitive(context, SKILL_CLASS,
							token);
				if (ref == null)
				{
					Logging
						.errorPrint("  Error was encountered while parsing ADD:"
							+ getTokenName()
							+ ": "
							+ token
							+ " is not a valid reference: " + value);
					return false;
				}
			}
			refs.add(ref);
		}

		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		ReferenceChoiceSet<Skill> rcs = new ReferenceChoiceSet<Skill>(refs);
		ChoiceSet<Skill> cs = new ChoiceSet<Skill>("ADD", rcs);
		PCGraphGrantsEdge edge =
				context.getGraphContext().grant(getTokenName(), obj, cs);
		edge.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
			.getFormulaFor(count));
		edge.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
			.getFormulaFor(Integer.MAX_VALUE));
		GrantFactory<Skill> gf = new GrantFactory<Skill>(edge);
		gf.setAssociation(AssociationKey.WEIGHT, Integer.valueOf(1));
		context.getGraphContext().grant(getTokenName(), obj, gf);
		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
	{
		GraphChanges<ChoiceSet> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					obj, ChoiceSet.class);
		if (changes == null)
		{
			return null;
		}
		MapToList<LSTWriteable, AssociatedPrereqObject> mtl =
				changes.getAddedAssociations();
		if (mtl == null || mtl.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> addStrings = new ArrayList<String>();
		for (LSTWriteable lstw : mtl.getKeySet())
		{
			ChoiceSet<?> cs = (ChoiceSet<?>) lstw;
			if (SKILL_CLASS.equals(cs.getChoiceClass()))
			{
				List<AssociatedPrereqObject> assocList = mtl.getListFor(lstw);
				if (assocList.size() != 1)
				{
					context
						.addWriteMessage("Only one Association to a CHOOSE can be made per object");
					return null;
				}
				AssociatedPrereqObject assoc = assocList.get(0);
				Formula f = assoc.getAssociation(AssociationKey.CHOICE_COUNT);
				if (f == null)
				{
					// Error
					return null;
				}
				String fString = f.toString();
				if ("1".equals(fString))
				{
					addStrings.add(cs.getLSTformat());
				}
				else
				{
					addStrings.add(fString + "|" + cs.getLSTformat());
				}
				// assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}
}
