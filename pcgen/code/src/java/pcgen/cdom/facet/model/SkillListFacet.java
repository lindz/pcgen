/*
 * Copyright (c) Thomas Parker, 2014.
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
package pcgen.cdom.facet.model;

import pcgen.cdom.facet.base.AbstractScopeFacet;
import pcgen.cdom.facet.event.ScopeFacetChangeEvent;
import pcgen.cdom.facet.event.ScopeFacetChangeListener;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.PCClass;

/**
 * SkillListFacet stores the SkillList objects for a PCClass of a Player
 * Character
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class SkillListFacet extends AbstractScopeFacet<PCClass, ClassSkillList>
		implements ScopeFacetChangeListener<PCClass, ClassSkillList>
{

	public void dataAdded(ScopeFacetChangeEvent<PCClass, ClassSkillList> dfce)
	{
		add(dfce.getCharID(), dfce.getScope(), dfce.getCDOMObject(),
			dfce.getSource());
	}

	public void dataRemoved(ScopeFacetChangeEvent<PCClass, ClassSkillList> dfce)
	{
		remove(dfce.getCharID(), dfce.getScope(), dfce.getCDOMObject(),
			dfce.getSource());
	}
}
