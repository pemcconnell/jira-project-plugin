package com.rehabstudio.plugins;
 
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.atlassian.crowd.embedded.api.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BreakdownReport extends AbstractReport
{

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
        List<Issue> issues = getIssues(remoteUser, projectId);
        
        Map<String, Object> assigneeMap = new HashMap<String, Object>();
        
        User testa = null;
        for (Issue issue : issues)
        {
        	String key = String.valueOf(issue.getAssignee());
        	if ( assigneeMap.get(key) != null) 
        	{
        		
        	} else {
        		//Map<String, String> map = new HashMap<String, String>();
        		//map.put("name", 2);
        		//map.put("fname", "fdemo");
        		BreakdownReportAssignee user = new BreakdownReportAssignee(issue);
        		
        		
        		assigneeMap.put(key, user.getUserData());
        	}
        }

        Map<String, Object> velocityParams = new HashMap<String, Object>();

        velocityParams.put("projectName", projectManager.getProjectObj(projectId).getName());
        velocityParams.put("issues", issues);
        velocityParams.put("assigneeMap", assigneeMap);
        velocityParams.put("testa", testa);
        velocityParams.put("issueCount", issues.size());
        velocityParams.put("issuesWithoutComponents", getIssueCountWithoutComponents(remoteUser, projectId));
        velocityParams.put("epics", getEpics(remoteUser, projectId));

        return descriptor.getHtml("view", velocityParams);
    }

    private List<Issue> getIssues(User remoteUser, Long projectId) throws SearchException
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

    public List<Issue> getEpics(User remoteUser, Long projectId) throws SearchException
    {
        JqlQueryBuilder queryBuilder = JqlQueryBuilder.newBuilder();
        Query query = queryBuilder.where().issueType("epic").and().project(projectId).buildQuery();
        return searchProvider.search(query, remoteUser, PagerFilter.getUnlimitedFilter()).getIssues();
    }

    public List<Issue> myTest(User remoteUser, Long projectId) throws SearchException
    {
        JqlQueryBuilder queryBuilder = JqlQueryBuilder.newBuilder();
        Query query = queryBuilder.where().issueType("epic").and().project(projectId).buildQuery();
        return searchProvider.search(query, remoteUser, PagerFilter.getUnlimitedFilter()).getIssues();
    }

}
