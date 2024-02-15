package com.alpha.interview.wizard.service.mall;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alpha.interview.wizard.model.Message;
import com.alpha.interview.wizard.model.mall.MallModel;
import com.alpha.interview.wizard.repository.mall.MallModelRepository;
import com.alpha.interview.wizard.service.SectorService;
import com.alpha.interview.wizard.service.SectorTypeConstants;
import com.alpha.interview.wizard.service.chat.ChatService;
import com.alpha.interview.wizard.service.chat.ChatServiceMapInitializer;

@Service
@Component("MallService")
public class MallService implements SectorService {

	@Value("${chat.service.impl}")
	private String chatServiceImpl;

	private final Map<String, ChatService> chatServiceMap;
	@Autowired
	public MallService(ChatServiceMapInitializer serviceMapInitializer) {
		this.chatServiceMap = serviceMapInitializer.getServiceMap();
	}

	@Autowired
	private MallModelRepository mallModelRepository;

	public List<MallModel> getAllMallModels() {
		return mallModelRepository.findAll();
	}

	public String initializeChat() {
		List<MallModel> mallModels = mallModelRepository.findAll();
		if (mallModels.isEmpty()) {
			return "redirect:/mall/csv/upload-success";
		}
		String initSystemMessage = "This Chat will Read all Mall Raw Data and "
				+ "will provide answers to users questions in an interactive session manner.";
		List<String> input = new ArrayList<String>();
		mallModels.stream().forEach(match -> input.add(String.valueOf(match)));
		chatServiceMap.get(chatServiceImpl).initializeChat(initSystemMessage,
				input, SectorTypeConstants.MALL);
		return "redirect:/";
	}
	@Override
	public Message getResponse(String input) {
		return chatServiceMap.get(chatServiceImpl).getResponse(input,
				getIdentity());
	}

	@Override
	public void resetChatSession() {
		chatServiceMap.get(chatServiceImpl).resetChatSession(getIdentity());
	}
	@Override
	public SectorTypeConstants getIdentity() {
		return SectorTypeConstants.MALL;
	}

}
