/*
 * FilterList.java
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
 * Created on Jun 18, 2008, 8:36:09 PM
 */
package pcgen.gui.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilterList
{

    private List<FilterListListener> listeners;
    private List<Filter> filters;

    public FilterList()
    {
        this.listeners = new ArrayList<FilterListListener>();
        this.filters = Collections.emptyList();
    }

    public void setFilters(List<Filter> filters)
    {
        FilterListEvent event = new FilterListEvent(this, this.filters, filters);
        this.filters = filters;
        fireFiltersChanged(event);
    }

    private void fireFiltersChanged(FilterListEvent event)
    {
        for (FilterListListener listener : listeners)
        {
            listener.filtersChanged(event);
        }
    }

    public void addFilterListListener(FilterListListener listener)
    {
        listeners.add(listener);
    }

    public void removeFilterListListener(FilterListListener listener)
    {
        listeners.remove(listener);
    }

    public List<Filter> getFilters()
    {
        return filters;
    }

}
