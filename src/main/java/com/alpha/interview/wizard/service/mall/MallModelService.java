package com.alpha.interview.wizard.service.mall;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alpha.interview.wizard.controller.mall.util.MallUtil;
import com.alpha.interview.wizard.model.Message;
import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.repository.mall.MallModelRepository;
import com.alpha.interview.wizard.service.SectorService;
import com.alpha.interview.wizard.service.SectorTypeConstants;
import com.alpha.interview.wizard.service.chat.ChatService;
import com.alpha.interview.wizard.service.chat.ChatServiceMapInitializer;

@Service
@Component("MallModelService")
public class MallModelService implements SectorService {

	@Value("${chat.service.impl}")
	private String chatServiceImpl;

	private final Map<String, ChatService> chatServiceMap;
	@Autowired
	public MallModelService(ChatServiceMapInitializer serviceMapInitializer) {
		this.chatServiceMap = serviceMapInitializer.getServiceMap();
	}

	@Autowired
	private MallModelRepository mallModelRepository;

	public List<MallModel> getAllMallModels() {
		return mallModelRepository.findAll();
	}

	public String initializeChat(Map<String, String> queryParams) {
		Long mallId = Long.parseLong(queryParams.get("mallId"));
		Optional<MallModel> mallModelOptional = mallModelRepository
				.findById(mallId);
		if (mallModelOptional.isPresent()) {
			String initSystemMessage = "Answer in less than 20 words and in this JSON Format {'response_json': {'answer':'', "
					+ "'mallId':'', 'shopId':[], 'attractionId':[]}}.";
			List<String> input = MallUtil
					.initalizeChatInput(mallModelOptional.get());
			// mallModels.stream().forEach(
			// match -> input.addAll(MallUtil.initalizeChatInput(match)));
			chatServiceMap.get(chatServiceImpl)
					.initializeChat(initSystemMessage, input, getIdentity());
		}
		return "redirect:/mallmodel/";
	}
	@Override
	public Message getResponse(String input, Map<String, String> queryParams) {
		// construct proper Question
		StringBuilder queryJson = new StringBuilder();
		queryJson.append("{");
		queryJson.append("'response_json': {'answer':'', "
				+ "'mallId':'', 'shopId':[], 'attractionId':[], 'eventId':[],, 'parkingId':[], 'otherAttractionId':[]}");
		if (input != null && !input.isBlank()) {
			queryJson.append(",'question':'").append(input).append("'");
		}
		if (queryParams.containsKey("mallId")) {
			queryJson.append(",'mallId':'").append(queryParams.get("mallId"))
					.append("'");
		}
		if (queryParams.containsKey("lang")) {
			queryJson.append(",'lang':'").append(queryParams.get("lang"))
					.append("'");
		}
		if (queryParams.containsKey("answerMaxWords")) {
			queryJson.append(",'answerMaxWords':'")
					.append(queryParams.get("answerMaxWords")).append("'");
		} else {
			queryJson.append(",'answerMaxWords':'20'");
		}
		if (queryParams.containsKey("shopId")) {
			queryJson.append(",'shopId':'").append(queryParams.get("shopId"))
					.append("'");
		}
		if (queryParams.containsKey("attractionId")) {
			queryJson.append(",'attractionId':'")
					.append(queryParams.get("attractionId")).append("'");
		}
		if (queryParams.containsKey("eventId")) {
			queryJson.append(",'eventId':'").append(queryParams.get("eventId"))
					.append("'");
		}
		if (queryParams.containsKey("parkingId")) {
			queryJson.append(",'parkingId':'")
					.append(queryParams.get("parkingId")).append("'");
		}
		if (queryParams.containsKey("couponId")) {
			queryJson.append(",'couponId':'")
					.append(queryParams.get("couponId")).append("'");
		}
		if (queryParams.containsKey("brandId")) {
			queryJson.append(",'brandId':'").append(queryParams.get("brandId"))
					.append("'");
		}
		if (queryParams.containsKey("categoryId")) {
			queryJson.append(",'categoryId':'")
					.append(queryParams.get("categoryId")).append("'");
		}
		if (queryParams.containsKey("productId")) {
			queryJson.append(",'productId':'")
					.append(queryParams.get("productId")).append("'");
		}

		queryJson.append("}");
		System.out.println(queryJson.toString());
		return chatServiceMap.get(chatServiceImpl)
				.getResponse(queryJson.toString(), getIdentity());
	}

	@Override
	public void resetChatSession(Map<String, String> queryParams) {
		chatServiceMap.get(chatServiceImpl).resetChatSession(getIdentity());
	}
	@Override
	public SectorTypeConstants getIdentity() {
		return SectorTypeConstants.MALL_MODEL;
	}

}
