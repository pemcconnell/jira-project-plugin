package com.rehabstudio.plugins;

import com.atlassian.jira.issue.Issue;

public class BreakdownReportAssignee {
	
	private Issue issue;
	
	public String desc;
	
	public BreakdownReportAssignee (Issue issue)
	{
		this.issue = issue;
	}
	
	public Object getUserData ()
	{
		this.desc = String.valueOf(this.issue.getSummary());
		
		return this;
	}
}
