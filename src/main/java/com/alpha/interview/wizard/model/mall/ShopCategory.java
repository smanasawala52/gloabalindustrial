package com.alpha.interview.wizard.model.mall;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@Table(name = "shop_category")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@ToString
public class ShopCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "shop_id")
	private Shop shop;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	private boolean featureFlag;
	private int activeStatusKey = ActiveStatusConstants.ACTIVE.getType();

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public boolean isFeatureFlag() {
		return featureFlag;
	}
	public void setFeatureFlag(boolean featureFlag) {
		this.featureFlag = featureFlag;
	}
	public int getActiveStatusKey() {
		return activeStatusKey;
	}
	public void setActiveStatusKey(int activeStatusKey) {
		this.activeStatusKey = activeStatusKey;
	}
}