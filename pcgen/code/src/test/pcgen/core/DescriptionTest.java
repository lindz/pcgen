/*
 * DescriptionTest.java
 *
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author: $
 *
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * This class tests the handling of DESC fields in PCGen
 */
@SuppressWarnings("nls")
public class DescriptionTest extends AbstractCharacterTestCase
{
	/**
	 * Constructs a new <code>DescriptionTest</code>.
	 */
	public DescriptionTest()
	{
		super();
	}

	/**
	 * Tests outputting an empty description.
	 *
	 */
	public void testEmptyDesc()
	{
		final Description desc = new Description(Constants.EMPTY_STRING);
		assertTrue(desc.getDescription(this.getCharacter(), null).equals(""));
	}

	/**
	 * Tests outputting a simple description.
	 *
	 */
	public void testSimpleDesc()
	{
		final String simpleDesc = "This is a test";
		final Description desc = new Description(simpleDesc);
		assertTrue(desc.getDescription(getCharacter(), null).equals(simpleDesc));
	}

	/**
	 * Test PREREQs for Desc
	 * @throws Exception
	 */
	public void testPreReqs() throws Exception
	{
		final String simpleDesc = "This is a test";
		final Description desc = new Description(simpleDesc);

		final PreParserFactory factory = PreParserFactory.getInstance();

		final Prerequisite prereqNE = factory.parse("PRETEMPLATE:1,KEY_Natural Lycanthrope");
		desc.addPrerequisite(prereqNE);
		is(desc.getDescription(getCharacter(), null), strEq(""));

		PCTemplate template = new PCTemplate();
		template.setName("Natural Lycanthrope");
		template.put(StringKey.KEY_NAME, "KEY_Natural Lycanthrope");
		Globals.getContext().ref.importObject(template);
		getCharacter().addTemplate(template);
		is(desc.getDescription(getCharacter(), null), strEq(simpleDesc));
	}

	/**
	 * Tests a simple string replacement.
	 */
	public void testSimpleReplacement()
	{
		final Description desc = new Description("%1");
		desc.addVariable("\"Variable\"");
		assertTrue(desc.getDescription(getCharacter(), null).equals("Variable"));
	}

	/**
	 * Test name replacement
	 */
	public void testSimpleNameReplacement()
	{
		final PObject pobj = new PObject();
		pobj.setName("PObject");

		final Description desc = new Description("%1");
		desc.addVariable("%NAME");
		assertTrue(desc.getDescription(getCharacter(), pobj).equals("PObject"));
	}

	/**
	 * Tests simple variable replacement
	 */
	public void testSimpleVariableReplacement()
	{
		final Race dummy = new Race();
		dummy.put(VariableKey.getConstant("TestVar"), FormulaFactory
				.getFormulaFor(2));

		final Description desc = new Description("%1");
		desc.addVariable("TestVar");
		assertTrue(desc.getDescription(getCharacter(), dummy).equals("0"));

		getCharacter().setRace(dummy);
		assertTrue(desc.getDescription(getCharacter(), dummy).equals("2"));
	}

	/**
	 * Tests simple replacement of %CHOICE
	 */
	public void testSimpleChoiceReplacement()
	{
		final PObject pobj = new PObject();
		Globals.getContext().unconditionallyProcess(pobj, "CHOOSE", "LANG|ALL");
		Globals.getContext().ref.constructCDOMObject(Language.class, "Foo");

		final Description desc = new Description("%1");
		desc.addVariable("%CHOICE");
		PlayerCharacter pc = getCharacter();
		assertTrue(desc.getDescription(pc, pobj).equals(""));

		add(ChooserUtilities.getChoiceManager(pobj, pc), pc, pobj, "Foo");
		assertTrue(desc.getDescription(pc, pobj).equals("Foo"));
	}

	/**
	 * Tests simple %LIST replacement.
	 */
	public void testSimpleListReplacement()
	{
		final PObject pobj = new PObject();
		Globals.getContext().unconditionallyProcess(pobj, "CHOOSE", "LANG|ALL");
		Globals.getContext().ref.constructCDOMObject(Language.class, "Foo");

		final Description desc = new Description("%1");
		desc.addVariable("%LIST");
		PlayerCharacter pc = getCharacter();
		assertTrue(desc.getDescription(pc, pobj).equals(""));

		add(ChooserUtilities.getChoiceManager(pobj, pc), pc, pobj, "Foo");
		assertTrue(desc.getDescription(pc, pobj).equals("Foo"));
	}

	/**
	 * Test a replacement with missing variables.
	 */
	public void testEmptyReplacement()
	{
		final PObject pobj = new PObject();

		final Description desc = new Description("%1");
		assertTrue(desc.getDescription(getCharacter(), pobj).equals(""));
	}

	/**
	 * Test having extra variables present
	 */
	public void testExtraVariables()
	{
		final PObject pobj = new PObject();
		Globals.getContext().unconditionallyProcess(pobj, "CHOOSE", "LANG|ALL");
		Globals.getContext().ref.constructCDOMObject(Language.class, "Foo");

		final Description desc = new Description("Testing");
		desc.addVariable("%LIST");
		PlayerCharacter pc = getCharacter();
		assertTrue(desc.getDescription(pc, pobj).equals("Testing"));

		add(ChooserUtilities.getChoiceManager(pobj, pc), pc, pobj, "Foo");
		assertTrue(desc.getDescription(pc, pobj).equals("Testing"));
	}

	/**
	 * Test complex replacements.
	 */
	public void testComplexVariableReplacement()
	{
		final Ability dummy = new Ability();
		Globals.getContext().unconditionallyProcess(dummy, "CATEGORY", "FEAT");
		Globals.getContext().unconditionallyProcess(dummy, "CHOOSE", "LANG|ALL");
		Globals.getContext().unconditionallyProcess(dummy, "MULT", "YES");
		Globals.getContext().ref.constructCDOMObject(Language.class, "Associated 1");
		Globals.getContext().ref.constructCDOMObject(Language.class, "Associated 2");
		dummy.put(VariableKey.getConstant("TestVar"), FormulaFactory
				.getFormulaFor(2));
		Globals.getContext().resolveReferences(null);
		PlayerCharacter pc = getCharacter();

		final Description desc = new Description("%1 test %3 %2");
		desc.addVariable("TestVar");
		assertEquals("0 test  ", desc.getDescription(pc, dummy));

		AbilityCategory category = AbilityCategory.FEAT;

		Ability pcAbility = pc.addAbilityNeedCheck(category, dummy);
		AbilityUtilities.finaliseAbility(pcAbility, "Associated 1", pc, category);
		AbilityUtilities.finaliseAbility(pcAbility, "Associated 2", pc, category);
		assertEquals("2 test  ", desc.getDescription(pc, dummy));

		desc.addVariable("%CHOICE");
		assertEquals("2 test  Associated 1", desc
			.getDescription(pc, pcAbility));

		desc.addVariable("%LIST");
		assertEquals("Replacement of %LIST failed",
			"2 test Associated 1 and Associated 2 Associated 1", desc
				.getDescription(pc, pcAbility));
	}

	private static <T> void add(ChoiceManagerList<T> aMan, PlayerCharacter pc,
		CDOMObject obj, String choice)
	{
		T sel = aMan.decodeChoice(choice);
		aMan.applyChoice(pc, obj, sel);
	}

}
