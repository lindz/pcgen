/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package tokenmodel;

import java.util.Collection;

import org.junit.Test;

import pcgen.cdom.content.SourcedSelection;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.model.DomainSelectionFacet;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Domain;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.ability.MultToken;
import plugin.lsttokens.choose.LangToken;
import plugin.lsttokens.domain.FeatToken;
import tokenmodel.testsupport.AbstractTokenModelTest;

public class DomainFeatListTargetTest extends AbstractTokenModelTest
{
	private static FeatToken token = new FeatToken();
	private DomainSelectionFacet domainSelectionFacet;

	@Test
	public void testSimple() throws PersistenceLayerException
	{
		Domain source = create(Domain.class, "Source");
		PCClass pcc = create(PCClass.class, "Class");
		Ability granted = createGrantedObject();
		Language sel =
				context.ref.constructCDOMObject(Language.class, "English");
		ParseResult result =
				new MultToken().parseToken(context, granted, "YES");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = new LangToken().parseToken(context, granted, "ALL");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = new LangToken().parseToken(context, source, "ALL");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = token.parseToken(context, source, "Granted (%LIST)");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(0, directAbilityFacet.getCount(id));
		ClassSource classSource = new ClassSource(pcc);

		SourcedSelection<Domain, Language, ClassSource> ss =
				SourcedSelection.getSelection(source, sel, classSource);
		domainSelectionFacet.add(id, ss, source);
		assertTrue(containsExpected(granted));
		assertEquals(1, directAbilityFacet.getCount(id));
		domainSelectionFacet.remove(id, ss, source);
		assertEquals(0, directAbilityFacet.getCount(id));
	}

	private boolean containsExpected(Ability granted)
	{
		Collection<CategorizedAbilitySelection> casSet =
				directAbilityFacet.getSet(id);
		for (CategorizedAbilitySelection cas : casSet)
		{
			boolean featExpected =
					cas.getAbilityCategory() == AbilityCategory.FEAT;
			boolean abilityExpected =
					cas.getAbility().equals(
						context.ref.silentlyGetConstructedCDOMObject(
							Ability.class, AbilityCategory.FEAT, "Granted"));
			boolean natureExpected = cas.getNature() == Nature.AUTOMATIC;
			boolean selectionExpected = "English".equals(cas.getSelection());
			if (featExpected && abilityExpected && natureExpected
				&& selectionExpected)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return token;
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		domainSelectionFacet =
				FacetLibrary.getFacet(DomainSelectionFacet.class);
	}

	protected Ability createGrantedObject()
	{
		Ability a = create(Ability.class, "Granted");
		context.ref.reassociateCategory(AbilityCategory.FEAT, a);
		return a;
	}
}