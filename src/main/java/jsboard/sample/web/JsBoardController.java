/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsboard.sample.web;

import java.util.List;

import egovframework.example.sample.service.EgovSampleService;
import egovframework.example.sample.service.SampleDefaultVO;
import egovframework.example.sample.service.SampleVO;

import egovframework.rte.fdl.property.EgovPropertyService;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import jsboard.sample.service.BoardDefaultVO;
import jsboard.sample.service.BoardService;
import jsboard.sample.service.BoardVO;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springmodules.validation.commons.DefaultBeanValidator;

/**
 * @Class Name : JsBoardController.java
 * @Description : JsBoard Controller Class
 * @Modification Information
 * @
 * @  수정일      수정자              수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2024.09.04           최초생성
 *
 * @author 강준수
 * @since 2024. 09.04
 * @version 1.0
 * @see
 *
 *  Copyright (C) by MOPAS All right reserved.
 */

@Controller
public class JsBoardController {

	/** BoardService */
	@Resource(name = "boardServiceTwo")
	private BoardService boardServiceTwo;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	/** Validator */
	@Resource(name = "beanValidator")
	protected DefaultBeanValidator beanValidator;

	/**
	 * 글 목록을 조회한다. (pageing)
	 * @param searchVO - 조회할 정보가 담긴 BoardDefaultVO
	 * @param model
	 * @return "jsboardList"
	 * @exception Exception
	 */
	@RequestMapping(value = "/jsBoardList.do")
	public String selectSampleList(@ModelAttribute("searchVO") BoardDefaultVO searchVO, ModelMap model) throws Exception {

		/** EgovPropertyService.sample */
		searchVO.setPageUnit(propertiesService.getInt("pageUnit"));
		searchVO.setPageSize(propertiesService.getInt("pageSize"));

		/** pageing setting */
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(searchVO.getPageUnit());
		paginationInfo.setPageSize(searchVO.getPageSize());

		searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		searchVO.setLastIndex(paginationInfo.getLastRecordIndex());
		searchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		List<?> boardList = boardServiceTwo.selectBoardList(searchVO);
		model.addAttribute("resultList", boardList);

		int totCnt = boardServiceTwo.selectBoardListTotCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("paginationInfo", paginationInfo);

		return "board/jsboardList";
	}

	/**
	 * 글 등록 화면을 조회한다.
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param model
	 * @return "jsboardRegister"
	 * @exception Exception
	 */
	@RequestMapping(value = "/addJsboard.do", method = RequestMethod.GET)
	public String addSampleView(@ModelAttribute("searchVO") BoardDefaultVO searchVO, Model model) throws Exception {
		model.addAttribute("boardVO", new BoardVO());
		return "board/jsboardRegister";
	}

	/**
	 * 글을 등록한다.
	 * @param boardVO - 등록할 정보가 담긴 VO
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return "forward:/jsBoardList.do"
	 * @exception Exception
	 */
	@RequestMapping(value = "/addJsboard.do", method = RequestMethod.POST)
	public String addSample(@ModelAttribute("searchVO") BoardDefaultVO searchVO, BoardVO boardVO, BindingResult bindingResult, Model model, SessionStatus status)
			throws Exception {

		// Server-Side Validation
		beanValidator.validate(boardVO, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("boardVO", boardVO);
			return "board/jsboardRegister";
		}

		boardServiceTwo.insertBoard(boardVO);
		status.setComplete();
		return "forward:/jsBoardList.do";
		//return "redirect:/jsBoardList.do";
	}

	/**
	 * 글 수정화면을 조회한다.
	 * @param id - 수정할 글 id
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param model
	 * @return "jsboardRegister"
	 * @exception Exception
	 */
	@RequestMapping("/updateJsboardView.do")
	public String updateSampleView(@RequestParam("selectedId") String id, @ModelAttribute("searchVO") BoardDefaultVO searchVO, Model model) throws Exception {
		BoardVO boardVO = new BoardVO();
		boardVO.setId(id);
		// 변수명은 CoC 에 따라 sampleVO
		model.addAttribute(selectBoard(boardVO, searchVO));
		return "board/jsboardRegister";
	}

	/**
	 * 글을 조회한다.
	 * @param boardVO - 조회할 정보가 담긴 VO
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return @ModelAttribute("boardVO") - 조회한 정보
	 * @exception Exception
	 */
	public BoardVO selectBoard(BoardVO boardVO, @ModelAttribute("searchVO") BoardDefaultVO searchVO) throws Exception {
		return boardServiceTwo.selectBoard(boardVO);
	}

	/**
	 * 글을 수정한다.
	 * @param boardVO - 수정할 정보가 담긴 VO
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return "forward:/jsBoardList.do"
	 * @exception Exception
	 */
	@RequestMapping("/updateJsboard.do")
	public String updateSample(@ModelAttribute("searchVO") BoardDefaultVO searchVO, BoardVO boardVO, BindingResult bindingResult, Model model, SessionStatus status)
			throws Exception {

		beanValidator.validate(boardVO, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("boardVO", boardVO);
			return "board/jsboardRegister";
		}

		boardServiceTwo.updateBoard(boardVO);
		status.setComplete();
		return "forward:/jsBoardList.do";
	}

	/**
	 * 글을 삭제한다.
	 * @param boardVO - 삭제할 정보가 담긴 VO
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return "forward:/jsBoardList.do"
	 * @exception Exception
	 */
	@RequestMapping("/deleteJsboard.do")
	public String deleteSample(BoardVO boardVO, @ModelAttribute("searchVO") BoardDefaultVO searchVO, SessionStatus status) throws Exception {
		boardServiceTwo.deleteBoard(boardVO);
		status.setComplete();
		return "forward:/jsBoardList.do";
	}

}
