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

import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.input.ActiveAbilityFacet;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Language;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.add.VFeatToken;
import tokenmodel.testsupport.AbstractAddListTokenTest;

public class AddTargetedVFeatTest extends AbstractAddListTokenTest<Ability>
{

	private static final VFeatToken ADD_VFEAT_TOKEN = new VFeatToken();
	private ActiveAbilityFacet activeAbilityFacet = FacetLibrary
		.getFacet(ActiveAbilityFacet.class);

	@Override
	public void processToken(CDOMObject source)
	{
		ParseResult result =
				ADD_VFEAT_TOKEN.parseToken(context, source, "Granted (English)");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
	}

	@Override
	protected Class<Ability> getGrantClass()
	{
		return Ability.class;
	}

	@Override
	protected ActiveAbilityFacet getTargetFacet()
	{
		return activeAbilityFacet;
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return AUTO_LANG_TOKEN;
	}

	@Override
	protected int getCount()
	{
		return getTargetFacet().get(id, AbilityCategory.FEAT, Nature.VIRTUAL)
			.size();
	}

	@Override
	protected boolean containsExpected(Ability granted)
	{
		Set<Ability> abilities =
				getTargetFacet().get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		for (Ability a : abilities)
		{
			boolean abilityExpected =
					a.equals(context.ref.silentlyGetConstructedCDOMObject(
						Ability.class, AbilityCategory.FEAT, "Granted"));
			if (abilityExpected)
			{
				Ability g = pc.getAbilityKeyed(AbilityCategory.FEAT, "Granted");
				if (pc.getDetailedAssociationCount(g) == 1)
				{
					if (!pc.getAssociationList(g).get(0).equals("English"))
					{
						continue;
					}
					Language english =
							context.ref.silentlyGetConstructedCDOMObject(
								Language.class, "English");
					languageFacet.contains(id, english);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected boolean cleanedSideEffects()
	{
		Language english =
				context.ref.silentlyGetConstructedCDOMObject(Language.class,
					"English");
		return !languageFacet.contains(id, english)
			&& super.cleanedSideEffects();
	}

	@Override
	protected Ability createGrantedObject()
	{
		context.ref.constructCDOMObject(Language.class, "English");
		Ability a = super.createGrantedObject();
		ParseResult result = AUTO_LANG_TOKEN.parseToken(context, a, "%LIST");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = ABILITY_MULT_TOKEN.parseToken(context, a, "YES");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = CHOOSE_LANG_TOKEN.parseToken(context, a, "ALL");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		context.ref.reassociateCategory(AbilityCategory.FEAT, a);
		return a;
	}

	//TODO CODE-2016/CODE-1921 (needs to be consistent with other methods of ADD:)
	@Override
	public void testFromAbility() throws PersistenceLayerException
	{
		//Not supported equivalent to other methods
	}

	//TODO CODE-2016 (needs to be consistent with other methods of ADD:)
	@Override
	public void testFromClass() throws PersistenceLayerException
	{
		//Not supported equivalent to other methods
	}
	
	
}
