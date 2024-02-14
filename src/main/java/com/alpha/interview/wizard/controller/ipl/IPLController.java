package com.alpha.interview.wizard.controller.ipl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alpha.interview.wizard.service.ipl.IPLDataLoadService;

@Controller
@RequestMapping("/ipl")
public class IPLController {

	@Autowired
	private IPLDataLoadService iplService;

	@GetMapping("/loadData")
	public String showUploadForm() {
		try {
			iplService.loadMatchData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/ipl";
	}
}
