package com.wip.controller.admin;

import com.github.pagehelper.PageInfo;
import com.wip.constant.LogActions;
import com.wip.constant.Types;
import com.wip.controller.BaseController;
import com.wip.dto.cond.ContentCond;
import com.wip.dto.cond.MetaCond;
import com.wip.model.CourseDomain;
import com.wip.model.MetaDomain;
import com.wip.service.course.CourseService;
import com.wip.service.log.LogService;
import com.wip.service.meta.MetaService;
import com.wip.utils.APIResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api("教程管理")
@Controller
@RequestMapping("/admin/course")
public class CourseController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private MetaService metaService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private LogService logService;

    @ApiOperation("教程页")
    @GetMapping(value = "")
    public String index(
            HttpServletRequest request,
            @ApiParam(name = "page", value = "页数", required = false)
            @RequestParam(name = "page", required = false, defaultValue = "1")
                    int page,
            @ApiParam(name = "limit", value = "每页数量", required = false)
            @RequestParam(name = "limit", required = false, defaultValue = "15")
                    int limit
    ) {
        PageInfo<CourseDomain> articles = courseService.getCourseByCond(new ContentCond(), page, limit);
        request.setAttribute("articles",articles);
        return "admin/course_list";
    }

    @ApiOperation("发布新教程页")
    @GetMapping(value = "/coursePublish")
    public String newArticle(HttpServletRequest request) {
        MetaCond metaCond = new MetaCond();
        metaCond.setType(Types.CATEGORY.getType());
        List<MetaDomain> metas = metaService.getMetas(metaCond);
        request.setAttribute("categories",metas);
        return "admin/course_edit";
    }

    @ApiOperation("教程编辑页")
    @GetMapping(value = "/{cid}")
    public String editCourse(
            @ApiParam(name = "cid", value = "教程编号", required = true)
            @PathVariable
                    Integer cid,
            HttpServletRequest request
    ) {
        CourseDomain courseDomain = courseService.getCourseById(cid);
        request.setAttribute("contents", courseDomain);
        MetaCond metaCond = new MetaCond();
        metaCond.setType(Types.CATEGORY.getType());
        List<MetaDomain> categories = metaService.getMetas(metaCond);
        request.setAttribute("categories", categories);
        request.setAttribute("active", "article");
        return "admin/course_edit";
    }

    @ApiOperation("编辑保存教程")
    @PostMapping("/modifyCourse")
    @ResponseBody
    public APIResponse modifyCourse(
            HttpServletRequest request,
            @ApiParam(name = "cid", value = "教程主键", required = true)
            @RequestParam(name = "cid", required = true)
                    Integer cid,
            @ApiParam(name = "title", value = "标题", required = true)
            @RequestParam(name = "title", required = true)
                    String title,
            @ApiParam(name = "titlePic", value = "标题图片", required = false)
            @RequestParam(name = "titlePic", required = false)
                    String titlePic,
            @ApiParam(name = "slug", value = "内容缩略名", required = false)
            @RequestParam(name = "slug", required = false)
                    String slug,
            @ApiParam(name = "content", value = "内容", required = true)
            @RequestParam(name = "content", required = true)
                    String content,
            @ApiParam(name = "type", value = "文章类型", required = true)
            @RequestParam(name = "type", required = true)
                    String type,
            @ApiParam(name = "status", value = "文章状态", required = true)
            @RequestParam(name = "status", required = true)
                    String status,
            @ApiParam(name = "tags", value = "标签", required = false)
            @RequestParam(name = "tags", required = false)
                    String tags,
            @ApiParam(name = "categories", value = "分类", required = false)
            @RequestParam(name = "categories", required = false, defaultValue = "默认分类")
                    String categories,
            @ApiParam(name = "allowComment", value = "是否允许评论", required = true)
            @RequestParam(name = "allowComment", required = true)
                    Boolean allowComment
    ) {
        CourseDomain courseDomain = new CourseDomain();
        courseDomain.setTitle(title);
        courseDomain.setCid(cid);
        courseDomain.setTitlePic(titlePic);
        courseDomain.setSlug(slug);
        courseDomain.setContent(content);
        courseDomain.setType(type);
        courseDomain.setStatus(status);
        courseDomain.setTags(tags);
        courseDomain.setCategories(categories);
        courseDomain.setAllowComment(allowComment ? 1: 0);
        courseService.updateCourseById(courseDomain);

        return APIResponse.success();
    }


    @ApiOperation("发布新教程")
    @PostMapping(value = "/coursePublish")
    @ResponseBody
    public APIResponse publishCourse(
            @ApiParam(name = "title", value = "标题", required = true)
            @RequestParam(name = "title", required = true)
                    String title,
            @ApiParam(name = "titlePic", value = "标题图片", required = false)
            @RequestParam(name = "titlePic", required = false)
                    String titlePic,
            @ApiParam(name = "slug", value = "内容缩略名", required = false)
            @RequestParam(name = "slug", required = false)
                    String slug,
            @ApiParam(name = "content", value = "内容", required = true)
            @RequestParam(name = "content", required = true)
                    String content,
            @ApiParam(name = "type", value = "文章类型", required = true)
            @RequestParam(name = "type", required = true)
                    String type,
            @ApiParam(name = "status", value = "文章状态", required = true)
            @RequestParam(name = "status", required = true)
                    String status,
            @ApiParam(name = "categories", value = "文章分类", required = false)
            @RequestParam(name = "categories", required = false, defaultValue = "默认分类")
                    String categories,
            @ApiParam(name = "tags", value = "文章标签", required = false)
            @RequestParam(name = "tags", required = false)
                    String tags,
            @ApiParam(name = "allowComment", value = "是否允许评论", required = true)
            @RequestParam(name = "allowComment", required = true)
                    Boolean allowComment
    ) {
        CourseDomain courseDomain = new CourseDomain();
        courseDomain.setTitle(title);
        courseDomain.setTitlePic(titlePic);
        courseDomain.setSlug(slug);
        courseDomain.setContent(content);
        courseDomain.setType(type);
        courseDomain.setStatus(status);
        courseDomain.setHits(1);
        courseDomain.setCommentsNum(0);
        // 只允许博客文章有分类，防止作品被收入分类
        courseDomain.setTags(type.equals(Types.ARTICLE.getType()) ? tags : null);
        courseDomain.setCategories(type.equals(Types.ARTICLE.getType()) ? categories : null);
        courseDomain.setAllowComment(allowComment ? 1 : 0);

        // 添加文章
        courseService.addCourse(courseDomain);

        return APIResponse.success();
    }

    @ApiOperation("删除教程")
    @PostMapping("/delete")
    @ResponseBody
    public APIResponse deleteCourse(
            @ApiParam(name = "cid", value = "教程ID", required = true)
            @RequestParam(name = "cid", required = true)
                    Integer cid,
            HttpServletRequest request
    ) {
        // 删除文章
        courseService.deleteCourseById(cid);
        // 写入日志
        logService.addLog(LogActions.DEL_ARTICLE.getAction(), cid+"",request.getRemoteAddr(),this.getUid(request));
        return APIResponse.success();
    }
}
