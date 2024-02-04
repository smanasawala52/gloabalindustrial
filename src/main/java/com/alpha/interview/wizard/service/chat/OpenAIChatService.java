package com.alpha.interview.wizard.service.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alpha.interview.wizard.model.ChatRequestModel;
import com.alpha.interview.wizard.model.Message;
import com.alpha.interview.wizard.service.SectorTypeConstants;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import io.github.sashirestela.openai.domain.chat.ChatResponse;
import io.github.sashirestela.openai.domain.chat.Role;
import io.github.sashirestela.openai.domain.chat.message.ChatMsg;
import io.github.sashirestela.openai.domain.chat.message.ChatMsgAssistant;
import io.github.sashirestela.openai.domain.chat.message.ChatMsgSystem;
import io.github.sashirestela.openai.domain.chat.message.ChatMsgUser;
@Service
@Component("OpenAIChat")
public class OpenAIChatService implements ChatService {
	private String apiKey;
	protected SimpleOpenAI openAI;
	private String modelIdToUse = "davinci-002";
	private Map<SectorTypeConstants, ChatRequestModel> chatRequestModels = new HashMap<>();

	@Override
	public void initializeChat(String initSystemMessage, List<String> inputJson,
			SectorTypeConstants sector) {
		List<ChatMsg> chatRequestSession = new ArrayList<>();
		apiKey = System.getenv("API_KEY");
		openAI = SimpleOpenAI.builder().apiKey(apiKey).build();
		modelIdToUse = "gpt-3.5-turbo-1106";

		System.out.println("===================================");
		System.out.println(inputJson);
		System.out.println("===================================");
		System.out.println(inputJson.size());
		System.out.println("===================================");
		List<String> subLists = new ArrayList<>();
		int subListSize = 10;
		for (int i = 0; i < inputJson.size(); i += subListSize) {
			int endIndex = Math.min(i + subListSize, inputJson.size());
			subLists.add(String.join(",", inputJson.subList(i, endIndex)));
		}

		List<ChatMsg> messages = new ArrayList<>();
		for (String input : subLists) {
			System.out.println(input);
			ChatRequest chatRequestFirstTemp = ChatRequest.builder()
					.model(modelIdToUse)
					.message(new ChatMsgSystem(initSystemMessage))
					.message(new ChatMsgUser(input)).temperature(0.0)
					.maxTokens(300).build();

			CompletableFuture<Stream<ChatResponse>> futureChat = openAI
					.chatCompletions().createStream(chatRequestFirstTemp);
			Stream<ChatResponse> chatResponse = futureChat.join();
			List<String> response = chatResponse
					.filter(chatResp -> chatResp.firstContent() != null)
					.map(chatResp -> chatResp.firstContent())
					.collect(Collectors.toList());
			messages.addAll(chatRequestFirstTemp.getMessages().stream()
					.filter(m -> m.getRole() == Role.USER)
					.collect(Collectors.toList()));
		}
		List<ChatMsg> chatRequestFirst = new ArrayList<>(messages);
		try {
			messages = new ArrayList<>(chatRequestFirst);
			System.out.println("After adding Assistant: "
					+ (new ArrayList<>(chatRequestFirst)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		//
		ChatRequestModel chatRequestModel = new ChatRequestModel();
		chatRequestModel.setChatRequestFirst(chatRequestFirst);
		chatRequestModel.setChatRequestSession(chatRequestSession);
		chatRequestModels.put(sector, chatRequestModel);
		resetChatSession(sector);
	}

	@Override
	public void resetChatSession(SectorTypeConstants sector) {
		try {
			ChatRequestModel chatRequestModel = chatRequestModels.get(sector);
			System.out.println("Before adding User resetChatSession: "
					+ chatRequestModel.getChatRequestFirst());
			List<ChatMsg> messages = new ArrayList<>(
					chatRequestModel.getChatRequestFirst());
			chatRequestModel.setChatRequestSession(messages);
			System.out.println("After adding User resetChatSession: "
					+ chatRequestModel.getChatRequestSession());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Message getResponse(String input, SectorTypeConstants sector) {
		Message currentMessage = new Message();
		if (input != null && !input.isEmpty()) {
			currentMessage.setQuestion(input);
			ChatRequestModel chatRequestModel = chatRequestModels.get(sector);
			try {
				System.out.println("Before adding User: "
						+ chatRequestModel.getChatRequestSession());
				List<ChatMsg> messages = new ArrayList<>(
						chatRequestModel.getChatRequestSession());
				messages.add(new ChatMsgUser(input));
				chatRequestModel.setChatRequestSession(messages);
				System.out.println("After adding User: "
						+ chatRequestModel.getChatRequestSession());
			} catch (Exception e) {
				e.printStackTrace();
			}
			ChatRequest chatRequestSessionTemp = ChatRequest.builder()
					.model(modelIdToUse)
					.messages(chatRequestModel.getChatRequestSession())
					.temperature(0.0).maxTokens(300).build();
			CompletableFuture<ChatResponse> futureChat = openAI
					.chatCompletions().create(chatRequestSessionTemp);
			String chatResponseOpenA1 = futureChat.join().firstContent();
			currentMessage.setContent(chatResponseOpenA1);
			try {
				System.out.println("Before adding Assistant: "
						+ chatRequestModel.getChatRequestSession());
				List<ChatMsg> messages = new ArrayList<>(
						chatRequestModel.getChatRequestSession());
				messages.add(new ChatMsgAssistant(chatResponseOpenA1));
				chatRequestModel.setChatRequestSession(messages);
				System.out.println("After adding Assistant: "
						+ chatRequestModel.getChatRequestSession());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return currentMessage;
	}
}
