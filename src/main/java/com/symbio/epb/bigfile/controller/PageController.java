package com.symbio.epb.bigfile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * The<code>Class  PageController </code>
 *
 * @author benju.xie
 * @since 2018/8/10
 */
@Controller
public class PageController {

    @RequestMapping(value="/bigfile")
    @ResponseBody
    public ModelAndView bigFileIndex(ModelAndView model){
        model.setViewName("bigfile/index");
        return model;
    }

    @RequestMapping(value="/sitefile")
    @ResponseBody
    public ModelAndView siteFileIndex(ModelAndView model){
        model.setViewName("sitefile/index");
        return model;
    }
    
    @RequestMapping(value="/autoupload")
    @ResponseBody
    public ModelAndView autoUpload(ModelAndView model){
    	model.setViewName("autoupload/dashboard");
    	return model;
    }
    
    @RequestMapping(value="/login")
    @ResponseBody
    public ModelAndView loginPage(ModelAndView model){
    	model.setViewName("login");
    	return model;
    }



}
