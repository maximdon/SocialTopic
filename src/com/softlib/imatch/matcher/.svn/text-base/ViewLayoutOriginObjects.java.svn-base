package com.softlib.imatch.matcher;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.model.SearchResultGroup;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ViewLayoutOriginObjects implements Converter
{
	private List<SearchResultGroup> objects = new ArrayList<SearchResultGroup>();
	
	public List<SearchResultGroup> getObjects() {
		return objects;
	}

	public void setObjects(List<SearchResultGroup> objects) {
		this.objects = objects;
	}

	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		// TODO complete
		
	}
	
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		while(reader.hasMoreChildren())
		{
			reader.moveDown();
			String id = reader.getAttribute("id");
			String title = reader.getAttribute("title");
			String tabOrder = reader.getAttribute("tabOrder");
			String comboOrder = reader.getAttribute("comboOrder");
			objects.add(new SearchResultGroup(id, title, tabOrder, comboOrder));
			reader.moveUp();
		}
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return type.equals(ViewLayoutOriginObjects.class);
	}
	
}
