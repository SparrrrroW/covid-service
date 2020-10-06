package io.boot.coronatracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import io.boot.coronatracker.bean.CoronaObject;
import io.boot.coronatracker.service.CoronaService;

@Controller
public class CoronaController {
	
	@Autowired
	CoronaService service;

	@GetMapping("/")
	public ModelAndView getHome() {
		ModelAndView mv = new ModelAndView();
		List<CoronaObject> locationStats = service.getTotalCasesList();
		int total = 0;
		for (CoronaObject object : locationStats) {
			total = total+ object.getLatestTotalCases();
		};
		mv.addObject("locationStats", locationStats);
		mv.addObject("totalCases", total);
		mv.setViewName("home");
		return mv;
	}
}
