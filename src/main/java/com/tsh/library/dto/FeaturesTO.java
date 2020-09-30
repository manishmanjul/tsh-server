package com.tsh.library.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FeaturesTO{

	private String key;
	private String name;
	private String permission;
	private String target;
	private String style;
	private int order;
	private FeatureTypeTO featureType;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonIgnore
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public FeatureTypeTO getFeatureType() {
		return featureType;
	}

	public void setFeatureType(FeatureTypeTO featureType) {
		this.featureType = featureType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "Features [name=" + name + ", permission=" + permission + ", target=" + target
				+ ", style=" + style + ", order=" + order + ", featureType=" + featureType + ", description="
				+ description + "]";
	}
}
