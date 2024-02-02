package com.alpha.interview.wizard.service.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alpha.interview.wizard.model.MallModel;
import com.alpha.interview.wizard.model.Message;
import com.alpha.interview.wizard.service.mall.MallService;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import io.github.sashirestela.openai.domain.chat.ChatResponse;
import io.github.sashirestela.openai.domain.chat.message.ChatMsg;
import io.github.sashirestela.openai.domain.chat.message.ChatMsgAssistant;
import io.github.sashirestela.openai.domain.chat.message.ChatMsgSystem;
import io.github.sashirestela.openai.domain.chat.message.ChatMsgUser;
@Service
@Component("OpenAIChat")
public class OpenAIChatService implements ChatService {
	private String apiKey;
	protected SimpleOpenAI openAI;
	private String modelIdToUse = "gpt-3.5-turbo-1106";
	private final MallService mallService;
	private ChatRequest chatRequestFirst;
	private ChatRequest chatRequestSession;

	@Autowired
	public OpenAIChatService(MallService mallService) {
		this.mallService = mallService;
	}

	@Override
	public String initializeChat() {
		List<MallModel> mallModels = mallService.getAllMallModels();
		if (mallModels.isEmpty()) {
			return "redirect:/csv/upload-success";
		}
		String mallModelJson = mallModels.toString();
		apiKey = System.getenv("API_KEY");
		openAI = SimpleOpenAI.builder().apiKey(apiKey).build();
		modelIdToUse = "gpt-3.5-turbo-1106";

		chatRequestFirst = ChatRequest.builder().model(modelIdToUse).message(
				new ChatMsgSystem("This Chat will Read all Mall Raw Data and "
						+ "will provide answers to users questions in an interactive session manner."))
				.message(new ChatMsgUser(mallModelJson)).temperature(0.0)
				.maxTokens(300).build();

		CompletableFuture<Stream<ChatResponse>> futureChat = openAI
				.chatCompletions().createStream(chatRequestFirst);
		Stream<ChatResponse> chatResponse = futureChat.join();
		List<String> response = chatResponse
				.filter(chatResp -> chatResp.firstContent() != null)
				.map(chatResp -> chatResp.firstContent())
				.collect(Collectors.toList());
		// System.out.println(String.join(",", response));
		try {
			System.out.println("Before adding Assistant: "
					+ chatRequestFirst.getMessages());
			// chatRequest.getMessages()
			// .add(new ChatMsgAssistant(String.join(",", response)));
			List<ChatMsg> messages = new ArrayList<>(
					chatRequestFirst.getMessages());
			messages.add(new ChatMsgAssistant(String.join(",", response)));
			chatRequestFirst = ChatRequest.builder().model(modelIdToUse)
					.messages(messages).temperature(0.0).maxTokens(300).build();
			System.out.println("After adding Assistant: "
					+ chatRequestFirst.getMessages());
		} catch (Exception e) {
			e.printStackTrace();
		}

		//
		resetChatSession();
		return "redirect:/";
	}

	public void resetChatSession() {
		try {
			System.out.println(
					"Before adding User: " + chatRequestFirst.getMessages());
			// chatRequest.getMessages().add(new ChatMsgUser(input));
			List<ChatMsg> messages = new ArrayList<>(
					chatRequestFirst.getMessages());
			chatRequestSession = ChatRequest.builder().model(modelIdToUse)
					.messages(messages).temperature(0.0).maxTokens(300).build();
			System.out.println(
					"After adding User: " + chatRequestSession.getMessages());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Message getResponse(String input) {
		Message currentMessage = new Message();
		if (input != null && !input.isEmpty()) {
			currentMessage.setQuestion(input);
			try {
				System.out.println("Before adding User: "
						+ chatRequestSession.getMessages());
				// chatRequest.getMessages().add(new ChatMsgUser(input));
				List<ChatMsg> messages = new ArrayList<>(
						chatRequestSession.getMessages());
				messages.add(new ChatMsgUser(input));
				chatRequestSession = ChatRequest.builder().model(modelIdToUse)
						.messages(messages).temperature(0.0).maxTokens(300)
						.build();
				System.out.println("After adding User: "
						+ chatRequestSession.getMessages());
			} catch (Exception e) {
				e.printStackTrace();
			}
			CompletableFuture<ChatResponse> futureChat = openAI
					.chatCompletions().create(chatRequestSession);
			String chatResponseOpenA1 = futureChat.join().firstContent();
			// System.out.println(chatResponseOpenA1);
			currentMessage.setContent(chatResponseOpenA1);
			try {
				System.out.println("Before adding Assistant: "
						+ chatRequestSession.getMessages());
				List<ChatMsg> messages = new ArrayList<>(
						chatRequestSession.getMessages());
				messages.add(new ChatMsgAssistant(chatResponseOpenA1));
				chatRequestSession = ChatRequest.builder().model(modelIdToUse)
						.messages(messages).temperature(0.0).maxTokens(300)
						.build();
				// chatRequest.getMessages()
				// .add(new ChatMsgAssistant(chatResponse.firstContent()));
				System.out.println("After adding Assistant: "
						+ chatRequestSession.getMessages());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// return chatResponse.firstContent();
		}
		return currentMessage;
	}
}
