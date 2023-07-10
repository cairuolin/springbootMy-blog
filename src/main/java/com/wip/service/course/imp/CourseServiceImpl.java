package com.wip.service.course.imp;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wip.constant.ErrorConstant;
import com.wip.constant.Types;
import com.wip.constant.WebConst;
import com.wip.dao.CommentDao;
import com.wip.dao.CourseDao;
import com.wip.dao.RelationShipDao;
import com.wip.dto.cond.ContentCond;
import com.wip.exception.BusinessException;
import com.wip.model.CommentDomain;
import com.wip.model.CourseDomain;
import com.wip.model.MetaDomain;
import com.wip.model.RelationShipDomain;
import com.wip.service.course.CourseService;
import com.wip.service.meta.MetaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseDao courseDao;

    @Autowired
    private MetaService metaService;

    @Autowired
    private RelationShipDao relationShipDao;

    @Autowired
    private CommentDao commentDao;

    @Override
    @Transactional
    @CacheEvict(value = {"courseCache", "courseCaches"}, allEntries = true, beforeInvocation = true)
    public void addCourse(CourseDomain courseDomain) {
        if (null == courseDomain)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);

        if (StringUtils.isBlank(courseDomain.getTitle()))
            throw BusinessException.withErrorCode(ErrorConstant.Article.TITLE_CAN_NOT_EMPTY);

        if (courseDomain.getTitle().length() > WebConst.MAX_TITLE_COUNT)
            throw BusinessException.withErrorCode(ErrorConstant.Article.TITLE_IS_TOO_LONG);

        if (StringUtils.isBlank(courseDomain.getContent()))
            throw BusinessException.withErrorCode(ErrorConstant.Article.CONTENT_CAN_NOT_EMPTY);

        if (courseDomain.getContent().length() > WebConst.MAX_CONTENT_COUNT)
            throw BusinessException.withErrorCode(ErrorConstant.Article.CONTENT_IS_TOO_LONG);

        // 取到标签和分类
        String tags = courseDomain.getTags();
        String categories = courseDomain.getCategories();

        // 添加文章
        courseDao.addCourse(courseDomain);

        // 添加分类和标签
        int cid = courseDomain.getCid();
        metaService.addMetas(cid, tags, Types.TAG.getType());
        metaService.addMetas(cid, categories, Types.CATEGORY.getType());
    }

    @Override
    @Cacheable(value = "courseCache", key = "'courseById_' + #p0")
    public CourseDomain getCourseById(Integer cid) {
        if (null == cid)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        return courseDao.getCourseById(cid);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"courseCache", "courseCaches"}, allEntries = true, beforeInvocation = true)
    public void updateCourseById(CourseDomain courseDomain) {
        // 标签和分类
        String tags = courseDomain.getTags();
        String categories = courseDomain.getCategories();

        // 更新文章
        courseDao.updateCourseById(courseDomain);
        int cid = courseDomain.getCid();
        relationShipDao.deleteRelationShipByCid(cid);
        metaService.addMetas(cid,tags,Types.TAG.getType());
        metaService.addMetas(cid,categories,Types.CATEGORY.getType());
    }

    @Override
    @Cacheable(value = "courseCaches", key = "'courseByCond_' + #p1 + 'type_' + #p0.type")
    public PageInfo<CourseDomain> getCourseByCond(ContentCond contentCond, int pageNum, int pageSize) {
        if (null == contentCond)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        PageHelper.startPage(pageNum,pageSize);
        List<CourseDomain> courses = courseDao.getCourseByCond(contentCond);
        PageInfo<CourseDomain> pageInfo = new PageInfo<>(courses);
        return pageInfo;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"courseCache","courseCaches"},allEntries = true, beforeInvocation = true)
    public void deleteCourseById(Integer cid) {
        if (null == cid)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        // 删除文章
        courseDao.deleteCourseById(cid);

        // 同时要删除该 文章下的所有评论
        List<CommentDomain> comments = commentDao.getCommentByCId(cid);
        if (null != comments && comments.size() > 0) {
            comments.forEach(comment -> {
                commentDao.deleteComment(comment.getCoid());
            });
        }

        // 删除标签和分类关联
        List<RelationShipDomain> relationShips = relationShipDao.getRelationShipByCid(cid);
        if (null != relationShips && relationShips.size() > 0) {
            relationShipDao.deleteRelationShipByCid(cid);
        }
    }

    @Override
    @CacheEvict(value = {"courseCache","courseCaches"}, allEntries = true, beforeInvocation = true)
    public void updateCourseByCid(CourseDomain courseDomain) {
        if (null != courseDomain && null != courseDomain.getCid()) {
            courseDao.updateCourseById(courseDomain);
        }
    }

    @Override
    @Cacheable(value = "courseCache", key = "'courseByCategory_' + #p0")
    public List<CourseDomain> getCourseByCategory(String category) {
        if (null == category)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        return courseDao.getCourseByCategory(category);
    }

    @Override
    @Cacheable(value = "courseCache", key = "'courseByTags_'+ #p0")
    public List<CourseDomain> getCourseByTags(MetaDomain tags) {
        if (null == tags)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        List<RelationShipDomain> relationShip = relationShipDao.getRelationShipByMid(tags.getMid());
        if (null != relationShip && relationShip.size() > 0) {
            return courseDao.getCourseByTags(relationShip);
        }
        return null;
    }
}
