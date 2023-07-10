package com.wip.service.course;

import com.github.pagehelper.PageInfo;
import com.wip.dto.cond.ContentCond;
import com.wip.model.CourseDomain;
import com.wip.model.MetaDomain;

import java.util.List;

public interface CourseService {
    /***
     * 添加文章
     * @param courseDomain
     */
    void addCourse(CourseDomain courseDomain);

    /**
     * 根据编号获取文章
     * @param cid
     * @return
     */
    CourseDomain getCourseById(Integer cid);

    /**
     * 更新文章
     * @param courseDomain
     */
    void updateCourseById(CourseDomain courseDomain);

    /**
     * 根据条件获取文章列表
     * @param contentCond
     * @param page
     * @param limit
     * @return
     */
    PageInfo<CourseDomain> getCourseByCond(ContentCond contentCond, int page, int limit);

    /**
     * 删除文章
     * @param cid
     */
    void deleteCourseById(Integer cid);

    /**
     * 添加文章点击量
     * @param courseDomain
     */
    void updateCourseByCid(CourseDomain courseDomain);

    /**
     * 通过分类获取文章
     * @param category
     * @return
     */
    List<CourseDomain> getCourseByCategory(String category);

    /**
     * 通过标签获取文章
     * @param tags
     * @return
     */
    List<CourseDomain> getCourseByTags(MetaDomain tags);
}
