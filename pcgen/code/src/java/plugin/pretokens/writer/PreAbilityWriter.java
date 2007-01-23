/*
 * PrerequisiteFeatWriter.java
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision: 1821 $
 *
 * Last Editor: $Author: jdempsey $
 *
 * Last Edited: $Date: 2006-12-28 17:12:38 +1100 (Thu, 28 Dec 2006) $
 *
 */
package plugin.pretokens.writer;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.AbstractPrerequisiteWriter;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;

import java.io.IOException;
import java.io.Writer;

/**
 * <code>PreAbilityWriter</code> outputs ability prereqs.
 *
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 15:36:01 +1100 (Sun, 17 Dec 2006) $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1777 $
 */
public class PreAbilityWriter extends AbstractPrerequisiteWriter implements
		PrerequisiteWriterInterface
{

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#kindHandled()
	 */
	public String kindHandled()
	{
		return "ability";
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#operatorsHandled()
	 */
	public PrerequisiteOperator[] operatorsHandled()
	{
		return new PrerequisiteOperator[]{PrerequisiteOperator.GTEQ,
			PrerequisiteOperator.LT};
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#write(java.io.Writer, pcgen.core.prereq.Prerequisite)
	 */
	public void write(Writer writer, Prerequisite prereq)
		throws PersistenceLayerException
	{
		checkValidOperator(prereq, operatorsHandled());
		try
		{
			if (prereq.getOperator().equals(PrerequisiteOperator.LT))
			{
				writer.write('!');
			}
			writer.write("PREABILITY:" + (prereq.isOverrideQualify() ? "Q:":""));
			writer.write(prereq.getOperand());
			writer.write(',');
			if (prereq.isCountMultiples())
			{
				writer.write("CHECKMULT,");
			}
			if (prereq.getCategoryName().length() >0)
			{
				writer.write("CATEGORY." + prereq.getCategoryName() + ",");
			}
			writer.write(prereq.getKey());
			if (prereq.getSubKey() != null)
			{
				writer.write(" (");
				writer.write(prereq.getSubKey());
				writer.write(")");
			}
		}
		catch (IOException e)
		{
			throw new PersistenceLayerException(e.getMessage());
		}
	}

}
