package com.alpha.interview.wizard.model.mall;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import com.alpha.interview.wizard.constants.mall.constants.ActiveStatusConstants;
import com.alpha.interview.wizard.controller.mall.util.MallUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@ToString
public class Attraction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToMany(cascade = CascadeType.ALL)
	private List<Category> categories;
	private String floor;
	private String location;
	private String howToReach;

	private String name;
	private String description;
	private String displayName;
	private String imgUrl;
	private boolean featured;
	@Column(length = 500)
	private String additionalDetails;
	private int activeStatusKey = ActiveStatusConstants.ACTIVE.getType();
	private Date updateTimestamp;
	private Date createTimestamp;
	@Transient
	private boolean linked = false;

	public boolean isLinked() {
		return linked;
	}
	public void setLinked(boolean linked) {
		this.linked = linked;
	}
	public Date getCreateTimestamp() {
		return createTimestamp;
	}
	public void setCreateTimestamp(Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<Category> getCategories() {
		return categories;
	}
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getHowToReach() {
		return howToReach;
	}
	public void setHowToReach(String howToReach) {
		this.howToReach = howToReach;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = MallUtil.formatName(name);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public boolean isFeatured() {
		return featured;
	}
	public void setFeatured(boolean featured) {
		this.featured = featured;
	}
	public String getAdditionalDetails() {
		return additionalDetails;
	}
	public void setAdditionalDetails(String additionalDetails) {
		this.additionalDetails = additionalDetails;
	}
	public int getActiveStatusKey() {
		return activeStatusKey;
	}
	public void setActiveStatusKey(int activeStatusKey) {
		this.activeStatusKey = activeStatusKey;
	}
	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}
	public void setUpdateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
