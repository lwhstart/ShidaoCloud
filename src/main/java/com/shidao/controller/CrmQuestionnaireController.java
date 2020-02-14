package com.shidao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shidao.model.CrmQuestionnaire;
import com.shidao.model.CrmQuestionnaire.QuestionnaireCategory;
import com.shidao.model.CrmQuestionnaireAnswer;
import com.shidao.model.CrmQuestionnaireQnswerSheet;
import com.shidao.model.CrmQuestionnaireQnswerSheet.AnswererType;
import com.shidao.model.CrmQuestionnaireQnswerSheet.RelatedCategory;
import com.shidao.model.CrmQuestionnaireQuestion;
import com.shidao.service.CrmQuestionnaireAnswerService;
import com.shidao.service.CrmQuestionnaireQnswerSheetService;
import com.shidao.service.CrmQuestionnaireQuestionService;
import com.shidao.service.CrmQuestionnaireService;
import com.shidao.util.JsonUtil;
import com.shidao.util.ShidaoException;

@RestController
@RequestMapping(value="/crmQuestionnaire")
public class CrmQuestionnaireController extends BaseController{

	@Autowired
	private CrmQuestionnaireQuestionService questionnaireQuestionService;
	
	@Autowired
	private CrmQuestionnaireService questionnaireService;
	
	@Autowired
	private CrmQuestionnaireQnswerSheetService questionnaireQnswerSheetService;
	
	@Autowired
	private CrmQuestionnaireAnswerService questionnaireAnswerService;
	
	@RequestMapping(value="/questionChoice/list/{sheetId}",method = RequestMethod.GET)
	public JSONObject getQuestionChoices(@PathVariable(value="sheetId") Integer sheetId){
		try {
			CrmQuestionnaireQuestion condition = new CrmQuestionnaireQuestion();
			CrmQuestionnaire questionnaire = new CrmQuestionnaire();
			questionnaire.setCategory(QuestionnaireCategory.Care);
			condition.setQuestionnaire(questionnaire);
			List<CrmQuestionnaireQuestion> questionnaireQuestion = questionnaireQuestionService.list(condition);
			questionnaireQuestion.forEach(p->{
				p.getChoice().forEach(ch ->{
					CrmQuestionnaireAnswer questionnaireAnswer = new CrmQuestionnaireAnswer();
					questionnaireAnswer.setQuestionId(p.getId());
					questionnaireAnswer.setAnswerSheetId(sheetId);
					questionnaireAnswer.setChoiceNumber(ch.getChoiceNumber());
					try {
						List<CrmQuestionnaireAnswer> questionnaireAnswers = questionnaireAnswerService.list(questionnaireAnswer);
						if (questionnaireAnswers != null && !questionnaireAnswers.isEmpty()) {
							ch.setIsChoice(true);
						}
					} catch (ShidaoException e) {
						_Logger.info("查询题目是否选中失败");
					}
				});
			});
			return JsonUtil.succeedJson(questionnaireQuestion);
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	//客户关怀问卷表
	@RequestMapping(value="/addSheet",method=RequestMethod.POST)
	public JSONObject addSheet(CrmQuestionnaireQnswerSheet questionnaireQnswerSheet){
		try {
			CrmQuestionnaireQnswerSheet sheetCondition = new CrmQuestionnaireQnswerSheet();
			sheetCondition.setRelatedId(questionnaireQnswerSheet.getRelatedId());
			List<CrmQuestionnaireQnswerSheet> crmQuestionnaireQnswerSheets = questionnaireQnswerSheetService.list(sheetCondition);
			if (crmQuestionnaireQnswerSheets != null && !crmQuestionnaireQnswerSheets.isEmpty()) {
				questionnaireQnswerSheet.setId(crmQuestionnaireQnswerSheets.get(0).getId());;
				questionnaireQnswerSheetService.updateByPrimaryKeySelective(questionnaireQnswerSheet);
				return JsonUtil.succeedJson(crmQuestionnaireQnswerSheets.get(0).getId());
			}
			if(questionnaireQnswerSheet.getRelatedCategory() == null)
				questionnaireQnswerSheet.setRelatedCategory(RelatedCategory.SdbsCustomerCare);
			
			if(questionnaireQnswerSheet.getAnswererType() == null)
				questionnaireQnswerSheet.setAnswererType(AnswererType.Employee);
			
			CrmQuestionnaire condition = new CrmQuestionnaire();
			condition.setTitle("客户关怀问卷表");
			List<CrmQuestionnaire> questionnaires = questionnaireService.list(condition);
			if(questionnaires!=null && !questionnaires.isEmpty())
				questionnaireQnswerSheet.setQuestionnaireId(questionnaires.get(0).getId());
			
			questionnaireQnswerSheetService.insertSelective(questionnaireQnswerSheet);
			
			return JsonUtil.succeedJson(questionnaireQnswerSheet.getId());			
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	
	@RequestMapping(value="/insert", method = RequestMethod.POST)
	public JSONObject insert(CrmQuestionnaireAnswer questionnaireAnswer){
		try {
			questionnaireAnswerService.insertSelective(questionnaireAnswer);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
	@RequestMapping(value="/delectByQuestionId" , method = RequestMethod.POST)
	public JSONObject delectByQuestionId(@RequestParam(value="questionId") Integer questionId,@RequestParam(value="answerSheetId")Integer answerSheetId,String choiceNumber){
		try {
			questionnaireAnswerService.delectByQuestionId(questionId,answerSheetId,choiceNumber);
			return JsonUtil.succeedJson();	
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
	
}
