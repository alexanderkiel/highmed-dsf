package org.highmed.dsf.bpe.spring.config;

import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.highmed.dsf.bpe.plugin.UpdateWhiteListPlugin;
import org.highmed.dsf.bpe.service.UpdateWhiteList;
import org.highmed.dsf.fhir.client.FhirWebserviceClientProvider;
import org.highmed.dsf.fhir.organization.OrganizationProvider;
import org.highmed.dsf.fhir.task.TaskHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateWhiteListConfig
{
	@Autowired
	private FhirWebserviceClientProvider clientProvider;

	@Autowired
	private OrganizationProvider organizationProvider;

	@Autowired
	private TaskHelper taskHelper;

	@Autowired
	private ApplicationContext context;

	@Bean
	public ProcessEnginePlugin updateWhiteListPlugin()
	{
		return new UpdateWhiteListPlugin(context);
	}

	@Bean
	public UpdateWhiteList updateWhiteList()
	{
		return new UpdateWhiteList(organizationProvider, clientProvider, taskHelper);
	}
}
