/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.add;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLstToken;
import pcgen.persistence.lst.GlobalLstToken;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

public abstract class AbstractAddTokenTestCase extends
		AbstractGlobalTokenTestCase
{

	static AddLst token = new AddLst();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(getSubToken());
	}

	protected char getJoinCharacter()
	{
		return ',';
	}

	protected abstract AddLstToken getSubToken();

	protected abstract <T extends PObject> Class<T> getSubTokenType();

	public String getSubTokenString()
	{
		return getSubToken().getTokenName();
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|String"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputType() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestType"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputJoinedPipe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1|TestWP2"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	public abstract boolean isTypeLegal();

	public abstract boolean isAllLegal();

	@Test
	public void testInvalidInputTypeEmpty() throws PersistenceLayerException
	{
		try
		{
			boolean result =
					getToken().parse(primaryContext, primaryProf,
						getSubTokenString() + "|TYPE=");
			if (isTypeLegal())
			{
				assertFalse(result);
				assertTrue(primaryGraph.isEmpty());
			}
			else
			{
				assertTrue(result);
				assertFalse(primaryContext.ref.validate());
			}
		}
		catch (IllegalArgumentException e)
		{
			if (isTypeLegal())
			{
				// Should have returned false;
				throw e;
			}
		}
	}

	@Test
	public void testInvalidInputTypeUnterminated()
		throws PersistenceLayerException
	{
		try
		{
			boolean result =
					getToken().parse(primaryContext, primaryProf,
						getSubTokenString() + "|TYPE=One.");
			if (isTypeLegal())
			{
				assertFalse(result);
				assertTrue(primaryGraph.isEmpty());
			}
			else
			{
				assertTrue(result);
				assertFalse(primaryContext.ref.validate());
			}
		}
		catch (IllegalArgumentException e)
		{
			if (isTypeLegal())
			{
				// Should have returned false;
				throw e;
			}
		}
	}

	@Test
	public void testInvalidInputTypeDoubleSeparator()
		throws PersistenceLayerException
	{
		try
		{
			boolean result =
					getToken().parse(primaryContext, primaryProf,
						getSubTokenString() + "|TYPE=One..Two");
			if (isTypeLegal())
			{
				assertFalse(result);
				assertTrue(primaryGraph.isEmpty());
			}
			else
			{
				assertTrue(result);
				assertFalse(primaryContext.ref.validate());
			}
		}
		catch (IllegalArgumentException e)
		{
			if (isTypeLegal())
			{
				// Should have returned false;
				throw e;
			}
		}
	}

	@Test
	public void testInvalidInputTypeFalseStart()
		throws PersistenceLayerException
	{
		try
		{
			boolean result =
					getToken().parse(primaryContext, primaryProf,
						getSubTokenString() + "|TYPE=.One");
			if (isTypeLegal())
			{
				assertFalse(result);
				assertTrue(primaryGraph.isEmpty());
			}
			else
			{
				assertTrue(result);
				assertFalse(primaryContext.ref.validate());
			}
		}
		catch (IllegalArgumentException e)
		{
			if (isTypeLegal())
			{
				// Should have returned false;
				throw e;
			}
		}
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1,"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|,TestWP1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidZeroCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|0|TestWP1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNegativeCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|-4|TestWP1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP2,,TestWP1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputCheckMult() throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1,TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputCheckTypeEqualLength()
		throws PersistenceLayerException
	{
		try
		{
			// Explicitly do NOT build TestWP2 (this checks that the TYPE=
			// doesn't
			// consume the ,
			construct(primaryContext, "TestWP1");
			assertTrue(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + "|TestWP1,TYPE=TestType,TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
		catch (IllegalArgumentException e)
		{
			if (isTypeLegal())
			{
				throw e;
			}
		}
	}

	@Test
	public void testInvalidInputCheckTypeDotLength()
		throws PersistenceLayerException
	{
		try
		{
			// Explicitly do NOT build TestWP2 (this checks that the TYPE=
			// doesn't
			// consume the ,
			construct(primaryContext, "TestWP1");
			assertTrue(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString()
					+ "|TestWP1,TYPE.TestType.OtherTestType,TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
		catch (IllegalArgumentException e)
		{
			if (isTypeLegal())
			{
				throw e;
			}
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1,TestWP2"));
		assertTrue(primaryContext.ref.validate());
		if (isTypeLegal())
		{
			assertTrue(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + "|TYPE=TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + "|TYPE.TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + "|TestWP1,TestWP2,TYPE=TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString()
					+ "|TestWP1,TestWP2,TYPE=TestType.OtherTestType"));
		}
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|2|TestWP1,TestWP2"));
		assertTrue(primaryContext.ref.validate());
		if (isTypeLegal())
		{
			assertTrue(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + "|2|TYPE=TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + "|6|TYPE.TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + "|8|TestWP1,TestWP2,TYPE=TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString()
					+ "|3|TestWP1,TestWP2,TYPE=TestType.OtherTestType"));
			assertTrue(primaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString() + "|TestWP1");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenString() + "|TestWP1,TestWP2,TestWP3");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinWithEqualType() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenString()
				+ "|TestWP1,TestWP2,TYPE=OtherTestType,TYPE=TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinTestEquals() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenString() + "|TYPE=TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenString()
				+ "|TYPE=TestAltType.TestThirdType.TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinTwoCountThree() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenString() + "|2|TestWP1,TestWP2,TestWP3");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinCountThreeWithEqualType()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenString()
				+ "|3|TestWP1,TestWP2,TYPE=OtherTestType,TYPE=TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinCountTwoTestEquals()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenString() + "|2|TYPE=TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinCountFourTestEqualThree()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenString()
				+ "|4|TYPE=TestAltType.TestThirdType.TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(getSubTokenType(), one);
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputAll() throws PersistenceLayerException
	{
		if (!isAllLegal())
		{
			try
			{
				boolean parse =
						getToken().parse(primaryContext, primaryProf, "ANY");
				if (parse)
				{
					// Only need to check if parsed as true
					assertFalse(primaryContext.ref.validate());
				}
			}
			catch (IllegalArgumentException e)
			{
				// This is okay too
			}
			assertTrue(primaryGraph.isEmpty());
		}
	}

	// TODO This really need to check the object is also not modified, not just
	// that the graph is empty (same with other tests here)
	@Test
	public void testInvalidInputAnyItem() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"ANY" + getJoinCharacter() + "TestWP1"));
			assertTrue(primaryGraph.isEmpty());
		}
	}

	@Test
	public void testInvalidInputItemAny() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"TestWP1" + getJoinCharacter() + "ANY"));
			assertTrue(primaryGraph.isEmpty());
		}
	}

	@Test
	public void testInvalidInputAnyType() throws PersistenceLayerException
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"ANY" + getJoinCharacter() + "TYPE=TestType"));
			assertTrue(primaryGraph.isEmpty());
		}
	}

	@Test
	public void testInvalidInputTypeAny() throws PersistenceLayerException
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"TYPE=TestType" + getJoinCharacter() + "ANY"));
			assertTrue(primaryGraph.isEmpty());
		}
	}

	@Test
	public void testInputInvalidAddsTypeNoSideEffect()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP3");
			assertTrue(getToken().parse(primaryContext, primaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertTrue(getToken().parse(secondaryContext, secondaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertEquals("Test setup failed", primaryGraph, secondaryGraph);
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"TestWP3" + getJoinCharacter() + "TYPE="));
			assertEquals("Bad Add had Side Effects", primaryGraph,
				secondaryGraph);
		}
	}

	@Test
	public void testInputInvalidAddsBasicNoSideEffect()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP3");
		construct(primaryContext, "TestWP4");
		construct(secondaryContext, "TestWP4");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"TestWP1" + getJoinCharacter() + "TestWP2"));
		assertTrue(getToken().parse(secondaryContext, secondaryProf,
			"TestWP1" + getJoinCharacter() + "TestWP2"));
		assertEquals("Test setup failed", primaryGraph, secondaryGraph);
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP3" + getJoinCharacter() + getJoinCharacter() + "TestWP4"));
		assertEquals("Bad Add had Side Effects", primaryGraph, secondaryGraph);
	}

	@Test
	public void testInputInvalidAddsAllNoSideEffect()
		throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP3");
			assertTrue(getToken().parse(primaryContext, primaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertTrue(getToken().parse(secondaryContext, secondaryProf,
				"TestWP1" + getJoinCharacter() + "TestWP2"));
			assertEquals("Test setup failed", primaryGraph, secondaryGraph);
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"TestWP3" + getJoinCharacter() + "ANY"));
			assertEquals("Bad Add had Side Effects", primaryGraph,
				secondaryGraph);
		}
	}
}
