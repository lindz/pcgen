/*
 * Race.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * <code>Race</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @author Michael Osterlie
 * @version $Revision$
 */
public final class Race extends PObject
{
	private ArrayList languageBonus = new ArrayList();
	private ArrayList monCCSkillList = null;
	private ArrayList monCSkillList = null;
	private ArrayList weaponProfBonus = new ArrayList();
	private ArrayList weaponProfs = new ArrayList();
	private HashMap hitPointMap = new HashMap();
	private Integer initMod = new Integer(0);
	private Integer naturalAC = new Integer(0);
	private Integer startingAC = new Integer(10);
	private String hitDieLock = "";
	private String ageString = "";
	private String bonusSkillList = "";
	private String chooseLanguageAutos = "";
	private String displayName = "None";

	//private String face = "5 ft. by 5 ft.";
	private Point2D.Double face = new Point2D.Double(5, 0);
	private String favoredClass = "";
	private String featList = "";
	private String heightString = "";
	private String levelAdjustment = "0"; //now a string so that we can handle formulae
	private String mFeatList = "";
	private String monsterClass = null;
	private String size = "";
	private String vFeatList = "";
	private String weightString = "";

	//private String type = "Humanoid";
	private int[] hitDiceAdvancement;
	private boolean unlimitedAdvancement = false;
	private int BAB = 0;
	private int CR = 0;
	private int bonusInitialFeats = 0;
	private int bonusSkillsPerLevel = 0;
	private int hands = 2;
	private int hitDice = 0;
	private int hitDiceSize = 0;
	private int initialSkillMultiplier = 4;
	private int langNum = 0;
	private int legs = 2;
	private int monsterClassLevels = 0;
	private int reach = 5;
	private String raceType = "None";
	private ArrayList racialSubTypes = new ArrayList();

	{
		vision = new HashMap();
	}

	public void setAdvancementUnlimited(final boolean unlimitedAdvancement)
	{
		this.unlimitedAdvancement = unlimitedAdvancement;
	}

	public boolean isAdvancementUnlimited()
	{
		return unlimitedAdvancement;
	}

	public void setAgeString(final String aString)
	{
		ageString = aString;
	}

	public void setBAB(final int newBAB)
	{
		BAB = newBAB;
	}

	public void setBonusInitialFeats(final int i)
	{
		bonusInitialFeats = i;
	}

	public int getBonusInitialFeats()
	{
		return bonusInitialFeats;
	}

	public void setBonusSkillList(final String aString)
	{
		bonusSkillList = aString;
	}

	public void setBonusSkillsPerLevel(final int i)
	{
		bonusSkillsPerLevel = i;
	}

	public int getBonusSkillsPerLevel()
	{
		return bonusSkillsPerLevel;
	}

	public void setCR(final int newCR)
	{
		CR = newCR;
	}

	public int getCR()
	{
		return CR;
	}

	public void setChooseLanguageAutos(final String chooseLanguageAutos)
	{
		this.chooseLanguageAutos = chooseLanguageAutos;
	}

	public String getChooseLanguageAutos()
	{
		return this.chooseLanguageAutos;
	}

