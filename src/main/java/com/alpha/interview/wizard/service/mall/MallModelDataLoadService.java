package com.alpha.interview.wizard.service.mall;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
import com.alpha.interview.wizard.model.mall.SubCategory;
import com.alpha.interview.wizard.model.mall.WebImage;
import com.alpha.interview.wizard.repository.mall.AttractionRepository;
import com.alpha.interview.wizard.repository.mall.BrandRepository;
import com.alpha.interview.wizard.repository.mall.CategoryRepository;
import com.alpha.interview.wizard.repository.mall.CouponRepository;
import com.alpha.interview.wizard.repository.mall.EventRepository;
import com.alpha.interview.wizard.repository.mall.MallModelRepository;
import com.alpha.interview.wizard.repository.mall.OtherAttractionRepository;
import com.alpha.interview.wizard.repository.mall.ParkingRepository;
import com.alpha.interview.wizard.repository.mall.ProductRepository;
import com.alpha.interview.wizard.repository.mall.ShopRepository;
import com.alpha.interview.wizard.repository.mall.SubCategoryRepository;
import com.alpha.interview.wizard.repository.mall.WebImageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("MallModelDataLoadService")
@Service
public class MallModelDataLoadService {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AttractionRepository attractionRepository;
	@Autowired
	private BrandRepository brandRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private CouponRepository couponRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private OtherAttractionRepository otherAttractionRepository;

	@Autowired
	private MallModelRepository mallModelRepository;

	@Autowired
	private SubCategoryRepository subCategoryRepository;
	@Autowired
	private ParkingRepository parkingRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private WebImageRepository webImageRepository;

