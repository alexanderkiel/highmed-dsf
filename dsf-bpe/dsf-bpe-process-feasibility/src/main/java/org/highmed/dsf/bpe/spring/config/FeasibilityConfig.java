package org.highmed.dsf.bpe.spring.config;

import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.highmed.dsf.bpe.message.SendFeasibilityRequest;
import org.highmed.dsf.bpe.message.SendFeasibilityResults;
import org.highmed.dsf.bpe.plugin.FeasibilityPlugin;
import org.highmed.dsf.bpe.service.CalculateSimpleFeasibilityResults;
import org.highmed.dsf.bpe.service.CheckMultiMedicFeasibilityResults;
import org.highmed.dsf.bpe.service.CheckFeasibilityResources;
import org.highmed.dsf.bpe.service.CheckSingleMedicFeasibilityResults;
import org.highmed.dsf.bpe.service.DownloadFeasibilityResources;
import org.highmed.dsf.bpe.service.ExecuteFeasibilityQueries;
import org.highmed.dsf.bpe.service.SelectRequestMedics;
import org.highmed.dsf.bpe.service.SelectResponseMedics;
import org.highmed.dsf.bpe.service.StoreFeasibilityResults;
import org.highmed.dsf.fhir.client.FhirWebserviceClientProvider;
import org.highmed.dsf.fhir.organization.OrganizationProvider;
import org.highmed.dsf.fhir.task.TaskHelper;
import org.highmed.dsf.openehr.client.OpenehrWebserviceClientProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeasibilityConfig
{
	@Autowired
	private FhirWebserviceClientProvider fhirClientProvider;

	@Autowired
	private OpenehrWebserviceClientProvider openehrClientProvider;

	@Autowired
	private OrganizationProvider organizationProvider;

	@Autowired
	private TaskHelper taskHelper;

	@Bean
	public ProcessEnginePlugin feasibilityPlugin()
	{
		return new FeasibilityPlugin();
	}

	@Bean
	public DownloadFeasibilityResources downloadFeasibilityResources()
	{
		return new DownloadFeasibilityResources(organizationProvider, fhirClientProvider, taskHelper);
	}

	@Bean
	public CheckFeasibilityResources checkFeasibilityResources()
	{
		return new CheckFeasibilityResources(fhirClientProvider.getLocalWebserviceClient(), taskHelper);
	}

	@Bean
	public SelectRequestMedics selectRequestMedics()
	{
		return new SelectRequestMedics(organizationProvider, fhirClientProvider.getLocalWebserviceClient(), taskHelper);
	}

	@Bean
	public ExecuteFeasibilityQueries executeFeasibilityQueries()
	{
		return new ExecuteFeasibilityQueries(organizationProvider, fhirClientProvider.getLocalWebserviceClient(), openehrClientProvider.getWebserviceClient(), taskHelper);
	}

	@Bean
	public CheckSingleMedicFeasibilityResults checkSingleMedicFeasibilityResults()
	{
		return new CheckSingleMedicFeasibilityResults(fhirClientProvider.getLocalWebserviceClient(), taskHelper);
	}

	@Bean
	public SelectResponseMedics selectResponseMedics()
	{
		return new SelectResponseMedics(organizationProvider, taskHelper, fhirClientProvider.getLocalWebserviceClient());
	}

	@Bean
	public StoreFeasibilityResults storeFeasibilityResults()
	{
		return new StoreFeasibilityResults(organizationProvider, fhirClientProvider.getLocalWebserviceClient(), taskHelper);
	}

	@Bean
	public CalculateSimpleFeasibilityResults calculateSimpleFeasibilityResults()
	{
		return new CalculateSimpleFeasibilityResults(fhirClientProvider.getLocalWebserviceClient(), taskHelper);
	}

	@Bean
	public CheckMultiMedicFeasibilityResults checkMultiMedicFeasibilityResults()
	{
		return new CheckMultiMedicFeasibilityResults(fhirClientProvider.getLocalWebserviceClient(), taskHelper);
	}

	@Bean
	public SendFeasibilityRequest sendFeasibilityRequest()
	{
		return new SendFeasibilityRequest(organizationProvider, fhirClientProvider, taskHelper);
	}

	@Bean
	public SendFeasibilityResults sendCohortSizeResultToMedic()
	{
		return new SendFeasibilityResults(organizationProvider, fhirClientProvider, taskHelper);
	}
}
