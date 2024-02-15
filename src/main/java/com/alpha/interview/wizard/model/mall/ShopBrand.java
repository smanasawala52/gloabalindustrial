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
@Table(name = "shop_brand")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@ToString
public class ShopBrand {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "shop_id")
	private Shop shop;

	@ManyToOne
	@JoinColumn(name = "brand_id")
	private Brand brand;

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
	public Brand getBrand() {
		return brand;
	}
	public void setBrand(Brand brand) {
		this.brand = brand;
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
