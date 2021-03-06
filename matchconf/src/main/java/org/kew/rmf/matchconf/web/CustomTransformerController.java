/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.matchconf.web;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.kew.rmf.matchconf.Configuration;
import org.kew.rmf.matchconf.Transformer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@Controller
public class CustomTransformerController {

    // GET: transformer creation form for this configuration
    @RequestMapping(value="/{configType}_configs/{configName}/transformers", params = "form", produces = "text/html")
    public String createForm(@PathVariable("configType") String configType, @PathVariable("configName") String configName, Model uiModel,
                              @RequestParam(value = "className", required = false) String className,
                              @RequestParam(value = "packageName", required = false) String packageName) {
        Transformer instance = new Transformer();
        instance.setPackageName(packageName);
        instance.setClassName(className);
        populateEditForm(uiModel, configType, configName, instance);
        return "config_transformers/create";
    }

    // POST to create an object and add it to this configuration
    @RequestMapping(value="/{configType}_configs/{configName}/transformers", method = RequestMethod.POST, produces = "text/html")
    public String create(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @Valid Transformer transformer, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        // assert unique_together:config&name
        if (Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getTransformerForName(transformer.getName()) != null) {
            bindingResult.addError(new ObjectError("transformer.name", "There is already a Transformer set up for this configuration with this name."));
        }
        this.customValidation(configName, transformer, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configType, configName, transformer);
            return "config_transformers/create";
        }
        uiModel.asMap().clear();
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        transformer.setConfiguration(config);
        transformer.persist();
        config.getTransformers().add(transformer);
        config.merge();
        return "redirect:/{configType}_configs/" + encodeUrlPathSegment(configName, httpServletRequest) + "/transformers/" + encodeUrlPathSegment(transformer.getName(), httpServletRequest);
    }

    // GET indiviual transformer level
    @RequestMapping(value="/{configType}_configs/{configName}/transformers/{transformerName}", produces = "text/html")
    public String show(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("transformerName") String transformerName, Model uiModel) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        Transformer transformer = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getTransformerForName(transformerName);
        uiModel.addAttribute("transformer", transformer);
        uiModel.addAttribute("itemId", transformer.getName());
        uiModel.addAttribute("configName", configName);
        return "config_transformers/show";
    }

    // GET list of transformers for this config
    @RequestMapping(value="/{configType}_configs/{configName}/transformers", produces = "text/html")
    public String list(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        List<Transformer> transformers = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getTransformers();
        if (page != null || size != null) {
            int sizeNo = Math.min(size == null ? 10 : size.intValue(), transformers.size());
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("transformers", transformers.subList(firstResult, sizeNo));
            float nrOfPages = (float) Transformer.countTransformers() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("transformers", transformers);
        }
        uiModel.addAttribute("configName", configName);
        return "config_transformers/list";
    }

    // GET the update form for a specific transformer
    @RequestMapping(value="/{configType}_configs/{configName}/transformers/{transformerName}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("transformerName") String transformerName, Model uiModel) {
        Transformer transformer = Configuration.findConfigurationsByNameEquals(configName).getSingleResult().getTransformerForName(transformerName);
        populateEditForm(uiModel, configType, configName, transformer);
        return "config_transformers/update";
    }

    // PUT: update a specific transformer
    @RequestMapping(value="/{configType}_configs/{configName}/transformers", method = RequestMethod.PUT, produces = "text/html")
    public String update(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @Valid Transformer transformer, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        this.customValidation(configName, transformer, bindingResult);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, configType, configName, transformer);
            return "config_transformers/update";
        }
        uiModel.asMap().clear();
        transformer.setConfiguration(Configuration.findConfigurationsByNameEquals(configName).getSingleResult());
        transformer.merge();
        return "redirect:/{configType}_configs/" + encodeUrlPathSegment(configName, httpServletRequest) + "/transformers/" + encodeUrlPathSegment(transformer.getName(), httpServletRequest);
    }

    // DELETE a specific transformer from a collection's list
    @RequestMapping(value="/{configType}_configs/{configName}/transformers/{transformerName}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("transformerName") String transformerName, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) throws Exception {
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        config.removeTransformer(transformerName);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/{configType}_configs/" + configName.toString() + "/transformers/";
    }

    // DELETE by id
    @RequestMapping(value="/{configType}_configs/{configName}/transformers/delete-by-id/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String deleteById(@PathVariable("configType") String configType, @PathVariable("configName") String configName, @PathVariable("id") long id, Model uiModel) {
        Transformer toDelete = Transformer.findTransformer(id);
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        config.getTransformers().remove(toDelete);
        config.merge();
        return "redirect:/{configType}_configs/" + configName.toString() + "/transformers/";
    }

    void populateEditForm(Model uiModel,String configType, String configName, Transformer transformer) {
        uiModel.addAttribute("availableItems", LibraryScanner.availableItems());
        uiModel.addAttribute("transformer", transformer);
        uiModel.addAttribute("configName", configName);
        uiModel.addAttribute("configType", configType);
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }

    public void customValidation (String configName, Transformer transformer, BindingResult br) {
        if (!FieldValidator.validSlug(transformer.getName())) {
            br.addError(new ObjectError("transformer.name", "The name has to be set and can only contain ASCII letters and/or '-' and/or '_'"));
        }
    }

}