	public void setDisplayName(final String displayName)
	{
		this.displayName = displayName;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public String getDisplayVision(final PlayerCharacter aPC)
	{
		if (vision == null)
		{
			return "";
		}

		if (aPC == null)
		{
			return "";
		}

		final StringBuffer vis = new StringBuffer(25);

		for (Iterator i = vision.keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();
			final String aVal = vision.get(aKey).toString();
			final int val = aPC.getVariableValue(aVal, "").intValue();

			if (vis.length() > 0)
			{
				vis.append(';');
			}

			vis.append(aKey);

			if (val != 0)
			{
				vis.append(" (").append(val).append("')");
			}
		}

		return vis.toString();
	}

	public void setFace(final double width, final double height)
	{
		face = new Point2D.Double(width, height);
	}

	public Point2D.Double getFace()
	{
		return face;
	}

	public void setFavoredClass(final String newClass)
	{
		favoredClass = newClass;
	}

	/*
	   public void setVFeatList(String vFeatList)
	   {
		   this.vFeatList = vFeatList;
	   }
	 */
	public String getFavoredClass()
	{
		return favoredClass;
	}

	public void setFeatList(final String featList)
	{
		this.featList = featList;
	}

	public String getFeatList(final PlayerCharacter aPC)
	{
		return getFeatList(aPC, true);
	}

	public String getFeatList(final PlayerCharacter aPC, final boolean checkPC)
	{
		// This was messing up feats by race for several PC races.
		// so a new tag MFEAT has been added.
		// --- arcady 1/18/2002

		if (checkPC && (aPC!=null) && aPC.isMonsterDefault() && !"".equals(mFeatList))
		{
			return featList + "|" + mFeatList;
		}
		else if (!checkPC || (aPC != null))
		{
			return featList;
		}
		else
		{
			return "";
		}
	}

	public void setHands(final int newHands)
	{
		hands = newHands;
	}

	/**
	 * Made public for use on equipping tab -- bug 586332
	 * sage_sam, 22 Nov 2002
	 * @return hands
	 */
	public int getHands()
	{
		return hands;
	}

	public void setHeightString(final String aString)
	{
		heightString = aString;
	}

	public void setHitDice(final int newHitDice)
	{
		if (newHitDice < 0)
		{
			ShowMessageDelegate.showMessageDialog("Invalid number of hit dice in race " + name, "PCGen", MessageType.ERROR);

			return;
		}

		hitDice = newHitDice;
	}

	public void setHitDiceAdvancement(final int[] advancement)
	{
		hitDiceAdvancement = advancement;
	}

//	public int[] getHitDiceAdvancement()
//	{
//		return hitDiceAdvancement;
//	}
	public int getHitDiceAdvancement(final int index)
	{
		return hitDiceAdvancement[index];
	}

	public void setHitDiceSize(final int newHitDiceSize)
	{
		hitDiceSize = newHitDiceSize;
	}

	public int getHitDiceSize(final PlayerCharacter aPC)
	{
		return getHitDiceSize(aPC, true);
	}

	public int getHitDiceSize(final PlayerCharacter aPC, final boolean checkPC)
	{
		if (!checkPC || ((aPC!=null) && aPC.isMonsterDefault()))
		{
			return hitDiceSize;
		}
		return 0;
	}

	public void setHitDieLock(final String hitDieLock)
	{
		this.hitDieLock = hitDieLock;
	}

	public void setHitPoint(final int aLevel, final Integer iRoll)
	{
		hitPointMap.put(Integer.toString(aLevel), iRoll);
	}

	public Integer getHitPoint(final int j)
	{
		final Integer aHP = (Integer) hitPointMap.get(Integer.toString(j));

		if (aHP == null)
		{
			return new Integer(0);
		}

		return aHP;
	}

	public void setHitPointMap(final HashMap newMap)
	{
		hitPointMap.clear();
		hitPointMap.putAll(newMap);
	}

	public int getHitPointMapSize()
	{
		return hitPointMap.size();
	}

	public void setInitMod(final Integer initMod)
	{
		this.initMod = initMod;
	}

	public void setInitialSkillMultiplier(final int initialSkillMultiplier)
	{
		this.initialSkillMultiplier = initialSkillMultiplier;
	}

	public int getInitialSkillMultiplier()
	{
		return initialSkillMultiplier;
	}

	public void setLangNum(final int langNum)
	{
		this.langNum = langNum;
	}

	public void setLanguageBonus(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);

		while (aTok.hasMoreTokens())
		{
			final String token = aTok.nextToken();

			if (".CLEAR".equals(token))
			{
				getLanguageBonus().clear();
			}
			else
			{
				final Language aLang = Globals.getLanguageNamed(token);

				if (aLang != null)
				{
					getLanguageBonus().add(aLang);
				}
			}
		}
	}

	public ArrayList getLanguageBonus()
	{
		return languageBonus;
	}

	public void setLegs(final int argLegs)
	{
		legs = argLegs;
	}

	public int getLegs()
	{
		return legs;
	}

	public void setLevelAdjustment(final String newLevelAdjustment)
	{
		levelAdjustment = newLevelAdjustment;
	}

	public int getLevelAdjustment(final PlayerCharacter aPC)
	{
		int lvlAdjust;

		//if there's a current PC, go ahead and evaluate the formula
		if (aPC != null)
		{
			return aPC.getVariableValue(levelAdjustment, "").intValue();
		}

		//otherwise do what we can
		try
		{
			//try to convert the string to an int to return
			lvlAdjust = Integer.parseInt(levelAdjustment);
		}
		catch (NumberFormatException nfe)
		{
			//if the parseInt failed then just punt... return 0
			lvlAdjust = 0;
		}

		return lvlAdjust;
	}

