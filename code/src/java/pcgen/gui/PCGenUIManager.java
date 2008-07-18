/*
 * PCGenUIManager.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jul 14, 2008, 8:43:48 PM
 */
package pcgen.gui;

import pcgen.gui.facade.AbilityCatagoryFacade;
import pcgen.gui.facade.AbilityFacade;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.filter.NamedFilter;
import pcgen.gui.util.DefaultGenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class PCGenUIManager
{

    private PCGenUIManager()
    {
    }

    public static <T> DefaultGenericListModel<NamedFilter<? super T>> getRegisteredFilters(Class<T> c)
    {
        return null;
    }

    public static DefaultGenericListModel<AbilityCatagoryFacade> getRegisteredAbilityCatagories(CharacterFacade character)
    {
        return null;
    }

    public static DefaultGenericListModel<AbilityFacade> getRegisteredAbilities(CharacterFacade character,
                                                         AbilityCatagoryFacade catagory)
    {
        return null;
    }

}