	@Autowired
	private ShopRepository shopRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void loadMallModelData(MultipartFile file) throws Exception {
		System.out.println("Multipart Dataloading started");
		try {
			String jsonArrayData = new String(file.getBytes());
			System.out.println("=============================");
			System.out.println(jsonArrayData);
			System.out.println("=============================");
			// objectMapper.setPropertyNamingStrategy(
			// PropertyNamingStrategy.SNAKE_CASE);
			// Type subCategoryListType = new TypeToken<List<MallModel>>() {
			// }.getType();
			// SimpleModule subCategoryModule = new SimpleModule();
			// subCategoryModule.addDeserializer(SubCategory.class,
			// new SubCategoryDeserializer());
			// objectMapper.registerModule(subCategoryModule);
			// SimpleModule categoryModule = new SimpleModule();
			// categoryModule.addDeserializer(Category.class,
			// new CategoryDeserializer());
			// objectMapper.registerModule(categoryModule);
			// SimpleModule attractionModule = new SimpleModule();
			// attractionModule.addDeserializer(Attraction.class,
			// new AttractionDeserializer());
			// objectMapper.registerModule(attractionModule);
			// SimpleModule brandModule = new SimpleModule();
			// brandModule.addDeserializer(Brand.class, new
			// BrandDeserializer());
			// objectMapper.registerModule(brandModule);
			// SimpleModule couponModule = new SimpleModule();
			// couponModule.addDeserializer(Coupon.class,
			// new CouponDeserializer());
			// objectMapper.registerModule(couponModule);
			//
			// SimpleModule eventModule = new SimpleModule();
			// eventModule.addDeserializer(Event.class, new
			// EventDeserializer());
			// objectMapper.registerModule(eventModule);
			//
			// SimpleModule mallModelModule = new SimpleModule();
			// mallModelModule.addDeserializer(MallModel.class,
			// new MallModelDeserializer());
			// objectMapper.registerModule(mallModelModule);
			//
			// SimpleModule otherAttractionModule = new SimpleModule();
			// otherAttractionModule.addDeserializer(OtherAttraction.class,
			// new OtherAttractionDeserializer());
			// objectMapper.registerModule(otherAttractionModule);
			//
			// SimpleModule parkingModule = new SimpleModule();
			// parkingModule.addDeserializer(Parking.class,
			// new ParkingDeserializer());
			// objectMapper.registerModule(parkingModule);
			//
			// SimpleModule productModule = new SimpleModule();
			// productModule.addDeserializer(Product.class,
			// new ProductDeserializer());
			// objectMapper.registerModule(productModule);
			//
			// SimpleModule shopModule = new SimpleModule();
			// shopModule.addDeserializer(Shop.class, new ShopDeserializer());
			// objectMapper.registerModule(shopModule);
			//
			// SimpleModule webImageModule = new SimpleModule();
			// webImageModule.addDeserializer(WebImage.class,
			// new WebImageDeserializer());
			// objectMapper.registerModule(webImageModule);
			try {
				// Parse the JSON array string into a list of SubCategory
				// objects
				// List<MallModel> mallModels = new
				// Gson().fromJson(jsonArrayData,
				// subCategoryListType);
				List<MallModel> mallModels = Arrays.asList(objectMapper
						.readValue(jsonArrayData, MallModel[].class));
				// mallModelRepository.saveAllAndFlush(mallModels);
				// for (MallModel mallModel : mallModels) {
				// entityManager.merge(mallModel);
				// }
				for (MallModel mallModel : mallModels) {
					for (WebImage image : mallModel.getImages()) {
						// Optional<WebImage> webImageOptional =
						// webImageRepository
						// .findById(image.getId());
						// if (!webImageOptional.isPresent()) {
						// webImageRepository.saveAndFlush(image);
						// webImageRepository.flush();
						// }
						image.setId(null);
					}
					for (Event event : mallModel.getEvents()) {
						// Optional<Event> eventOptional = eventRepository
						// .findById(event.getId());
						// if (!eventOptional.isPresent()) {
						// eventRepository.saveAndFlush(event);
						// }
						event.setId(null);
					}
					for (OtherAttraction otherAttraction : mallModel
							.getOtherAttractions()) {
						// Optional<OtherAttraction> otherAttractionOptional =
						// otherAttractionRepository
						// .findById(otherAttraction.getId());
						// if (!otherAttractionOptional.isPresent()) {
						// otherAttractionRepository
						// .saveAndFlush(otherAttraction);
						// }
						otherAttraction.setId(null);
					}
					for (Parking parking : mallModel.getParkings()) {
						// Optional<Parking> otherAttractionOptional =
						// parkingRepository
						// .findById(parking.getId());
						// if (!otherAttractionOptional.isPresent()) {
						// parkingRepository.saveAndFlush(parking);
						// }
						parking.setId(null);
					}
					for (Attraction attraction : mallModel.getAttractions()) {
						for (Category category : attraction.getCategories()) {
							for (SubCategory subCategory : category
									.getSubCategories()) {
								// Optional<SubCategory> subCategoryOptional =
								// subCategoryRepository
								// .findById(subCategory.getId());
								// if (!subCategoryOptional.isPresent()) {
								// subCategoryRepository
								// .saveAndFlush(subCategory);
								// }
								subCategory.setId(null);
							}

							// Optional<Category> categoryOptional =
							// categoryRepository
							// .findById(category.getId());
							// if (!categoryOptional.isPresent()) {
							// categoryRepository.saveAndFlush(category);
							// }
							category.setId(null);
						}
						for (Coupon coupon : attraction.getCoupons()) {
							// Optional<Coupon> couponOptional =
							// couponRepository
							// .findById(coupon.getId());
							// if (!couponOptional.isPresent()) {
							// couponRepository.saveAndFlush(coupon);
							// }
							coupon.setId(null);
						}
						for (Brand brand : attraction.getBrands()) {
							// Optional<Brand> brandOptional = brandRepository
							// .findById(brand.getId());
							// if (!brandOptional.isPresent()) {
							// brandRepository.saveAndFlush(brand);
							// }
							brand.setId(null);
						}
						for (Product product : attraction.getProducts()) {
							// Optional<Product> brandOptional =
							// productRepository
							// .findById(product.getId());
							// if (!brandOptional.isPresent()) {
							// productRepository.saveAndFlush(product);
							// }
							product.setId(null);
						}

						// Optional<Attraction> attractionOptional =
						// attractionRepository
						// .findById(attraction.getId());
						// if (!attractionOptional.isPresent()) {
						// attractionRepository.saveAndFlush(attraction);
						// }
						attraction.setId(null);
					}

					for (Shop shop : mallModel.getShops()) {
						for (Category category : shop.getCategories()) {
							for (SubCategory subCategory : category
									.getSubCategories()) {
								// Optional<SubCategory> subCategoryOptional =
								// subCategoryRepository
								// .findById(subCategory.getId());
								// if (!subCategoryOptional.isPresent()) {
								// subCategoryRepository
								// .saveAndFlush(subCategory);
								// }
								subCategory.setId(null);
							}

							// Optional<Category> categoryOptional =
							// categoryRepository
							// .findById(category.getId());
							// if (!categoryOptional.isPresent()) {
							// categoryRepository.saveAndFlush(category);
							// }
							category.setId(null);
						}
						for (Coupon coupon : shop.getCoupons()) {
							// Optional<Coupon> couponOptional =
							// couponRepository
							// .findById(coupon.getId());
							// if (!couponOptional.isPresent()) {
							// couponRepository.saveAndFlush(coupon);
							// }
							coupon.setId(null);
						}
						for (Brand brand : shop.getBrands()) {
							// Optional<Brand> brandOptional = brandRepository
							// .findById(brand.getId());
							// if (!brandOptional.isPresent()) {
							// brandRepository.saveAndFlush(brand);
							// }
							brand.setId(null);
						}
						for (Product product : shop.getProducts()) {
							// Optional<Product> brandOptional =
							// productRepository
							// .findById(product.getId());
							// if (!brandOptional.isPresent()) {
							// productRepository.saveAndFlush(product);
							// }
							product.setId(null);
						}
						// Optional<Shop> shopOptional = shopRepository
						// .findById(shop.getId());
						// if (!shopOptional.isPresent()) {
						// shopRepository.saveAndFlush(shop);
						// }
						shop.setId(null);
					}

					// Optional<MallModel> mallModelOptional =
					// mallModelRepository
					// .findById(mallModel.getId());
					// if (!mallModelOptional.isPresent()) {
					// mallModelRepository.saveAndFlush(mallModel);
					// }
					mallModel.setId(null);
				}

				// Now you have a List<SubCategory> containing your objects
				// You can iterate over the list or perform other operations as
				// needed
				// for (MallModel mallModel : mallModels) {
				// System.out.println(mallModel);
				// }
				// JSONArray jsonArray = new JSONArray(jsonArrayData);
				//
				// for (int i = 0; i < jsonArray.length(); i++) {
				// JSONObject jsonObject = jsonArray.getJSONObject(i);
				// JSONArray shopsArray = jsonObject.getJSONArray("shops");
				//
				// for (int j = 0; j < shopsArray.length(); j++) {
				// JSONObject shopObject = shopsArray.getJSONObject(j);
				// JSONArray categoriesArray =
				// shopObject.getJSONArray("categories");
				//
				// for (int k = 0; k < categoriesArray.length(); k++) {
				// JSONObject categoryObject = categoriesArray.getJSONObject(k);
				// String categoryName = categoryObject.getString("name");
				// System.out.println("Category: " + categoryName);
				// System.out.println("SUb Category: " +
				// categoryObject.getJSONArray("subCategories"));
				// }
				// }
				// }
				//
				// List<MallModel> entities =
				// Arrays.asList(objectMapper.readValue(jsonArrayData,
				// MallModel[].class));
				System.out.println("=============================");
				System.out.println(mallModels);
				System.out.println("=============================");
				mallModelRepository.saveAll(mallModels);
				mallModelRepository.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// private class SubCategoryDeserializer
	// extends
	// JsonDeserializer<SubCategory> {
	// @Override
	// public SubCategory deserialize(JsonParser p,
	// DeserializationContext ctxt) throws IOException {
	// JsonNode node = p.getCodec().readTree(p);
	// Long subCategoryId = node.get("id").asLong();
	// SubCategory existingSubCategory = entityManager
	// .find(SubCategory.class, subCategoryId);
	// if (existingSubCategory != null) {
	// return existingSubCategory;
	// } else {
	// return objectMapper.treeToValue(node, SubCategory.class);
	// }
	// }
	// }
	//
	// private class WebImageDeserializer extends JsonDeserializer<WebImage> {
	// @Override
	// public WebImage deserialize(JsonParser p, DeserializationContext ctxt)
	// throws IOException {
	// JsonNode node = p.getCodec().readTree(p);
	// Long subCategoryId = node.get("id").asLong();
	// WebImage existingSubCategory = entityManager.find(WebImage.class,
	// subCategoryId);
	// if (existingSubCategory != null) {
	// return existingSubCategory;
	// } else {
	// return objectMapper.treeToValue(node, WebImage.class);
	// }
	// }
	// }
	// private class ShopDeserializer extends JsonDeserializer<Shop> {
	// @Override
	// public Shop deserialize(JsonParser p, DeserializationContext ctxt)
	// throws IOException {
	// JsonNode node = p.getCodec().readTree(p);
	// Long subCategoryId = node.get("id").asLong();
	// Shop existingSubCategory = entityManager.find(Shop.class,
	// subCategoryId);
	// if (existingSubCategory != null) {
	// return existingSubCategory;
	// } else {
	// return objectMapper.treeToValue(node, Shop.class);
	// }
	// }
	// }
	// private class ProductDeserializer extends JsonDeserializer<Product> {
	// @Override
	// public Product deserialize(JsonParser p, DeserializationContext ctxt)
	// throws IOException {
	// JsonNode node = p.getCodec().readTree(p);
	// Long subCategoryId = node.get("id").asLong();
	// Product existingSubCategory = entityManager.find(Product.class,
	// subCategoryId);
	// if (existingSubCategory != null) {
	// return existingSubCategory;
	// } else {
	// return objectMapper.treeToValue(node, Product.class);
	// }
	// }
	// }
	//
	// private class ParkingDeserializer extends JsonDeserializer<Parking> {
	// @Override
	// public Parking deserialize(JsonParser p, DeserializationContext ctxt)
	// throws IOException {
	// JsonNode node = p.getCodec().readTree(p);
	// Long subCategoryId = node.get("id").asLong();
	// Parking existingSubCategory = entityManager.find(Parking.class,
	// subCategoryId);
	// if (existingSubCategory != null) {
	// return existingSubCategory;
	// } else {
	// return objectMapper.treeToValue(node, Parking.class);
	// }
	// }
	// }
	// private class OtherAttractionDeserializer
	// extends
	// JsonDeserializer<OtherAttraction> {
	// @Override
	// public OtherAttraction deserialize(JsonParser p,
	// DeserializationContext ctxt) throws IOException {
	// JsonNode node = p.getCodec().readTree(p);
	// Long subCategoryId = node.get("id").asLong();
	// OtherAttraction existingSubCategory = entityManager
	// .find(OtherAttraction.class, subCategoryId);
	// if (existingSubCategory != null) {
	// return existingSubCategory;
	// } else {
	// return objectMapper.treeToValue(node, OtherAttraction.class);
	// }
	// }
	// }
	// private class MallModelDeserializer extends JsonDeserializer<MallModel> {
	// @Override
	// public MallModel deserialize(JsonParser p, DeserializationContext ctxt)
	// throws IOException {
	// JsonNode node = p.getCodec().readTree(p);
	// Long subCategoryId = node.get("id").asLong();
	// MallModel existingSubCategory = entityManager.find(MallModel.class,
	// subCategoryId);
	// if (existingSubCategory != null) {
	// return existingSubCategory;
	// } else {
	// return objectMapper.treeToValue(node, MallModel.class);
	// }
	// }
	// }
	// private class EventDeserializer extends JsonDeserializer<Event> {
	// @Override
	// public Event deserialize(JsonParser p, DeserializationContext ctxt)
	// throws IOException {
	// JsonNode node = p.getCodec().readTree(p);
	// Long subCategoryId = node.get("id").asLong();
	// Event existingSubCategory = entityManager.find(Event.class,
	// subCategoryId);
	// if (existingSubCategory != null) {
	// return existingSubCategory;
	// } else {
	// return objectMapper.treeToValue(node, Event.class);
	// }
	// }
	// }
	// private class CouponDeserializer extends JsonDeserializer<Coupon> {
	// @Override
	// public Coupon deserialize(JsonParser p, DeserializationContext ctxt)
	// throws IOException {
	// JsonNode node = p.getCodec().readTree(p);
	// Long subCategoryId = node.get("id").asLong();
	// Coupon existingSubCategory = entityManager.find(Coupon.class,
	// subCategoryId);
	// if (existingSubCategory != null) {
	// return existingSubCategory;
	// } else {
	// return objectMapper.treeToValue(node, Coupon.class);
	// }
	// }
	// }
	// private class BrandDeserializer extends JsonDeserializer<Brand> {
	// @Override
	// public Brand deserialize(JsonParser p, DeserializationContext ctxt)
	// throws IOException {
	// JsonNode node = p.getCodec().readTree(p);
	// Long subCategoryId = node.get("id").asLong();
	// Brand existingSubCategory = entityManager.find(Brand.class,
	// subCategoryId);
	// if (existingSubCategory != null) {
	// return existingSubCategory;
	// } else {
	// return objectMapper.treeToValue(node, Brand.class);
	// }
	// }
	// }
	// private class AttractionDeserializer extends JsonDeserializer<Attraction>
	// {
	// @Override
	// public Attraction deserialize(JsonParser p, DeserializationContext ctxt)
	// throws IOException {
	// JsonNode node = p.getCodec().readTree(p);
	// Long subCategoryId = node.get("id").asLong();
	// Attraction existingSubCategory = entityManager
	// .find(Attraction.class, subCategoryId);
	// if (existingSubCategory != null) {
	// return existingSubCategory;
	// } else {
	// return objectMapper.treeToValue(node, Attraction.class);
	// }
	// }
	// }
	// private class CategoryDeserializer extends JsonDeserializer<Category> {
	// @Override
	// public Category deserialize(JsonParser p, DeserializationContext ctxt)
	// throws IOException {
	// JsonNode node = p.getCodec().readTree(p);
	// Long subCategoryId = node.get("id").asLong();
	// Category existingSubCategory = entityManager.find(Category.class,
	// subCategoryId);
	// if (existingSubCategory != null) {
	// return existingSubCategory;
	// } else {
	// return objectMapper.treeToValue(node, Category.class);
	// }
	// }
	// }
}
