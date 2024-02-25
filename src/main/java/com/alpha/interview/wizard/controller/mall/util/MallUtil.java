package com.alpha.interview.wizard.controller.mall.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.alpha.interview.wizard.model.mall.Attraction;
import com.alpha.interview.wizard.model.mall.Brand;
import com.alpha.interview.wizard.model.mall.Category;
import com.alpha.interview.wizard.model.mall.Coupon;
import com.alpha.interview.wizard.model.mall.Event;
import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.model.mall.OtherAttraction;
import com.alpha.interview.wizard.model.mall.Parking;
import com.alpha.interview.wizard.model.mall.Product;
import com.alpha.interview.wizard.model.mall.Shop;

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
	public static String formatJsonName(String inputName) {
		if (inputName != null && !inputName.isBlank()) {
			String name = inputName.trim();
			name = name.substring(0, 1).toUpperCase()
					+ name.substring(1).toLowerCase();
			name = name.replaceAll("\"", "");
			name = name.replaceAll("'", "");
			return name;
		}
		return "";
	}
	public static List<String> initalizeChatInput(MallModel inputMallModel) {
		List<String> input = new ArrayList<String>();
		input.add(getMallInputString(inputMallModel));
		input.addAll(getShopInputString(inputMallModel.getShops(),
				inputMallModel.getId().toString()));
		input.addAll(getAttractionInputString(inputMallModel.getAttractions(),
				inputMallModel.getId().toString()));
		input.addAll(getEventInputString(inputMallModel.getEvents(),
				inputMallModel.getId().toString()));
		input.addAll(getParkingInputString(inputMallModel.getParkings(),
				inputMallModel.getId().toString()));
		input.addAll(getOtherAttractionInputString(
				inputMallModel.getOtherAttractions(),
				inputMallModel.getId().toString()));
		return input;
	}

	private static StringBuilder getCategoryInputString(
			List<Category> categories) {
		StringBuilder stringCategories = new StringBuilder();
		if (categories != null && !categories.isEmpty()) {
			stringCategories.append(", \"categories\": [");
			String preCat = "";
			for (Category category : categories) {
				// Iterate over subcategories within each category
				stringCategories.append(preCat).append("\"")
						.append(formatJsonName(category.getDisplayName()))
						.append("\"");
				preCat = ", ";
			}
			stringCategories.append("] ");
		}
		return stringCategories;
	}
	private static StringBuilder getProductInputString(List<Product> products) {
		StringBuilder stringProduct = new StringBuilder();
		if (products != null && !products.isEmpty()) {
			stringProduct.append(", \"products\": [");
			String preProd = "";
			for (Product product : products) {
				stringProduct.append(preProd).append("\"")
						.append(formatJsonName(product.getDisplayName()))
						.append("\"");
				preProd = ", ";
			}
			stringProduct.append("] ");
		}
		return stringProduct;
	}
	private static StringBuilder getCouponInputString(List<Coupon> coupons) {
		StringBuilder stringCoupons = new StringBuilder();
		if (coupons != null && !coupons.isEmpty()) {
			stringCoupons.append(", \"coupons\": [");
			String preCoupon = "";
			for (Coupon coupon : coupons) {
				if (isTodayInRange(coupon.getStartDate(),
						coupon.getEndDate())) {
					stringCoupons.append(preCoupon).append("\"")
							.append(formatJsonName(coupon.getDisplayName()))
							.append("\"");
					preCoupon = ", ";
				}
			}
			stringCoupons.append("] ");
		}
		return stringCoupons;
	}
	private static StringBuilder getBrandInputString(List<Brand> brands) {
		StringBuilder stringBrands = new StringBuilder();
		if (brands != null && !brands.isEmpty()) {
			stringBrands.append(", \"brands\": [");
			String preBrand = "";
			for (Brand brand : brands) {
				stringBrands.append(preBrand).append("\"")
						.append(formatJsonName(brand.getDisplayName()))
						.append("\"");
				preBrand = ", ";
			}
			stringBrands.append("]");
		}
		return stringBrands;
	}

	private static List<String> getShopInputString(List<Shop> shops,
			String mallId) {
		List<String> input = new ArrayList<String>();
		if (shops != null) {
			shops.stream().forEach(shop -> {
				StringBuilder sb = new StringBuilder();
				sb.append("{\"mallId\": \"").append(mallId).append("\"");
				sb.append(", \"shopId\": \"").append(shop.getId()).append("\"");
				if (shop.getShopNumber() != null
						&& !shop.getShopNumber().isBlank()) {
					sb.append(", \"shopNumber\": \"")
							.append(formatJsonName(shop.getShopNumber()))
							.append("\"");
				}
				sb.append(", \"name\": \"")
						.append(formatJsonName(shop.getDisplayName()))
						.append("\"");
				if (shop.getFloor() != null && !shop.getFloor().isBlank()) {
					sb.append(", \"floor\": \"")
							.append(formatJsonName(shop.getFloor()))
							.append("\"");
				}
				if (shop.getHowToReach() != null
						&& !shop.getHowToReach().isBlank()) {
					sb.append(", \"howToReach\": \"")
							.append(formatJsonName(shop.getHowToReach()))
							.append("\"");
				}
				if (shop.getFounded() != null && !shop.getFounded().isBlank()) {
					sb.append(", \"founded\": \"")
							.append(formatJsonName(shop.getFounded()))
							.append("\"");
				}
				sb.append(getBrandInputString(shop.getBrands()));
				sb.append(getCategoryInputString(shop.getCategories()));
				sb.append(getCouponInputString(shop.getCoupons()));
				sb.append(getProductInputString(shop.getProducts()));
				if (shop.getDescription() != null
						&& !shop.getDescription().isBlank()) {
					sb.append(", \"details\": \"")
							.append(formatJsonName(shop.getDescription()))
							.append("\"");
				}
				if (shop.getAdditionalDetails() != null
						&& !shop.getAdditionalDetails().isBlank()) {
					sb.append(", \"addDet\": \"")
							.append(formatJsonName(shop.getAdditionalDetails()))
							.append("\"");;
				}
				sb.append("}");
				input.add(sb.toString());
			});
		}
		return input;
	}
	private static List<String> getAttractionInputString(List<Attraction> shops,
			String mallId) {
		List<String> input = new ArrayList<String>();
		if (shops != null) {
			shops.stream().forEach(attraction -> {
				StringBuilder sb = new StringBuilder();
				sb.append("{\"mallId\": \"").append(mallId).append("\"");
				sb.append(", \"attractionId\": \"").append(attraction.getId())
						.append("\"");
				sb.append(", \"name\": \"")
						.append(formatJsonName(attraction.getDisplayName()))
						.append("\"");
				if (attraction.getFloor() != null
						&& !attraction.getFloor().isBlank()) {
					sb.append(", \"floor\": \"")
							.append(formatJsonName(attraction.getFloor()))
							.append("\"");
				}
				if (attraction.getHowToReach() != null
						&& !attraction.getHowToReach().isBlank()) {
					sb.append(", \"howToReach\": \"")
							.append(formatJsonName(attraction.getHowToReach()))
							.append("\"");
				}
				sb.append(getBrandInputString(attraction.getBrands()));
				sb.append(getCategoryInputString(attraction.getCategories()));
				sb.append(getCouponInputString(attraction.getCoupons()));
				sb.append(getProductInputString(attraction.getProducts()));
				if (attraction.getDescription() != null
						&& !attraction.getDescription().isBlank()) {
					sb.append(", \"details\": \"")
							.append(formatJsonName(attraction.getDescription()))
							.append("\"");
				}
				if (attraction.getAdditionalDetails() != null
						&& !attraction.getAdditionalDetails().isBlank()) {
					sb.append(", \"addDet\": \"")
							.append(formatJsonName(
									attraction.getAdditionalDetails()))
							.append("\"");
				}
				sb.append("}");
				input.add(sb.toString());
			});
		}
		return input;
	}
	private static String getMallInputString(MallModel inputMallModel) {
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"mallId\": \"").append(inputMallModel.getId())
				.append("\"");
		sb.append(", \"name\": \"")
				.append(formatJsonName(inputMallModel.getDisplayName()))
				.append("\"");
		if (inputMallModel.getFloors() != null
				&& !inputMallModel.getFloors().isBlank()) {
			sb.append(", \"floors\": \"")
					.append(formatJsonName(inputMallModel.getFloors()))
					.append("\"");
		}
		if (inputMallModel.getCity() != null
				&& !inputMallModel.getCity().isBlank()) {
			sb.append(", \"city\": \"")
					.append(formatJsonName(inputMallModel.getCity()))
					.append("\"");
		}
		if (inputMallModel.getCountry() != null
				&& !inputMallModel.getCountry().isBlank()) {
			sb.append(", \"country\": \"")
					.append(formatJsonName(inputMallModel.getCountry()))
					.append("\"");
		}
		if (inputMallModel.getDescription() != null
				&& !inputMallModel.getDescription().isBlank()) {
			sb.append(", \"details\": \"")
					.append(formatJsonName(inputMallModel.getDescription()))
					.append("\"");
		}
		if (inputMallModel.getAdditionalDetails() != null
				&& !inputMallModel.getAdditionalDetails().isBlank()) {
			sb.append(", \"addDet\": \"")
					.append(formatJsonName(
							inputMallModel.getAdditionalDetails()))
					.append("\"");
		}
		sb.append("}");
		return sb.toString();
	}
	private static List<String> getOtherAttractionInputString(
			List<OtherAttraction> otherAttractions, String mallId) {
		List<String> input = new ArrayList<String>();
		if (otherAttractions != null) {
			otherAttractions.stream().filter(
					x -> isTodayInRange(x.getStartDate(), x.getEndDate()))
					.forEach(attraction -> {
						StringBuilder sb = new StringBuilder();
						sb.append("{ \"mallId\": \"").append(mallId)
								.append("\"");
						sb.append(", \"otherAttractionId\": \"")
								.append(attraction.getId()).append("\"");
						sb.append(", \"name\": \"")
								.append(formatJsonName(
										attraction.getDisplayName()))
								.append("\"");
						if (attraction.getFloor() != null
								&& !attraction.getFloor().isBlank()) {
							sb.append(", \"floor\": \"")
									.append(formatJsonName(
											attraction.getFloor()))
									.append("\"");
						}
						if (attraction.getHowToReach() != null
								&& !attraction.getHowToReach().isBlank()) {
							sb.append(", \"howToReach\": \"")
									.append(formatJsonName(
											attraction.getHowToReach()))
									.append("\"");
						}
						if (attraction.getDescription() != null
								&& !attraction.getDescription().isBlank()) {
							sb.append(", \"details\": \"")
									.append(formatJsonName(
											attraction.getDescription()))
									.append("\"");
						}
						if (attraction.getAdditionalDetails() != null
								&& !attraction.getAdditionalDetails()
										.isBlank()) {
							sb.append(", \"addDet\": \"")
									.append(formatJsonName(
											attraction.getAdditionalDetails()))
									.append("\"");
						}
						sb.append("}");
						input.add(sb.toString());
					});
		}
		return input;
	}
	private static List<String> getParkingInputString(List<Parking> parkings,
			String mallId) {
		List<String> input = new ArrayList<String>();
		if (parkings != null) {
			parkings.stream().forEach(attraction -> {
				StringBuilder sb = new StringBuilder();
				sb.append("{ \"mallId\": \"").append(mallId).append("\"");
				sb.append(", \"parkingId\": \"").append(attraction.getId())
						.append("\"");
				sb.append(", \"name\": \"")
						.append(formatJsonName(attraction.getDisplayName()))
						.append("\"");
				if (attraction.getFloor() != null
						&& !attraction.getFloor().isBlank()) {
					sb.append(", \"floor\": \"")
							.append(formatJsonName(attraction.getFloor()))
							.append("\"");
				}
				if (attraction.getBlock() != null
						&& !attraction.getBlock().isBlank()) {
					sb.append(", \"block\": \"")
							.append(formatJsonName(attraction.getBlock()))
							.append("\"");
				}
				if (attraction.getAdditionalDetails() != null
						&& !attraction.getAdditionalDetails().isBlank()) {
					sb.append(", \"addDet\": \"")
							.append(formatJsonName(
									attraction.getAdditionalDetails()))
							.append("\"");
				}
				sb.append("}");
				input.add(sb.toString());
			});
		}
		return input;
	}
	private static List<String> getEventInputString(List<Event> events,
			String mallId) {
		List<String> input = new ArrayList<String>();
		if (events != null) {
			events.stream().filter(
					x -> isTodayInRange(x.getStartDate(), x.getEndDate()))
					.forEach(event -> {
						StringBuilder sb = new StringBuilder();
						sb.append("{ \"mallId\": \"").append(mallId)
								.append("\"");
						sb.append(", \"eventId\": \"").append(event.getId())
								.append("\"");
						sb.append(", \"name\": \"")
								.append(formatJsonName(event.getDisplayName()))
								.append("\"");
						if (event.getDescription() != null
								&& !event.getDescription().isBlank()) {
							sb.append(", \"details\": \"")
									.append(formatJsonName(
											event.getDescription()))
									.append("\"");
						}
						if (event.getAdditionalDetails() != null
								&& !event.getAdditionalDetails().isBlank()) {
							sb.append(", \"addDet\": \"")
									.append(formatJsonName(
											event.getAdditionalDetails()))
									.append("\"");
						}
						sb.append("}");
						input.add(sb.toString());
					});
		}
		return input;
	}
	public static boolean isTodayInRange(Date startDate, Date endDate) {
		// Get today\"s date
		Date today = new Date();

		// Set start date to 00:00
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		startCal.set(Calendar.HOUR_OF_DAY, 0);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.SECOND, 0);
		startDate = startCal.getTime();

		// Set end date to 23:59
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		endCal.set(Calendar.HOUR_OF_DAY, 23);
		endCal.set(Calendar.MINUTE, 59);
		endCal.set(Calendar.SECOND, 59);
		endDate = endCal.getTime();

		// Check if today\"s date is within the range
		return (today.compareTo(startDate) >= 0
				&& today.compareTo(endDate) <= 0);
	}
	public static List<Long> convertToLongList(String str) {
		List<Long> longList = new ArrayList<>();
		if (str != null && !str.isBlank()) {
			// Remove square brackets and split by comma
			str = str.replace("[", "");
			str = str.replace("]", "");
			String[] numbers = str.split(",");
			// Parse each number and add to the list
			for (String number : numbers) {
				try {
					longList.add(Long.parseLong(number.trim()));
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
		return longList;
	}
}
