package com.tsh.library;

import java.util.Comparator;

import com.tsh.library.dto.TopicResponse;

public class TopicCompartor implements Comparator<TopicResponse> {

	@Override
	public int compare(TopicResponse o1, TopicResponse o2) {
		String gr1, gr2 = null;
		if (o1.getGrade().length() == 1)
			gr1 = "0" + o1.getGrade();
		else
			gr1 = o1.getGrade();

		if (o2.getGrade().length() == 1)
			gr2 = "0" + o2.getGrade();
		else
			gr2 = o2.getGrade();

		String tr1, tr2 = null;
		if (o1.getTerm().length() == 1)
			tr1 = "0" + o1.getTerm();
		else
			tr1 = o1.getTerm();

		if (o2.getTerm().length() == 1)
			tr2 = "0" + o2.getTerm();
		else
			tr2 = o2.getTerm();

		String wk1, wk2 = null;
		if (o1.getWeek().length() == 1)
			wk1 = "0" + o1.getWeek();
		else
			wk1 = o1.getWeek();

		if (o2.getWeek().length() == 1)
			wk2 = "0" + o2.getWeek();
		else
			wk2 = o2.getWeek();

		return (gr1 + o1.getCourse() + tr1 + wk1).compareTo(gr2 + o2.getCourse() + tr2 + wk2);
	}

}
