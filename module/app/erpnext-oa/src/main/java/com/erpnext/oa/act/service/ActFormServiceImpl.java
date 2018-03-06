package com.erpnext.oa.act.service;

import javax.annotation.Resource;

import org.activiti.form.model.FormDefinition;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erpnext.framework.web.service.exception.InternalServerErrorException;
import com.erpnext.framework.web.util.AuthenticationUtils;
import com.erpnext.oa.act.domain.AbstractModel;
import com.erpnext.oa.act.domain.Model;
import com.erpnext.oa.act.dto.FormRepresentation;
import com.erpnext.oa.act.dto.FormSaveRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional(readOnly = true)
public class ActFormServiceImpl implements ActFormService {
	
	private Logger logger = LoggerFactory.getLogger(ActFormServiceImpl.class);

	@Resource
	private ModelService modelService;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public FormRepresentation getForm(String formId) {
		Model model = modelService.getModel(formId);
		FormRepresentation form = createFormRepresentation(model);
	    return form;
	}
	
	@Override
	@Transactional
	public FormRepresentation saveForm(String formId, FormSaveRepresentation saveRepresentation) {
		UserDetails user = AuthenticationUtils.getPrincipal();
		Model model = modelService.getModel(formId);
		
		String formKey = saveRepresentation.getFormRepresentation().getKey();
		
		model.setName(saveRepresentation.getFormRepresentation().getName());
	    model.setModelKey(formKey);
	    model.setDescription(saveRepresentation.getFormRepresentation().getDescription());

	    String editorJson = null;
	    try {
	      editorJson = objectMapper.writeValueAsString(saveRepresentation.getFormRepresentation().getFormDefinition());
	    } catch (Exception e) {
	      logger.error("Error while processing form json", e);
	      throw new InternalServerErrorException("Form could not be saved " + formId);
	    }

	    String filteredImageString = saveRepresentation.getFormImageBase64().replace("data:image/png;base64,", "");
	    byte[] imageBytes = Base64.decodeBase64(filteredImageString);
	    model = modelService.saveModel(model, editorJson, imageBytes, saveRepresentation.isNewVersion(), saveRepresentation.getComment(), user);
	    FormRepresentation result = new FormRepresentation(model);
	    result.setFormDefinition(saveRepresentation.getFormRepresentation().getFormDefinition());
	    return result;
		
	}

	private FormRepresentation createFormRepresentation(AbstractModel model) {
		FormDefinition formDefinition = null;
		try {
			formDefinition = objectMapper.readValue(model.getModelEditorJson(), FormDefinition.class);
		} catch (Exception e) {
			logger.error("Error deserializing form", e);
			throw new InternalServerErrorException("Could not deserialize form definition");
		}

		FormRepresentation result = new FormRepresentation(model);
		result.setFormDefinition(formDefinition);
		return result;
	}

}
