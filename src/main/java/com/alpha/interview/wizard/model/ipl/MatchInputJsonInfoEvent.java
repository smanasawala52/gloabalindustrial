package com.alpha.interview.wizard.model.ipl;

import com.alpha.interview.wizard.controller.mall.util.MallUtil;

public class MatchInputJsonInfoEvent {
	private String name;
	private String matchNumber;
	private String group;
	private String stage;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = MallUtil.formatName(name);
	}

	public String getMatchNumber() {
		return matchNumber;
	}

	public void setMatchNumber(String matchNumber) {
		this.matchNumber = matchNumber;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	@Override
	public String toString() {
		return "MatchInputJsonInfoEvent [name=" + name + ", matchNumber="
				+ matchNumber + ", group=" + group + ", stage=" + stage + "]";
	}
}