	public String getLevelAdjustmentFormula()
	{
		return levelAdjustment;
	}

	public void setMFeatList(final String mFeatList)
	{
		this.mFeatList = mFeatList;
	}

	public String getMFeatList()
	{
		return mFeatList;
	}

	public void setMonCCSkillList(final String aString)
	{
		if (monCCSkillList == null)
		{
			monCCSkillList = new ArrayList();
		}

		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			if (".CLEAR".equals(bString))
			{
				monCCSkillList.clear();
			}
			else if (bString.startsWith("TYPE.") || bString.startsWith("TYPE="))
			{
				Skill aSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();

					if (aSkill.isType(bString.substring(5)))
					{
						monCCSkillList.add(aSkill.getName());
					}
				}
			}
			else
			{
				monCCSkillList.add(bString);
			}
		}
	}

	public void setMonCSkillList(final String aString)
	{
		if (monCSkillList == null)
		{
			monCSkillList = new ArrayList();
		}

		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			if (".CLEAR".equals(bString))
			{
				monCSkillList.clear();
			}
			else if (bString.startsWith("TYPE.") || bString.startsWith("TYPE="))
			{
				Skill aSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();

					if (aSkill.isType(bString.substring(5)))
					{
						monCSkillList.add(aSkill.getName());
					}
				}
			}
			else
			{
				monCSkillList.add(bString);
			}
		}
	}

	public void setMonsterClass(final String string)
	{
		monsterClass = string;
	}

	public String getMonsterClass(final PlayerCharacter aPC, final boolean checkPC)
	{
		if (!checkPC || ((aPC != null) && !aPC.isMonsterDefault()))
		{
			return monsterClass;
		}
		return null;
	}

	public void setMonsterClassLevels(final int num)
	{
		monsterClassLevels = num;
	}

	public int getMonsterClassLevels(final PlayerCharacter aPC)
	{
		return getMonsterClassLevels(aPC, true);
	}

	public int getMonsterClassLevels(final PlayerCharacter aPC, final boolean checkPC)
	{
		if (!checkPC || ((aPC!= null) && !aPC.isMonsterDefault()))
		{
			return monsterClassLevels;
		}
		return 0;
	}

	public boolean isNonAbility(final int statIdx)
	{
		final List statList = SettingsHandler.getGame().getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return true;
		}

		final String aStat = "|LOCK." + ((PCStat) statList.get(statIdx)).getAbb() + "|10";

		for (int i = 0, x = getVariableCount(); i < x; ++i)
		{
			final String varString = getVariableDefinition(i);

			if (varString.endsWith(aStat))
			{
				return true;
			}
		}

		return false;
	}

	public int getNumberOfHitDiceAdvancements()
	{
		return (hitDiceAdvancement != null) ? hitDiceAdvancement.length : 0;
	}

	/**
	 * Retrieve Unarmed Damage according to the Race
	 * @return UDAM damage die (ie 1d3)
	 */
	public String getUdam()
	{
		final int iSize = Globals.sizeInt(getSize());
		final SizeAdjustment defAdj = SettingsHandler.getGame().getDefaultSizeAdjustment();
		final SizeAdjustment sizAdj = SettingsHandler.getGame().getSizeAdjustmentAtIndex(iSize);
		if ((defAdj != null) && (sizAdj != null))
		{
			return Globals.adjustDamage("1d3", defAdj.getAbbreviation(), sizAdj.getAbbreviation());
		}
		return "1d3";
	}

	public String getRaceType()
	{
		return raceType;
	}

	public void setRaceType(final String aType)
	{
		raceType = aType;
	}

	public void addRacialSubType(final String aSubType)
	{
		racialSubTypes.add(aSubType);
	}

	public boolean removeRacialSubType(final String aSubType)
	{
		return racialSubTypes.remove(aSubType);
	}

	public List getRacialSubTypes()
	{
		return Collections.unmodifiableList(racialSubTypes);
	}

	/**
	 * Produce a tailored PCC output, used for saving custom races.
	 * @return PCC Text
	 */
	public String getPCCText()
	{
		// 29 July 2003 : sage_sam corrected order
		final StringBuffer txt = new StringBuffer(super.getPCCText());

		if ((favoredClass != null) && (favoredClass.length() > 0))
		{
			txt.append("\tFAVCLASS:").append(favoredClass);
		}

		if (bonusInitialFeats != 0)
		{
			txt.append("\tSTARTFEATS:").append(bonusInitialFeats);
		}

		if ((size != null) && (size.length() > 0))
		{
			txt.append("\tSIZE:").append(size);
		}

		if (reach != 5)
		{
			txt.append("\tREACH:").append(reach);
		}

		if ((chooseLanguageAutos != null) && (chooseLanguageAutos.length() > 0))
		{
			txt.append("\tCHOOSE:LANGAUTO:").append(chooseLanguageAutos);
		}

		if ((languageBonus != null) && !languageBonus.isEmpty())
		{
			final StringBuffer buffer = new StringBuffer();

			for (Iterator e = languageBonus.iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append(',');
				}

				buffer.append(e.next().toString());
			}

			txt.append("\tLANGBONUS:").append(buffer.toString());
		}

		if ((weaponProfBonus != null) && (weaponProfBonus.size() > 0))
		{
			final StringBuffer buffer = new StringBuffer();

			for (Iterator e = weaponProfBonus.iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}

				buffer.append((String) e.next());
			}

			txt.append("\tWEAPONBONUS:").append(buffer.toString());
		}

		if ((mFeatList != null) && (mFeatList.length() > 0))
		{
			txt.append("\tMFEAT:").append(mFeatList);
		}

		if (legs != 2)
		{
			txt.append("\tLEGS:").append(legs);
		}

		if (hands != 2)
		{
			txt.append("\tHANDS:").append(hands);
		}

		if ((getNaturalWeapons() != null) && (getNaturalWeapons().size() > 0))
		{
			final StringBuffer buffer = new StringBuffer();

			for (Iterator e = getNaturalWeapons().iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}

				final Equipment natEquip = (Equipment) e.next();
				String eqName = natEquip.getName();
				int index = eqName.indexOf(" (Natural/Primary)");

				if (index >= 0)
				{
					eqName = eqName.substring(0, index) + eqName.substring(index + " (Natural/Primary)".length());
				}

				index = eqName.indexOf(" (Natural/Secondary)");

				if (index >= 0)
				{
					eqName = eqName.substring(0, index) + eqName.substring(index + " (Natural/Secondary)".length());
				}

				buffer.append(eqName).append(',');
				buffer.append(natEquip.getType(false)).append(',');

				if (!natEquip.isAttacksProgress())
				{
					buffer.append('*');
				}

				buffer.append((int) natEquip.bonusTo(null, "WEAPON", "ATTACKS", true) + 1).append(',');
				buffer.append(natEquip.getDamage(null));
			}

			txt.append("\tNATURALATTACKS:").append(buffer.toString());
		}

		if (initialSkillMultiplier != 4)
		{
			txt.append("\tSKILLMULT:").append(initialSkillMultiplier);
		}

		if (monsterClass != null)
		{
			txt.append("\tMONSTERCLASS:").append(monsterClass);
			txt.append(':').append(monsterClassLevels);
		}

		List templates = getTemplateList();
		if ((templates != null) && (templates.size() > 0))
		{
			for (Iterator e = templates.iterator(); e.hasNext();)
			{
				txt.append("\tTEMPLATE:").append((String) e.next());
			}
		}

		if ((hitDiceAdvancement != null) && (hitDiceAdvancement.length > 0))
		{
			txt.append("\tHITDICEADVANCEMENT:");

			for (int index = 0; index < hitDiceAdvancement.length; index++)
			{
				if (index > 0)
				{
					txt.append(',');
				}

				if ((hitDiceAdvancement[index] == -1) && isAdvancementUnlimited())
				{
					txt.append('*');
				}
				else
				{
					txt.append(hitDiceAdvancement[index]);
				}
			}
		}

		if (CR != 0)
		{
			txt.append("\tCR:");

			if (CR < 0)
			{
				txt.append("1/").append(-CR);
			}
			else
			{
				txt.append(CR);
			}
		}

		if (startingAC.intValue() != 10)
		{
			txt.append("\tAC:").append(startingAC.toString());
		}

