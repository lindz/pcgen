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
package pcgen.cdom.reference;

import java.util.Collection;

import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.GroupingState;

/**
 * A CDOMTransparentAllRef is a CDOMReference which is intended to contain a
 * CDOMGroupRef, to which the CDOMTransparentAllRef will delegate behavior.
 * 
 * A CDOMTransparentAllRef, unlike many CDOMReference objects, can be cleared,
 * and the underlying CDOMGroupRef can be changed.
 * 
 * @see TransparentReference for a description of cases in which
 *      TransparentReferences like CDOMTransparentAllRef are typically used
 * 
 * @param <T>
 *            The Class of the underlying object contained by this
 *            CDOMTransparentAllRef
 */
public class CDOMTransparentAllRef<T extends PrereqObject> extends
		CDOMGroupRef<T> implements TransparentReference<T>
{

	/**
	 * Holds the reference to which this CDOMTransparentAllRef will delegate
	 * behavior.
	 */
	private CDOMGroupRef<T> subReference = null;

	/**
	 * Constructs a new CDOMTransparentAllRef for the given Class.
	 * 
	 * @param cl
	 *            The Class of the underlying objects contained by this
	 *            CDOMTransparentTypeRef.
	 */
	public CDOMTransparentAllRef(Class<T> cl)
	{
		super(cl, "ALL");
	}

	/**
	 * Returns true if the given Object is the Object to which this
	 * CDOMTransparentAllRef refers.
	 * 
	 * Note that the behavior of this class is undefined if the underlying
	 * CDOMGroupRef has not yet been resolved.
	 * 
	 * @param obj
	 *            The object to be tested to see if it is referred to by this
	 *            CDOMTransparentAllRef.
	 * @return true if the given Object is the Object to which this
	 *         CDOMTransparentAllRef refers; false otherwise.
	 * @throws IllegalStateException
	 *             if no underlying CDOMGroupRef has been defined.
	 */
	@Override
	public boolean contains(T obj)
	{
		if (subReference == null)
		{
			throw new IllegalStateException("Cannot ask for contains: "
					+ getReferenceClass().getName() + " Reference " + getName()
					+ " has not been resolved");
		}
		return subReference.contains(obj);
	}

	/**
	 * Returns a representation of this CDOMTransparentAllRef, suitable for
	 * storing in an LST file.
	 * 
	 * Note that this will return the identifier of the underlying reference (of
	 * the types given at construction), often the "key" in LST terminology.
	 * 
	 * @return A representation of this CDOMTransparentAllRef, suitable for
	 *         storing in an LST file.
	 * @see pcgen.cdom.base.CDOMReference#getLSTformat()
	 */
	@Override
	public String getLSTformat()
	{
		return getName();
	}

	/**
	 * Returns true if this CDOMTransparentAllRef is equal to the given Object.
	 * Equality is defined as being another CDOMTransparentAllRef object with
	 * equal Class represented by the reference. This is NOT a deep .equals, in
	 * that neither the actual contents of this CDOMTransparentAllRef nor the
	 * underlying CDOMGroupRef are tested.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDOMTransparentAllRef)
		{
			CDOMTransparentAllRef<?> ref = (CDOMTransparentAllRef<?>) o;
			return getReferenceClass().equals(ref.getReferenceClass())
					&& getName().equals(ref.getName());
		}
		return false;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this
	 * CDOMTransparentAllRef
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode() ^ getName().hashCode();
	}

	/**
	 * Throws an exception. This method may not be called because a
	 * CDOMTransparentAllRef is resolved using an underlying CDOMGroupRef.
	 * 
	 * @see Resolve(ReferenceManufacturer<T, ?>)
	 * 
	 * @param obj
	 *            ignored
	 * @throws IllegalStateException
	 *             because a CDOMTransparentTypeRef is resolved using an
	 *             underlying CDOMGroupRef.
	 */
	@Override
	public void addResolution(T obj)
	{
		throw new IllegalStateException(
				"Cannot resolve a Transparent Reference");
	}

	/**
	 * Resolves this CDOMTransparentAllRef using the given
	 * ReferenceManufacturer. The underlying CDOMGroupRef for this
	 * CDOMTransparentAllRef will be set to the "ALL" CDOMGroupRef constructed
	 * by the given ReferenceManufacturer
	 * 
	 * This method may be called more than once; each time it is called it will
	 * overwrite the existing CDOMGroupRef to which this CDOMTransparentAllRef
	 * delegates its behavior.
	 * 
	 * @throws IllegalArgumentException
	 *             if the Reference Class of the given ReferenceManufacturer is
	 *             different than the Reference Class of this
	 *             CDOMTransparentAllRef
	 * @throws NullPointerException
	 *             if the given ReferenceManufacturer is null
	 */
	public void resolve(ReferenceManufacturer<T, ?> rm)
	{
		if (rm.getReferenceClass().equals(getReferenceClass()))
		{
			subReference = rm.getAllReference();
		}
		else
		{
			throw new IllegalArgumentException("Cannot resolve a "
					+ getReferenceClass().getSimpleName() + " Reference to a "
					+ rm.getReferenceClass().getSimpleName());
		}
	}

	/**
	 * Returns a Collection containing the single Object to which this
	 * CDOMTransparentAllRef refers.
	 * 
	 * The semantics of this method are defined solely by the semantics of the
	 * underlying CDOMGroupRef. Ownership of the Collection returned by this
	 * method may or may not be transferred to the calling object (check the
	 * documentation of the underlying CDOMGroupRef).
	 * 
	 * Note that if you know this CDOMTransparentAllRef is a CDOMGroupRef, you
	 * are better off using resolvesTo() as the result will be much faster than
	 * having to extract the object out of the Collection returned by this
	 * method.
	 * 
	 * @return A Collection containing the single Object to which this
	 *         CDOMTransparentAllRef refers.
	 */
	@Override
	public Collection<T> getContainedObjects()
	{
		return subReference.getContainedObjects();
	}

	/**
	 * Returns the count of the number of objects included in the Collection of
	 * Objects to which this CDOMTransparentAllRef refers.
	 * 
	 * Note that the behavior of this class is undefined if the
	 * CDOMTransparentAllRef has not yet been resolved.
	 * 
	 * @return The count of the number of objects included in the Collection of
	 *         Objects to which this CDOMTransparentAllRef refers.
	 */
	@Override
	public int getObjectCount()
	{
		return subReference == null ? 0 : subReference.getObjectCount();
	}

	/**
	 * Returns the GroupingState for this CDOMTransparentAllRef. The
	 * GroupingState indicates how this CDOMTransparentAllRef can be combined
	 * with other PrimitiveChoiceFilters.
	 * 
	 * @return The GroupingState for this CDOMTransparentAllRef.
	 */
	public GroupingState getGroupingState()
	{
		return GroupingState.ALLOWS_NONE;
	}
}
