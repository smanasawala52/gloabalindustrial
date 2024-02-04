package com.alpha.interview.wizard.service.ipl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alpha.interview.wizard.model.Message;
import com.alpha.interview.wizard.model.ipl.Match;
import com.alpha.interview.wizard.model.ipl.MatchBuilder;
import com.alpha.interview.wizard.model.ipl.MatchInputJson;
import com.alpha.interview.wizard.model.ipl.Venue;
import com.alpha.interview.wizard.service.SectorService;
import com.alpha.interview.wizard.service.SectorTypeConstants;
import com.alpha.interview.wizard.service.chat.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

@Component("IPLService")
@Service
public class IPLDataLoadService implements SectorService {
	private List<Match> matches = new CopyOnWriteArrayList<>();
	private List<Venue> venues = new CopyOnWriteArrayList<>();
	private String dirLocation = "ipl_json";

	public List<Match> getMatches() {
		return matches;
	}

	public void setMatches(List<Match> matches) {
		this.matches = matches;
	}

	@Autowired
	private ObjectMapper objectMapper;

	@PostConstruct
	public void loadMatchData() throws Exception {
		System.out.println("Dataloading started");

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("M/d/yyyy");
		try {
			List<File> files = Files.list(Paths.get(dirLocation))
					.map(Path::toFile).collect(Collectors.toList());
			objectMapper.setPropertyNamingStrategy(
					PropertyNamingStrategy.SNAKE_CASE);

			for (File file : files) {
				try {
					MatchInputJson value = objectMapper.readValue(
							new File(file.getPath()), MatchInputJson.class);
					System.out.println(value);
					Match match = new MatchBuilder()
							.setId(Long.valueOf(
									file.getName().replace(".json", "")))
							.setCity(value.getInfo().getCity())
							.setDate(Instant
									.ofEpochMilli(value.getInfo().getDates()
											.get(0).getTime())
									.atZone(ZoneId.systemDefault())
									.toLocalDate())
							.setPlayerOfMatch(
									value.getInfo().getPlayerOfMatch())
							.setEvent(value.getInfo().getEvent())
							.setOutcome(value.getInfo().getOutcome())
							.setTeam1(value.getInfo().getTeams().get(0))
							.setTeam2(value.getInfo().getTeams().get(1))
							.setVenue(value.getInfo().getVenue())
							.setTossDecision(
									value.getInfo().getToss().getDecision())
							.setTossWinner(
									value.getInfo().getToss().getWinner())
							.setOfficials(value.getInfo().getOfficials())
							.build();
					getMatches().add(match);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.sort(getMatches());
		if (!matches.isEmpty()) {
			initializeChat();
		}
	}

	public List<Venue> getVenues() {
		return venues;
	}

	public MatchInputJson getMatch(Long key) {
		try {
			objectMapper.setPropertyNamingStrategy(
					PropertyNamingStrategy.SNAKE_CASE);

			MatchInputJson value = objectMapper.readValue(
					new File(dirLocation + "/" + key + ".json"),
					MatchInputJson.class);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Autowired
	@Qualifier("OpenAIChat")
	private ChatService chatService;

	@Override
	public String initializeChat() {
		String initSystemMessage = "This Chat will Read all IPL Matches Raw Data and "
				+ "will provide answers to users questions in an interactive session manner.";
		List<String> input = new ArrayList<String>();
		matches.stream().forEach(match -> input.add(String.valueOf(match)));

		chatService.initializeChat(initSystemMessage, input,
				SectorTypeConstants.IPL);
		return "redirect:/ipl";
	}
	@Override
	public Message getResponse(String input) {
		return chatService.getResponse(input, getIdentity());
	}

	@Override
	public void resetChatSession() {
		chatService.resetChatSession(getIdentity());
	}

	@Override
	public SectorTypeConstants getIdentity() {
		// TODO Auto-generated method stub
		return SectorTypeConstants.IPL;
	}
}
