package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.CrmQuestionnaireAnswer;

public interface CrmQuestionnaireAnswerMapper extends IBatisMapper<CrmQuestionnaireAnswer> {

	void delectByQuestionId(@Param("questionId") Integer questionId,@Param("answerSheetId") Integer answerSheetId,@Param("choiceNumber") String choiceNumber);
}
