package com.alpha.interview.wizard.service.mall;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alpha.interview.wizard.model.mall.Category;
import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.model.mall.Shop;
import com.alpha.interview.wizard.repository.mall.MallModelRepository;
import com.alpha.interview.wizard.repository.mall.ShopRepository;

@Service
public class CsvService {

	@Autowired
	private MallModelRepository mallModelRepository;

	@Autowired
	private ShopRepository shopRepository;

	public void processCsvFile(MultipartFile file) {
		// System.out.println(file);
		MallModel mallModel = new MallModel(null, null);
		List<Shop> shops = new ArrayList<>();
		int i = 0;
		try (InputStreamReader reader = new InputStreamReader(
				file.getInputStream())) {
			// System.out.println(reader);
			Iterable<CSVRecord> records = CSVFormat.DEFAULT
					.withFirstRecordAsHeader().withDelimiter('|')
					.withAllowDuplicateHeaderNames()
					.withAllowMissingColumnNames().parse(reader);

			for (CSVRecord record : records) {
				Shop shop = new Shop(null, null);
				try {
					shop.setName(record.get(0));
					List<Category> categories = new ArrayList<Category>();

					Category category = new Category(null, null);
					category.setName(record.get("Category"));
					categories.add(category);
					shop.setCategories(categories);
					shop.setFounded(record.get("Founded"));
					// shop.setStarProducts(record.get("Star_Products"));
					// shop.setDelivery(record.get("Delivery"));
					shop.setLocation(record.get("Location"));
					shop.setHowToReach(record.get("How_to_Reach"));
					// shop.setSales(record.get("Sales"));
					try {
						shop.setAdditionalDetails(
								record.get("Additional_Details"));
					} catch (Exception ex1) {
						shop.setAdditionalDetails(record.get(8));
					}

				} catch (Exception ex) {
					// System.out.println("Row number " + i);
					ex.printStackTrace();
				}
				shops.add(shop);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mallModel.setShops(shops);
		// System.out.println(shops);
		shopRepository.saveAll(shops);
		// System.out.println(mallModel);
		mallModelRepository.save(mallModel);
	}
}
