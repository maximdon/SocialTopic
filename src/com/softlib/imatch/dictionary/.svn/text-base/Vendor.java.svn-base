package com.softlib.imatch.dictionary;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.softlib.imatch.common.StringUtils;

@Entity
@Table(name="VENDOR_DICTIONARY_TERMS")
public class Vendor {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="vendor_id")
	private int vendorId;
	
	@Column(name="vendor_text")
	private String vendorText;
	
	//This constructor for hibernate use only! 
	//Don't use it inside your code
	public Vendor()
	{
	}
	
	
	public Vendor(String vendorText)
	{
		this();
		this.setvendorText(vendorText);
		//setEnabled(true);		
	}

	public void setvendorText(String termText) {
		this.vendorText = toCanonicalForm(termText);		
	}
	
	public String getVendorText() {
		return vendorText;
	}

	public static String toCanonicalForm(String text)
	{
		return text.trim().toLowerCase();
	}
	
	public void setTermId(int vendorId) {
		this.vendorId = vendorId;
	}

	public int getvendorId() {
		return vendorId;
	}
	

	
}
