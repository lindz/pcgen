/**
 * SpellLevelChoiceManager.java
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
 * Current Version: $Revision: 285 $
 * Last Editor:     $Author: nuance $
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This is the chooser that deals with choosing a spell level.
 */
public class SpellLevelChoiceManager extends AbstractComplexChoiceManager
{
	private ArrayList aBonusList = new ArrayList();
	private String    stChoices  = "";


	/**
	 * Make a new spell level chooser.
	 *
	 * @param  aPObject
	 * @param  choiceString
	 * @param  aPC
	 */
	public SpellLevelChoiceManager(
	    PObject         aPObject,
	    String          choiceString,
	    PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title          = "Spell Level choice";
		chooserHandled = "SPELLLEVEL";

		/* reconstruct a suitable choiceString to pass to buildSpellTypeChoices.  This is
		 * not necessarily the same as the choiceString that was passed in because we may
		 * have removed some | separated elements from the front of it in the constructor
		 * of the superclass */

		StringBuffer newChoice = new StringBuffer(choiceString.length());
		Iterator     choiceIt  = choices.iterator();

		while (choiceIt.hasNext())
		{
			if (newChoice.length() != 0)
			{
				newChoice.append('|');
			}
			newChoice.append(choiceIt.next());
		}

		stChoices = newChoice.toString();
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 *
	 * @param  aPc
	 * @param  availableList
	 * @param  selectedList
	 */
	public void getChoices(
	    final PlayerCharacter aPc,
	    final List            availableList,
	    final List            selectedList)
	{
		/* This will need to be re-worked at some point when I can think of a better way.
		 * This feat is different from the others in that it requires a bonus to be
		 * embedded in the choice.  Probably this whole feat methodology needs to be
		 * re-thought as its getting a bit bloated - a generic way to embed bonuses could
		 * be done to simplify this all tremendously instead of so many special
		 * cases.*/

		final StringTokenizer cTok      = new StringTokenizer(stChoices, "[]");
		final String          choicesSt = cTok.nextToken();

		while (cTok.hasMoreTokens())
		{
			aBonusList.add(cTok.nextToken());
		}

		final StringTokenizer choicesTok = new StringTokenizer(choicesSt, "|");

		// get appropriate choices for chooser
		ChooserUtilities.buildSpellTypeChoices(
			availableList,
			uniqueList,
			aPc,
			choicesTok);

		pobject.addAssociatedTo(selectedList);
	}

	/**
	 * Perform any necessary clean up of the associated property of pobject.
	 *
	 * @param aPc
	 * @param size
	 */
	protected void cleanUpAssociated(
			PlayerCharacter aPc,
			int             size)
	{

		// remove previous selections from bonuses
		// aBonusList contains the bonuses
		for (int e = 0; e < pobject.getAssociatedCount(); ++e)
		{
			final String aString = pobject.getAssociated(e);

			for (Iterator bonusIter = aBonusList.iterator(); bonusIter.hasNext();)	
			{
				String bonus = (String) bonusIter.next();
				pobject.removeBonus(bonus, aString, aPc);
			}
		}
		pobject.clearAssociated();
	}

	
	/**
	 * Associate a choice with the pobject.  Only here so we can override part
	 * of the behaviour of applyChoices
	 * 
	 * @param aPc 
	 * @param item the choice to associate
	 * @param prefix 
	 */
	protected void associateChoice(
			final PlayerCharacter aPc,
			final String          item,
			final String          prefix)
	{
		final String name = prefix + item;

		for (Iterator e = aBonusList.iterator(); e.hasNext();)
		{
			final String bString = (String) e.next();

			pobject.addAssociated(name);
			pobject.applyBonus(bString, name, aPc, true);
		}

	}

	
	/**
	 * For the times when you want the bonus list instead of the available list.
	 * In a previous life this code was selected by the boolean flag process, if
	 * it was false, then the contents of availableList in getChoices was
	 * replaced with the contents of this
	 *
	 * @return  Returns the aBonusList.
	 */
	public final ArrayList getABonusList()
	{
		return aBonusList;
	}
}
