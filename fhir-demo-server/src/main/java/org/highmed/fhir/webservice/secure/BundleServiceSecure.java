package org.highmed.fhir.webservice.secure;

import org.highmed.fhir.help.ResponseGenerator;
import org.highmed.fhir.webservice.specification.BundleService;
import org.hl7.fhir.r4.model.Bundle;

public class BundleServiceSecure extends AbstractServiceSecure<Bundle, BundleService> implements BundleService
{
	public BundleServiceSecure(BundleService delegate, ResponseGenerator responseGenerator)
	{
		super(delegate, responseGenerator);
	}
}
