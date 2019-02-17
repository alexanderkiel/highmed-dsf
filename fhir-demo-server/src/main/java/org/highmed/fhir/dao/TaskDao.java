package org.highmed.fhir.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.highmed.fhir.search.parameters.TaskRequester;
import org.highmed.fhir.search.parameters.TaskStatus;
import org.hl7.fhir.r4.model.Task;

import ca.uhn.fhir.context.FhirContext;

public class TaskDao extends AbstractDomainResourceDao<Task>
{
	public TaskDao(BasicDataSource dataSource, FhirContext fhirContext)
	{
		super(dataSource, fhirContext, Task.class, "tasks", "task", "task_id", TaskRequester::new, TaskStatus::new);
	}

	@Override
	protected Task copy(Task resource)
	{
		return resource.copy();
	}
}