/*
   if (ageString != null && !Constants.s_NONE.equals(ageString) && ageString.length() > 0)
   {
	   txt.append("\tAGE:").append(ageString);
   }
   if (BAB != 0)
   {
	   txt.append("\tBAB:").append(BAB);
   }
 */
		if(CoreUtility.doublesEqual(face.getY(), 0.0))
		{
			txt.append("\tFACE:").append( face.getX() + " ft.");
		}
		else
		{
			txt.append("\tFACE:").append( face.getX() + " ft. by " + face.getY() + " ft.");
		}

		if ((featList != null) && (featList.length() > 0))
		{
			txt.append("\tFEAT:").append(featList);
		}

		if ((hitDice != 0) || (hitDiceSize != 0))
		{
			txt.append("\tHITDICE:").append(hitDice).append(',').append(hitDiceSize);
		}

		if (initMod.intValue() != 0)
		{
			txt.append("\tINIT:").append(initMod.toString());
		}

		if (langNum != 0)
		{
			txt.append("\tLANGNUM:").append(langNum);
		}

		if (!"0".equals(levelAdjustment))
		{
			txt.append("\tLEVELADJUSTMENT:").append(levelAdjustment);
		}

		if ((weaponProfs != null) && (weaponProfs.size() > 0))
		{
			final StringBuffer buffer = new StringBuffer();

			for (Iterator e = weaponProfs.iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}

				buffer.append((String) e.next());
			}

			txt.append("\tPROF:").append(buffer.toString());
		}

		if (!"alwaysValid".equals(getQualifyString()))
		{
			txt.append("\tQUALIFY:").append(getQualifyString());
		}

		if (!Constants.s_NONE.equals(displayName))
		{
			txt.append("\tRACENAME:").append(displayName);
		}

		if ((bonusSkillList != null) && (bonusSkillList.length() > 0))
		{
			txt.append("\tSKILL:").append(bonusSkillList);
		}

		if ((vFeatList != null) && (vFeatList.length() > 0))
		{
			txt.append("\tVFEAT:").append(vFeatList);
		}

		if (bonusSkillsPerLevel != 0)
		{
			txt.append("\tXTRASKILLPTSPERLVL:").append(bonusSkillsPerLevel);
		}

