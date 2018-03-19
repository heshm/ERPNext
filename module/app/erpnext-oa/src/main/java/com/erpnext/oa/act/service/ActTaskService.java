package com.erpnext.oa.act.service;

import java.util.List;


import com.erpnext.oa.act.dto.CreateProcessInstanceRepresentation;
import com.erpnext.oa.act.dto.TaskDTO;

public interface ActTaskService {
	
	void startProcessInstance(CreateProcessInstanceRepresentation startRequest);
	
	List<TaskDTO> getToDoTask(String userId);

	List<TaskDTO> getDoingTask(String userId);
}
