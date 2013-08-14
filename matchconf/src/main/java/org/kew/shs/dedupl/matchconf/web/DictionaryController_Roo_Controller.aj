// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.kew.shs.dedupl.matchconf.web;

import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.kew.shs.dedupl.matchconf.Configuration;
import org.kew.shs.dedupl.matchconf.Dictionary;
import org.kew.shs.dedupl.matchconf.web.DictionaryController;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect DictionaryController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String DictionaryController.create(@Valid Dictionary dictionary, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, dictionary);
            return "dictionarys/create";
        }
        uiModel.asMap().clear();
        dictionary.persist();
        return "redirect:/dictionarys/" + encodeUrlPathSegment(dictionary.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String DictionaryController.createForm(Model uiModel) {
        populateEditForm(uiModel, new Dictionary());
        return "dictionarys/create";
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String DictionaryController.show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("dictionary", Dictionary.findDictionary(id));
        uiModel.addAttribute("itemId", id);
        return "dictionarys/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String DictionaryController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("dictionarys", Dictionary.findDictionaryEntries(firstResult, sizeNo));
            float nrOfPages = (float) Dictionary.countDictionarys() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("dictionarys", Dictionary.findAllDictionarys());
        }
        return "dictionarys/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String DictionaryController.update(@Valid Dictionary dictionary, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, dictionary);
            return "dictionarys/update";
        }
        uiModel.asMap().clear();
        dictionary.merge();
        return "redirect:/dictionarys/" + encodeUrlPathSegment(dictionary.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String DictionaryController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Dictionary.findDictionary(id));
        return "dictionarys/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String DictionaryController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Dictionary dictionary = Dictionary.findDictionary(id);
        dictionary.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/dictionarys";
    }
    
    void DictionaryController.populateEditForm(Model uiModel, Dictionary dictionary) {
        uiModel.addAttribute("dictionary", dictionary);
        uiModel.addAttribute("configurations", Configuration.findAllConfigurations());
    }
    
    String DictionaryController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
