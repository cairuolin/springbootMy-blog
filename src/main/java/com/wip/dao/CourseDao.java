package com.wip.dao;

import com.wip.dto.cond.ContentCond;
import com.wip.model.CourseDomain;
import com.wip.model.RelationShipDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教程相关Dao接口
 */
@Mapper
public interface CourseDao {
    /**
     * 添加教程
     * @param courseDomain
     */
    void addCourse(CourseDomain courseDomain);

    /**
     * 根据编号获取教程
     * @param cid
     * @return
     */
    CourseDomain getCourseById(Integer cid);

    /**
     * 更新教程
     * @param courseDomain
     */
    void updateCourseById(CourseDomain courseDomain);

    /**
     * 根据条件获取教程列表
     * @param contentCond
     * @return
     */
    List<CourseDomain> getCourseByCond(ContentCond contentCond);

    /**
     * 删除教程
     * @param cid
     */
    void deleteCourseById(Integer cid);

    /**
     * 获取教程总数
     * @return
     */
    Long getCourseCount();

    /**
     * 通过分类名获取教程
     * @param category
     * @return
     */
    List<CourseDomain> getCourseByCategory(@Param("category") String category);

    /**
     * 通过标签获取教程
     * @param cid
     * @return
     */
    List<CourseDomain> getCourseByTags(List<RelationShipDomain> cid);
}
