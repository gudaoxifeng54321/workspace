package com.ly.sys.common.web.controller;

import java.io.Serializable;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ly.sys.common.Constants;
import com.ly.sys.common.bind.annotation.PageableDefaults;
import com.ly.sys.common.entity.AbstractEntity;
import com.ly.sys.common.entity.search.Searchable;
import com.ly.sys.common.service.BaseService;
import com.ly.sys.common.web.controller.permission.PermissionList;

/**
 * 基础CRUD 控制器
 * <p>
 * User: mtwu
 * <p>
 * Date: 13-2-23 下午1:20
 * <p>
 * Version: 1.0
 */
public abstract class BaseCRUDController<M extends AbstractEntity, ID extends Serializable> extends BaseController<M, ID> {
  @Autowired
  protected BaseService<M, ID> baseService;

  private boolean              listAlsoSetCommonData = false;

  protected PermissionList     permissionList        = null;

  /**
   * 设置基础service
   * 
   * @param baseService
   */
  
  public void setBaseService(BaseService<M, ID> baseService) {
    this.baseService = baseService;
  }

  /**
   * 列表也设置common data
   */
  public void setListAlsoSetCommonData(boolean listAlsoSetCommonData) {
    this.listAlsoSetCommonData = listAlsoSetCommonData;
  }

  /**
   * 权限前缀：如sys:user 则生成的新增权限为 sys:user:create
   */
  public void setResourceIdentity(String resourceIdentity) {
    if (!StringUtils.isEmpty(resourceIdentity)) {
      permissionList = PermissionList.newPermissionList(resourceIdentity);
    }
  }

  

    @RequestMapping(method = RequestMethod.GET)
    @PageableDefaults(sort = "id=desc")
    public String list(Searchable searchable, Model model) {

        if (permissionList != null) {
            this.permissionList.assertHasViewPermission();
        }
        model.addAttribute("page", baseService.findPageList(searchable));
        if (listAlsoSetCommonData) {
            setCommonData(model);
        }
        return viewName("list");
    }

  /**
   * 仅返回表格数据
   * 
   * @param searchable
   * @param model
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, headers = "table=true")
  @PageableDefaults(sort = "id=desc")
  public String listTable(Searchable searchable, Model model) {
    list(searchable, model);
    return viewName("listTable");
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public String view(Model model, @PathVariable("id") ID id) {

    if (permissionList != null) {
      this.permissionList.assertHasViewPermission();
    }

    setCommonData(model);
    M m = baseService.findById(id);
    model.addAttribute("m", m);
    model.addAttribute(Constants.OP_NAME, "查看");
    return viewName("editForm");
  }

  @RequestMapping(value = "create", method = RequestMethod.GET)
  public String showCreateForm(Model model) {

    if (permissionList != null) {
      this.permissionList.assertHasCreatePermission();
    }

    setCommonData(model);
    model.addAttribute(Constants.OP_NAME, "新增");
    if (!model.containsAttribute("m")) {
      model.addAttribute("m", newModel());
    }
    return viewName("editForm");
  }

  @RequestMapping(value = "create", method = RequestMethod.POST)
  public String create(Model model, @Valid @ModelAttribute("m") M m, BindingResult result, RedirectAttributes redirectAttributes) {

    if (permissionList != null) {
      this.permissionList.assertHasCreatePermission();
    }

    if (hasError(m, result)) {
      return showCreateForm(model);
    }
    baseService.add(m);
    redirectAttributes.addFlashAttribute(Constants.MESSAGE, "新增成功");
    return redirectToUrl(null);
  }

  @RequestMapping(value = "{id}/update", method = RequestMethod.GET)
  public String showUpdateForm(@PathVariable("id") ID id, Model model) {

    if (permissionList != null) {
      this.permissionList.assertHasUpdatePermission();
    }

    setCommonData(model);
    model.addAttribute(Constants.OP_NAME, "修改"); 
    M m = baseService.findById(id);
    model.addAttribute("m", m);
    return viewName("editForm");
  }

  @RequestMapping(value = "{id}/update", method = RequestMethod.POST)
  public String update(Model model,@Valid @ModelAttribute("m") M m, BindingResult result,
      @RequestParam(value = Constants.BACK_URL, required = false) String backURL, RedirectAttributes redirectAttributes) {

    if (permissionList != null) {
      this.permissionList.assertHasUpdatePermission();
    }
    if (hasError(m, result)) {
      return showUpdateForm((ID)m.getId(), model);
    }
    baseService.update(m);
    redirectAttributes.addFlashAttribute(Constants.MESSAGE, "修改成功");
    return redirectToUrl(backURL);
  }

  @RequestMapping(value = "{id}/delete", method = RequestMethod.GET)
  public String showDeleteForm(@PathVariable("id") ID id, Model model) {

    if (permissionList != null) {
      this.permissionList.assertHasDeletePermission();
    }

    setCommonData(model);
    model.addAttribute(Constants.OP_NAME, "删除");
    M m=baseService.findById(id);
    model.addAttribute("m", m);
    return viewName("editForm");
  }

  @RequestMapping(value = "{id}/delete", method = RequestMethod.POST)
  public String delete(@PathVariable("id") ID id, @RequestParam(value = Constants.BACK_URL, required = false) String backURL,
      RedirectAttributes redirectAttributes) {

    if (permissionList != null) {
      this.permissionList.assertHasDeletePermission();
    }

    baseService.delete(id);

    redirectAttributes.addFlashAttribute(Constants.MESSAGE, "删除成功");
    return redirectToUrl(backURL);
  }

  @RequestMapping(value = "batch/delete", method = { RequestMethod.GET, RequestMethod.POST })
  public String deleteInBatch(@RequestParam(value = "ids", required = false) ID[] ids,
      @RequestParam(value = Constants.BACK_URL, required = false) String backURL, RedirectAttributes redirectAttributes) {

    if (permissionList != null) {
      this.permissionList.assertHasDeletePermission();
    }
    baseService.delete(ids);

    redirectAttributes.addFlashAttribute(Constants.MESSAGE, "删除成功");
    return redirectToUrl(backURL);
  }

}