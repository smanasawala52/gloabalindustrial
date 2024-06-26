package com.alpha.interview.wizard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

	@GetMapping("/")
	public String login(Model model) {
		model.addAttribute("contentTemplate", "mallModel");
		return "common";
	}
	@GetMapping("/homeMall")
	public String showHomeMall() {
		return "homeMall";
	}

	@GetMapping("/ipl")
	public String showHomeIPL() {
		return "homeIPL";
	}

	@GetMapping("/global")
	public String showHomeGlobalIndustrial() {
		return "homeGlobalIndustrial";
	}

	@GetMapping("/homeSendAudioUsingWebSocket")
	public String showHomeSendAudioUsingWebSocketPage() {
		return "homeSendAudioUsingWebSocket";
	}

	@GetMapping("/homeSendTextUsingWebSocket")
	public String showHomeSendTextUsingWebSocketPage() {
		return "homeSendTextUsingWebSocket";
	}

	@GetMapping("/homeFullAudioDemo")
	public String showHomeFullAudioDemoPage() {
		return "homeFullAudioDemo";
	}

	@GetMapping("/stream")
	public String showStreamPage() {
		return "stream";
	}

	@GetMapping("/audioVisualizer")
	public String showAudioVisualizerPage() {
		return "audioVisualizer";
	}
	@GetMapping("/audioVisualizer3d")
	public String showAudioVisualizer3dPage() {
		return "audioVisualizer3d";
	}
}
