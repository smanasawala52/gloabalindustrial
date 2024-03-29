package com.alpha.interview.wizard.model.mall;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.alpha.interview.wizard.constants.mall.constants.ActiveStatusConstants;
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
public class Parking {
	public Parking(Long id, String floor, String block) {
		this.id = id;
		this.floor = floor;
		this.block = block;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String floor;
	private String block;
	private String displayName;
	private String imgUrl;
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
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getBlock() {
		return block;
	}
	public void setBlock(String block) {
		this.block = block;
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
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
