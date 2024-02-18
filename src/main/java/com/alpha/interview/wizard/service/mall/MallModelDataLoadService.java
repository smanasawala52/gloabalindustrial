package com.alpha.interview.wizard.service.mall;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alpha.interview.wizard.model.mall.Category;
import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.model.mall.Shop;
import com.alpha.interview.wizard.model.mall.SubCategory;
import com.alpha.interview.wizard.repository.mall.CategoryRepository;
import com.alpha.interview.wizard.repository.mall.MallModelRepository;
import com.alpha.interview.wizard.repository.mall.ShopRepository;
import com.alpha.interview.wizard.repository.mall.SubCategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component("MallModelDataLoadService")
@Service
public class MallModelDataLoadService {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MallModelRepository mallModelRepository;

	@Autowired
	private SubCategoryRepository subCategoryRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ShopRepository shopRepository;

	public void loadMallModelData(MultipartFile file) throws Exception {
		System.out.println("Multipart Dataloading started");
		try {
			String jsonArrayData = new String(file.getBytes());
			System.out.println("=============================");
			System.out.println(jsonArrayData);
			System.out.println("=============================");
			objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
			Type subCategoryListType = new TypeToken<List<MallModel>>() {
			}.getType();

			try {
				// Parse the JSON array string into a list of SubCategory objects
				List<MallModel> mallModels = new Gson().fromJson(jsonArrayData, subCategoryListType);
				for (MallModel mallModel : mallModels) {
					for (Shop shop : mallModel.getShops()) {
						for (Category category : shop.getCategories()) {
							for (SubCategory subCategory : category.getSubCategories()) {
								Optional<SubCategory> subCategoryOptional = subCategoryRepository
										.findById(subCategory.getId());
								if (subCategoryOptional.isPresent()) {
									subCategoryRepository.save(subCategory);
								}
							}

							Optional<Category> categoryOptional = categoryRepository.findById(category.getId());
							if (categoryOptional.isPresent()) {
								categoryRepository.save(category);
							}
						}
						Optional<Shop> shopOptional = shopRepository.findById(shop.getId());
						if (shopOptional.isPresent()) {
							shopRepository.save(shop);
						}
					}
				}

				// Now you have a List<SubCategory> containing your objects
				// You can iterate over the list or perform other operations as needed
//				for (MallModel mallModel : mallModels) {
//					System.out.println(mallModel);
//				}
//				JSONArray jsonArray = new JSONArray(jsonArrayData);
//
//				for (int i = 0; i < jsonArray.length(); i++) {
//					JSONObject jsonObject = jsonArray.getJSONObject(i);
//					JSONArray shopsArray = jsonObject.getJSONArray("shops");
//
//					for (int j = 0; j < shopsArray.length(); j++) {
//						JSONObject shopObject = shopsArray.getJSONObject(j);
//						JSONArray categoriesArray = shopObject.getJSONArray("categories");
//
//						for (int k = 0; k < categoriesArray.length(); k++) {
//							JSONObject categoryObject = categoriesArray.getJSONObject(k);
//							String categoryName = categoryObject.getString("name");
//							System.out.println("Category: " + categoryName);
//							System.out.println("SUb Category: " + categoryObject.getJSONArray("subCategories"));
//						}
//					}
//				}
//
//				List<MallModel> entities = Arrays.asList(objectMapper.readValue(jsonArrayData, MallModel[].class));
				System.out.println("=============================");
				System.out.println(mallModels);
				System.out.println("=============================");
				// mallModelRepository.saveAll(mallModels);
				// mallModelRepository.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
