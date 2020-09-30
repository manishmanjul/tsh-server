package com.tsh.library.dto;

public class FeatureTypeTO{

	private String typeName;
	private String subType;

	public String getTypeName() {
		return typeName;
	}

	public String getSubType() {
		return subType;
	}

	public String toString() {
		return "FeatureType [typeName=" + typeName + ", subType=" + subType + "]";
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}
}
