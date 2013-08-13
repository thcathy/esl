package com.esl.service.group;

import java.util.*;

import com.esl.exception.*;
import com.esl.model.*;
import com.esl.model.group.*;
import com.esl.web.model.group.*;

public interface IMemberGroupResultService {
	// Main function
	public List<MemberGroupPracticeResult> listResults(MemberGroupPracticeResult result); 
	public Map<Long, Integer> getPositionMap(List<MemberGroupPracticeResult> list);
	
	// Schedule Job
	public void generatePracticeResult();
}
