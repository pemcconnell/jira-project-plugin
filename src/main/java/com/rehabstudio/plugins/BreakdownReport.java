package com.rehabstudio.plugins;
 
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.core.util.DateUtils;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.web.util.OutlookDate;
import com.atlassian.jira.web.util.OutlookDateManager;
import com.atlassian.query.Query;
import com.atlassian.crowd.embedded.api.User;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
 
public class BreakdownReport extends AbstractReport
{
    private static final Logger log = Logger.getLogger(BreakdownReport.class);

    private final SearchProvider searchProvider;
    private final ProjectManager projectManager;
    
    public BreakdownReport (SearchProvider searchProvider, ProjectManager projectManager)
    {
        this.projectManager = projectManager;
        this.searchProvider = searchProvider;
    }

    public String generateReportHtml(ProjectActionSupport action, Map params) throws Exception
    {
        User remoteUser = action.getRemoteUser();
        Long projectId = ParameterUtils.getLongParam(params, "selectedProjectId");
        List issues = getIssues(remoteUser, projectId);
        Map<String, Object> velocityParams = new HashMap<String, Object>();

        velocityParams.put("projectName", projectManager.getProjectObj(projectId).getName());
        velocityParams.put("issues", issues);
        velocityParams.put("issueCount", issues.size());
        velocityParams.put("issuesWithoutComponents", getIssueCountWithoutComponents(remoteUser, projectId));
        velocityParams.put("epics", getEpics(remoteUser, projectId));
        
        return descriptor.getHtml("view", velocityParams);
    }

    private List getIssues(User remoteUser, Long projectId) throws SearchException
    {
        JqlQueryBuilder queryBuilder = JqlQueryBuilder.newBuilder();
        Query query = queryBuilder.where().issueType("Improvement", "New Feature", "Story", "Task").and().project(projectId).buildQuery();
        //issuetype IN (Improvement, "New Feature", Story, Task) AND component is EMPTY
        return searchProvider.search(query, remoteUser, PagerFilter.getUnlimitedFilter()).getIssues();
    }

    private Long getIssueCountWithoutComponents(User remoteUser, Long projectId) throws SearchException
    {
        JqlQueryBuilder queryBuilder = JqlQueryBuilder.newBuilder();
        Query query = queryBuilder.where().issueType("Improvement", "New Feature", "Story", "Task").and().component("EMPTY").and().project(projectId).buildQuery();
        //issuetype IN (Improvement, "New Feature", Story, Task) AND component is EMPTY
        return searchProvider.searchCount(query, remoteUser);
    }

    public List getEpics(User remoteUser, Long projectId) throws SearchException
    {
        JqlQueryBuilder queryBuilder = JqlQueryBuilder.newBuilder();
        Query query = queryBuilder.where().issueType("epic").and().project(projectId).buildQuery();
        //issuetype IN (Improvement, "New Feature", Story, Task) AND component is EMPTY
        return searchProvider.search(query, remoteUser, PagerFilter.getUnlimitedFilter()).getIssues();
    }

}
