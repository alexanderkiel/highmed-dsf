package org.highmed.dsf.bpe.delegate;

import java.util.stream.Stream;

import org.camunda.bpm.engine.impl.variable.serializer.TypedValueSerializer;
import org.springframework.context.ApplicationContext;

public interface DelegateProvider
{
	/**
	 * @param processDefinitionKey
	 *            not <code>null</code>
	 * @param processDefinitionVersion
	 *            not <code>null</code>
	 * @return returns the default class loader if no special class loader is registered for the given
	 *         <b>processDefinitionKey</b> and <b>processDefinitionVersion</b>
	 */
	ClassLoader getClassLoader(String processDefinitionKey, String processDefinitionVersion);

	/**
	 * @param processDefinitionKey
	 *            not <code>null</code>
	 * @param processDefinitionVersion
	 *            not <code>null</code>
	 * @return returns the default application context if no special application context is registered for the given
	 *         <b>processDefinitionKey</b> and <b>processDefinitionVersion</b>
	 */
	ApplicationContext getApplicationContext(String processDefinitionKey, String processDefinitionVersion);

	/**
	 * @return additional {@link TypedValueSerializer}s from the plugin to deploy into the process engine
	 */
	@SuppressWarnings("rawtypes")
	Stream<TypedValueSerializer> getAdditionalTypedValueSerializers();
}