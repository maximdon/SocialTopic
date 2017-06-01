package com.softlib.imatch.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultGroup {
	private String id;
	private String title;
	private String tabOrder;
	private String comboOrder;
	private Map<String, String> columns = new HashMap<String, String>();
	private Collection<SearchResultItem> items = new ArrayList<SearchResultItem>();
	
	public SearchResultGroup(String id, String title, String tabOrder, String comboOrder)
	{
		this.setId(id);
		this.setTitle(title);
		this.setTabOrder(tabOrder);
		this.setComboOrder(comboOrder);
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	
	public void setTabOrder(String tabOrder) {
		this.tabOrder = tabOrder;
	}

	public String getTabOrder() {
		return tabOrder;
	}

	public void setComboOrder(String comboOrder) {
		this.comboOrder = comboOrder;
	}

	public String getComboOrder() {
		return comboOrder;
	}

	public void setColumns(Map<String, String> columns) {
		this.columns = columns;
	}
	public Map<String, String> getColumns() {
		return columns;
	}
	public List<String> getColumnsKeys() { 
		return new ArrayList<String>(this.columns.keySet()); 
	}
	
	public List<String> getColumnsValues() {
		return new ArrayList<String>(this.columns.values()); 
	} 
	
	public void setItems(Collection<SearchResultItem> items) {
		this.items = items;
	}
	public Collection<SearchResultItem> getItems() {
		return items;
	}
	
	public static Comparator<SearchResultGroup> TabOrderComparator = new Comparator<SearchResultGroup>() {
	    public int compare(SearchResultGroup object, SearchResultGroup anotherObject) {
	      String order1 = ((SearchResultGroup) object).getTabOrder();
	      String order2 = ((SearchResultGroup) anotherObject).getTabOrder();

	      return order1.compareTo(order2);
	    }
	};
	
	public static Comparator<SearchResultGroup> ComboOrderComparator = new Comparator<SearchResultGroup>() {
	    public int compare(SearchResultGroup object, SearchResultGroup anotherObject) {
	      String order1 = ((SearchResultGroup) object).getComboOrder();
	      String order2 = ((SearchResultGroup) anotherObject).getComboOrder();

	      return order1.compareTo(order2);
	    }
	};
	
	public SearchResultItem find(String id)
	{
		for (SearchResultItem searchResultItem : this.items) {
			if (searchResultItem.getId().equals(id))
				return searchResultItem;
		}
		
		return null;
	}
}
