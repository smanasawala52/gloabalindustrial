package com.alpha.interview.wizard.controller.mall.util;

import java.util.ArrayList;
import java.util.List;

import com.alpha.interview.wizard.model.mall.Attraction;
import com.alpha.interview.wizard.model.mall.Brand;
import com.alpha.interview.wizard.model.mall.Category;
import com.alpha.interview.wizard.model.mall.Coupon;
import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.model.mall.Product;
import com.alpha.interview.wizard.model.mall.Shop;
import com.alpha.interview.wizard.model.mall.SubCategory;

public class MallUtil {
	public static String formatName(String inputName) {
		if (inputName != null && !inputName.isBlank()) {
			String name = inputName.trim();
			name = name.substring(0, 1).toUpperCase()
					+ name.substring(1).toLowerCase();
			return name;
		}
		return "";
	}
	public static List<String> initalizeChatInput(MallModel inputMallModel) {
		List<String> input = new ArrayList<String>();
		input.add(getMallInputString(inputMallModel));
		input.addAll(getShopInputString(inputMallModel.getShops()));
		input.addAll(getAttractionInputString(inputMallModel.getAttractions()));
		return input;
	}
	private static StringBuilder getCategoryInputString(
			List<Category> categories) {
		StringBuilder stringCategories = new StringBuilder();
		if (categories != null && !categories.isEmpty()) {
			stringCategories.append(" categories: (");
			for (Category category : categories) {
				// Iterate over subcategories within each category
				StringBuilder stringSubCategories = new StringBuilder();
				if (category.getSubCategories() != null
						&& !category.getSubCategories().isEmpty()) {
					stringSubCategories.append(" with sub categories [");
					for (SubCategory subcategory : category
							.getSubCategories()) {
						stringSubCategories.append(subcategory.getDisplayName())
								.append(", ");
					}
					stringSubCategories.append("] ");
				}
				stringCategories.append(category.getDisplayName()).append(", ");
			}
			stringCategories.append(") ");
		}
		return stringCategories;
	}
	private static StringBuilder getProductInputString(List<Product> products) {
		StringBuilder stringCategories = new StringBuilder();
		if (products != null && !products.isEmpty()) {
			stringCategories.append(" products: (");
			for (Product category : products) {
				stringCategories.append(category.getDisplayName()).append(", ");
			}
			stringCategories.append(") ");
		}
		return stringCategories;
	}
	private static StringBuilder getCouponInputString(List<Coupon> coupons) {
		StringBuilder stringCategories = new StringBuilder();
		if (coupons != null && !coupons.isEmpty()) {
			stringCategories.append(" coupons: (");
			for (Coupon category : coupons) {
				stringCategories.append(category.getDisplayName()).append(", ");
			}
			stringCategories.append(") ");
		}
		return stringCategories;
	}
	private static StringBuilder getBrandInputString(List<Brand> brands) {
		StringBuilder stringCategories = new StringBuilder();
		if (brands != null && !brands.isEmpty()) {
			stringCategories.append(" brands: (");
			for (Brand category : brands) {
				stringCategories.append(category.getDisplayName()).append(", ");
			}
			stringCategories.append(") ");
		}
		return stringCategories;
	}

	private static List<String> getShopInputString(List<Shop> shops) {
		List<String> input = new ArrayList<String>();
		if (shops != null) {
			shops.stream().forEach(shop -> {
				StringBuilder sb = new StringBuilder();
				sb.append("shopId: ").append(shop.getId());
				if (shop.getShopNumber() != null
						&& !shop.getShopNumber().isBlank()) {
					sb.append(", shop number: ").append(shop.getShopNumber());
				}
				sb.append(", name: ").append(shop.getDisplayName());
				if (shop.getFloor() != null && !shop.getFloor().isBlank()) {
					sb.append(", floor: ").append(shop.getFloor());
				}
				if (shop.getHowToReach() != null
						&& !shop.getHowToReach().isBlank()) {
					sb.append(", how to reach: ").append(shop.getHowToReach());
				}
				if (shop.getFounded() != null && !shop.getFounded().isBlank()) {
					sb.append(", founded: ").append(shop.getFounded());
				}
				sb.append(getBrandInputString(shop.getBrands()));
				sb.append(getCategoryInputString(shop.getCategories()));
				sb.append(getCouponInputString(shop.getCoupons()));
				sb.append(getProductInputString(shop.getProducts()));
				if (shop.getDescription() != null
						&& !shop.getDescription().isBlank()) {
					sb.append(", details: ").append(shop.getDescription());
				}
				if (shop.getAdditionalDetails() != null
						&& !shop.getAdditionalDetails().isBlank()) {
					sb.append(" ").append(shop.getAdditionalDetails());
				}
				input.add(sb.toString());
			});
		}
		return input;
	}
	private static List<String> getAttractionInputString(
			List<Attraction> shops) {
		List<String> input = new ArrayList<String>();
		if (shops != null) {
			shops.stream().forEach(attraction -> {
				StringBuilder sb = new StringBuilder();
				sb.append("attractionId: ").append(attraction.getId());
				sb.append(", name: ").append(attraction.getDisplayName());
				if (attraction.getFloor() != null
						&& !attraction.getFloor().isBlank()) {
					sb.append(", floor: ").append(attraction.getFloor());
				}
				if (attraction.getHowToReach() != null
						&& !attraction.getHowToReach().isBlank()) {
					sb.append(", how to reach: ")
							.append(attraction.getHowToReach());
				}
				sb.append(getBrandInputString(attraction.getBrands()));
				sb.append(getCategoryInputString(attraction.getCategories()));
				sb.append(getCouponInputString(attraction.getCoupons()));
				sb.append(getProductInputString(attraction.getProducts()));
				if (attraction.getDescription() != null
						&& !attraction.getDescription().isBlank()) {
					sb.append(", details: ")
							.append(attraction.getDescription());
				}
				if (attraction.getAdditionalDetails() != null
						&& !attraction.getAdditionalDetails().isBlank()) {
					sb.append(" ").append(attraction.getAdditionalDetails());
				}
				input.add(sb.toString());
			});
		}
		return input;
	}
	private static String getMallInputString(MallModel inputMallModel) {
		StringBuilder sb = new StringBuilder();
		sb.append("mallId: ").append(inputMallModel.getId());
		sb.append(" name: ").append(inputMallModel.getDisplayName());
		if (inputMallModel.getFloors() != null
				&& !inputMallModel.getFloors().isBlank()) {
			sb.append(", floors: ").append(inputMallModel.getFloors());
		}
		if (inputMallModel.getCity() != null
				&& !inputMallModel.getCity().isBlank()) {
			sb.append(", city: ").append(inputMallModel.getCity());
		}
		if (inputMallModel.getCountry() != null
				&& !inputMallModel.getCountry().isBlank()) {
			sb.append(", country: ").append(inputMallModel.getCountry());
		}
		if (inputMallModel.getDescription() != null
				&& !inputMallModel.getDescription().isBlank()) {
			sb.append(", details: ").append(inputMallModel.getDescription());
		}
		if (inputMallModel.getAdditionalDetails() != null
				&& !inputMallModel.getAdditionalDetails().isBlank()) {
			sb.append(" ").append(inputMallModel.getAdditionalDetails());
		}
		return sb.toString();
	}
}
