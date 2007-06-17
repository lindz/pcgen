/*
 * Copyright 2006 (C) Tom Parker <thpr@sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on October 29, 2006.
 * 
 * Current Ver: $Revision: 1111 $ Last Editor: $Author: boomer70 $ Last Edited:
 * $Date: 2006-06-22 21:22:44 -0400 (Thu, 22 Jun 2006) $
 */
package pcgen.cdom.choice;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.AssociatedObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.core.CDOMListObject;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public class PCListChooser<T extends PObject> extends AbstractChooser<T>
{

	private HashMapToList<AssociationKey<?>, Object> assoc;

	private Class<? extends CDOMListObject<T>> listClass;

	public PCListChooser(Class<? extends CDOMListObject<T>> cl)
	{
		super();
		if (cl == null)
		{
			throw new IllegalArgumentException("Choice Class cannot be null");
		}
		listClass = cl;
	}

	public Set<T> getSet(PlayerCharacter pc)
	{
		Set<? extends CDOMListObject<T>> lists = pc.getCDOMLists(listClass);
		Set<T> set = new HashSet<T>();
		for (CDOMListObject<T> listObj : lists)
		{
			Collection<T> listContents = pc.getCDOMListContents(listObj);
			if (assoc == null)
			{
				set.addAll(listContents);
			}
			else
			{
				for (T obj : listContents)
				{
					AssociatedObject objAssoc =
							pc.getCDOMListAssociation(listObj, obj);
					for (AssociationKey<?> ak : assoc.getKeySet())
					{
						Object thisAssoc = objAssoc.getAssociation(ak);
						for (Object requiredAssoc : assoc.getListFor(ak))
						{
							if (requiredAssoc == null)
							{
								if (thisAssoc == null)
								{
									set.add(obj);
								}
							}
							else if (requiredAssoc.equals(thisAssoc))
							{
								set.add(obj);
							}
						}
					}
				}
			}
		}
		return set;
	}

	@Override
	public String toString()
	{
		return getCount().toString() + '<' + getMaxSelections().toString()
			+ Constants.PIPE + "List: " + listClass;
	}

	@Override
	public int hashCode()
	{
		return chooserHashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof PCListChooser))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		PCListChooser<?> cs = (PCListChooser) o;
		return equalsAbstractChooser(cs) && listClass.equals(cs.listClass);
	}

	public <A> void setAssociation(AssociationKey<A> ak, A val)
	{
		if (assoc == null)
		{
			assoc = new HashMapToList<AssociationKey<?>, Object>();
		}
		assoc.addToListFor(ak, val);
	}

	public String getLSTformat()
	{
		if (assoc == null || assoc.size() != 1)
		{
			return "LIST";
		}
		else
		{
			return "LIST="
				+ StringUtil.join(assoc.getListFor(assoc.getKeySet().iterator()
					.next()), ",");
		}
	}
}
