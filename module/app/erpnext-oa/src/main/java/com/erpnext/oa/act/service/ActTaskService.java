package com.erpnext.oa.act.service;

import java.util.List;

import org.activiti.form.model.FormDefinition;

import com.erpnext.oa.act.dto.CreateProcessInstanceRepresentation;
import com.erpnext.oa.act.dto.TaskDTO;

public interface ActTaskService {
	
	void startProcessInstance(CreateProcessInstanceRepresentation startRequest);
	
	List<TaskDTO> getToDoTask(String userId);

	List<TaskDTO> getDoingTask(String userId);
	
	List<TaskDTO> getTasks(String userId);

	TaskDTO getOneTask(String taskId);
	
	List<TaskDTO> listTasks(String processInstanceId,String state);
	
	void completeTask(String taskId);
	
	FormDefinition getTaskForm(String taskId);
}