//		txt.append(super.getPCCText(false));
		return txt.toString();
	}

	public void setReach(final int newReach)
	{
		reach = newReach;
	}

	public int getReach()
	{
		return reach;
	}

	public void setSize(final String argSize)
	{
		this.size = argSize;
	}

	public String getSize()
	{
		return size;
	}

	public void setStartingAC(final Integer anInt)
	{
		startingAC = anInt;
	}

	public void setVisionTable(final Map visionTable)
	{
		vision = visionTable;
	}

	public void setWeaponProfBonus(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");

		while (aTok.hasMoreTokens())
		{
			getWeaponProfBonus().add(aTok.nextToken());
		}
	}

	public ArrayList getWeaponProfBonus()
	{
		return weaponProfBonus;
	}

	public void setWeaponProfs(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");
		final String typeString = aTok.nextToken();
		final String prefix = typeString + "|";

		while (aTok.hasMoreTokens())
		{
			weaponProfs.add(prefix + aTok.nextToken());
		}
	}

	public Object clone()
	{
		Race aRace = null;

		try
		{
			aRace = (Race) super.clone();
			aRace.favoredClass = favoredClass;
			aRace.bonusSkillsPerLevel = bonusSkillsPerLevel;
			aRace.bonusInitialFeats = bonusInitialFeats;
			aRace.size = size;

			aRace.bonusSkillList = bonusSkillList;
			aRace.ageString = ageString;
			aRace.heightString = heightString;
			aRace.weightString = weightString;
			aRace.languageBonus = (ArrayList) languageBonus.clone();
			aRace.weaponProfBonus = (ArrayList) weaponProfBonus.clone();
			aRace.featList = featList;
			aRace.vFeatList = vFeatList;
			aRace.startingAC = new Integer(startingAC.intValue());
			aRace.naturalAC = new Integer(naturalAC.intValue());
			aRace.initMod = new Integer(initMod.intValue());
			aRace.langNum = langNum;
			aRace.initialSkillMultiplier = initialSkillMultiplier;
			aRace.levelAdjustment = levelAdjustment;
			aRace.CR = CR;
			aRace.BAB = BAB;
			aRace.hitDice = hitDice;
			aRace.hitDiceSize = hitDiceSize;
			aRace.hitPointMap = new HashMap(hitPointMap);
			aRace.hitDiceAdvancement = hitDiceAdvancement;
			aRace.hands = hands;
			aRace.reach = reach;
			aRace.face = face;
			aRace.weaponProfs = (ArrayList) weaponProfs.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		return aRace;
	}

	/**
	 * returns true if the race has HD advancement
	 * @return true if the race has HD advancement
	 */
	public boolean hasAdvancement()
	{
		return hitDiceAdvancement != null;
	}

	/**
	 * Overridden to only consider the race's name.
	 * @return hash code
	 */
	public int hashCode()
	{
		return getName().hashCode();
	}

	public int hitDice(final PlayerCharacter aPC)
	{
		return hitDice(aPC, true);
	}

	public int hitDice(final PlayerCharacter aPC, final boolean checkPC)
	{
		if (!checkPC || ((aPC != null) && aPC.isMonsterDefault()))
		{
			return hitDice;
		}
		return 0;
	}

	/**
	 * TODO: Note that this code does *not* work like that in PCClass
	 * Does it need to be?
	 * @param aPC
	 **/
	public void rollHP(final PlayerCharacter aPC)
	{
		if (!aPC.isImporting())
		{
			final int min = 1 + (int) aPC.getTotalBonusTo("HD", "MIN");
			final int max = hitDiceSize + (int) aPC.getTotalBonusTo("HD", "MAX");

			for (int x = 0; x < hitDice; ++x)
			{
				setHitPoint(x, new Integer(Globals.rollHP(min, max, getName(), x + 1)));
			}
		}

		aPC.setCurrentHP(aPC.hitPoints());
	}

	protected int getSR(final PlayerCharacter aPC)
	{
		int intSR;

		//if there's a current PC, go ahead and evaluate the formula
		if ((getSRFormula() != null) && (aPC != null))
		{
			return aPC.getVariableValue(getSRFormula(), "").intValue();
		}

		//otherwise do what we can
		try
		{
			//try to convert the string to an int to return
			intSR = Integer.parseInt(getSRFormula());
		}
		catch (NumberFormatException nfe)
		{
			//if the parseInt failed then just punt... return 0
			intSR = 0;
		}

		return intSR;
	}

	protected void doGlobalTypeUpdate(final String aString)
	{
		Globals.getRaceTypes().add(aString);
	}

	int getBAB(final PlayerCharacter aPC)
	{
		if ((aPC != null) && aPC.isMonsterDefault())
		{
			// "BAB" not being used on races any more; instead using a BONUS tag.
			// This will fix a bug this causes for default monsters.  Bug #647163
			// sage_sam 03 Dec 2002
			if (BAB == 0)
			{
				BAB = (int) bonusTo("COMBAT", "BAB", aPC, aPC);
			}

			return BAB;
		}
		return 0;
	}

	String getHitDieLock()
	{
		return hitDieLock;
	}

	int getLangNum()
	{
		return langNum;
	}

	String getMonsterClass(final PlayerCharacter aPC)
	{
		return getMonsterClass(aPC, true);
	}

	ArrayList getWeaponProfs()
	{
		return weaponProfs;
	}

	int bonusForSkill(final String skillName)
	{
		if (getBonusSkillList().length() == 0)
		{
			return 0;
		}

		final StringTokenizer aTok = new StringTokenizer(bonusSkillList, "=");

		while (aTok.hasMoreTokens())
		{
			final String skillList = aTok.nextToken();
			final int anInt = Integer.parseInt(aTok.nextToken());
			final StringTokenizer bTok = new StringTokenizer(skillList, ",", false);

			while (bTok.hasMoreTokens())
			{
				final String aSkill = bTok.nextToken();

				if (aSkill.equals(skillName))
				{
					return anInt;
				}
			}
		}

		return 0;
	}

	int calcHitPoints(final int iConMod)
	{
		int total = 0;

		for (int i = 0; i <= hitDice; i++)
		{
			if (getHitPoint(i).intValue() > 0)
			{
				int iHp = getHitPoint(i).intValue() + iConMod;

				if (iHp < 1)
				{
					iHp = 1;
				}

				total += iHp;
			}
		}

		return total;
	}

	boolean canBeAlignment(final String aString)
	{
		if (getPreReqCount() != 0)
		{
			for (int e = 0; e < getPreReqCount(); e++)
			{
				final Prerequisite prereq = getPreReq(e);

				if ("ALIGN".equalsIgnoreCase( prereq.getKind() ))
				{
					String alignStr = aString;
					final String[] aligns = SettingsHandler.getGame().getAlignmentListStrings(false);
					try
					{
						final int align = Integer.parseInt(alignStr);
						alignStr = aligns[align];
					}
					catch (NumberFormatException ex)
					{
						// Do Nothing
					}
					String desiredAlignment = prereq.getKey();
					try
					{
						final int align = Integer.parseInt(desiredAlignment);
						desiredAlignment = aligns[align];
					}
					catch (NumberFormatException ex)
					{
						// Do Nothing
					}

					return desiredAlignment.equalsIgnoreCase(alignStr);
				}
			}
		}

		return true;
	}

	/** Adds one chosen language.
	 * TODO: Identical method in PCTemplate.java. Refactor. XXX
	 * @param flag
	 * @param aPC TODO
	 */
	void chooseLanguageAutos(final boolean flag, final PlayerCharacter aPC)
	{
		if (!flag && !"".equals(chooseLanguageAutos))
		{
			final StringTokenizer tokens = new StringTokenizer(chooseLanguageAutos, "|", false);
			final List selectedList; // selected list of choices

			final ChooserInterface c = ChooserFactory.getChooserInstance();
			c.setPool(1);
			c.setPoolFlag(false);
			c.setTitle("Pick a Language: ");

			SortedSet list = new TreeSet();

			while (tokens.hasMoreTokens())
			{
				list.add(tokens.nextToken());
			}

			list = Globals.extractLanguageListNames(list);
			c.setAvailableList(new ArrayList(list));
			c.setVisible(true);
			selectedList = c.getSelectedList();

			if ((selectedList != null) && (selectedList.size() != 0))
			{
				aPC.addFreeLanguage((String) selectedList.get(0));
			}
		}
	}

	boolean hasMonsterCCSkill(final String aName)
	{
		if ((monCCSkillList == null) || monCCSkillList.isEmpty())
		{
			return false;
		}

		if (monCCSkillList.contains(aName))
		{
			return true;
		}

		String aString;

		for (Iterator e = monCCSkillList.iterator(); e.hasNext();)
		{
			aString = (String) e.next();

			if (aString.lastIndexOf('%') >= 0)
			{
				aString = aString.substring(0, aString.length() - 1);

				if (aName.startsWith(aString))
				{
					return true;
				}
			}
		}

		return false;
	}

	boolean hasMonsterCSkill(final String aName)
	{
		if ((monCSkillList == null) || monCSkillList.isEmpty())
		{
			return false;
		}

		if (monCSkillList.contains(aName))
		{
			return true;
		}

		if (monCSkillList.contains("LIST"))
		{
			String aString;

			for (int e = 0; e < getAssociatedCount(); ++e)
			{
				aString = getAssociated(e);

				if (aName.startsWith(aString) || aString.startsWith(aName))
				{
					return true;
				}
			}
		}

		String aString;

		for (Iterator e = monCSkillList.iterator(); e.hasNext();)
		{
			aString = (String) e.next();

			if (aString.lastIndexOf('%') >= 0)
			{
				aString = aString.substring(0, aString.length() - 1);

				if (aName.startsWith(aString))
				{
					return true;
				}
			}
		}

		return false;
	}

	int maxHitDiceAdvancement()
	{
		if ((hitDiceAdvancement != null) && (hitDiceAdvancement.length >= 1))
		{
			return hitDiceAdvancement[hitDiceAdvancement.length - 1];
		}
		return 0;
	}

	int sizesAdvanced(final int HD)
	{
		if (hitDiceAdvancement != null)
		{
			for (int x = 0; x < hitDiceAdvancement.length; x++)
			{
				if ((HD <= hitDiceAdvancement[x]) || (hitDiceAdvancement[x] == -1))
				{
					return x;
				}
			}
		}

		return 0;
	}

	private String getBonusSkillList()
	{
		return bonusSkillList;
	}
}
