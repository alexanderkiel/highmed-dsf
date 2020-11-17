package org.highmed.dsf.bpe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.dbcp2.BasicDataSource;
import org.highmed.dsf.bpe.process.ProcessKeyAndVersion;
import org.highmed.dsf.bpe.process.ProcessesResource;
import org.highmed.dsf.bpe.process.ResourceInfo;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.parser.DataFormatException;

public class ProcessPluginResourcesDaoJdbc extends AbstractDaoJdbc implements ProcessPluginResourcesDao
{
	private static final Logger logger = LoggerFactory.getLogger(ProcessPluginResourcesDaoJdbc.class);

	public ProcessPluginResourcesDaoJdbc(BasicDataSource dataSource)
	{
		super(dataSource);
	}

	@Override
	public List<ResourceInfo> getResources(ProcessKeyAndVersion processKeyAndVersion) throws SQLException
	{
		Objects.requireNonNull(processKeyAndVersion, "processKeyAndVersion");

		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(
						"SELECT resource_type, resource_id, url, version, name FROM process_plugin_resources WHERE process_key_and_version = ?"))
		{
			statement.setString(1, processKeyAndVersion.toString());

			logger.trace("Executing query '{}'", statement);
			try (ResultSet result = statement.executeQuery())
			{
				List<ResourceInfo> resources = new ArrayList<>();
				while (result.next())
				{
					String resourceType = result.getString(1);
					UUID resourceId = result.getObject(2, UUID.class);
					String url = result.getString(3);
					String version = result.getString(4);
					String name = result.getString(5);

					resources.add(new ResourceInfo(resourceType, url, version, name).setResourceId(resourceId));
				}

				return resources;
			}
		}
	}

	@Override
	public Map<ProcessKeyAndVersion, List<ResourceInfo>> getResources() throws SQLException
	{
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(
						"SELECT process_key_and_version, resource_type, resource_id, url, version, name FROM process_plugin_resources ORDER BY process_key_and_version"))
		{
			logger.trace("Executing query '{}'", statement);
			try (ResultSet result = statement.executeQuery())
			{
				Map<ProcessKeyAndVersion, List<ResourceInfo>> resources = new HashMap<>();

				ProcessKeyAndVersion processKeyAndVersion = null;
				List<ResourceInfo> processKeyAndVersionResources = null;
				while (result.next())
				{
					ProcessKeyAndVersion currentProcessKeyAndVersion = ProcessKeyAndVersion
							.fromString(result.getString(1));

					if (!currentProcessKeyAndVersion.equals(processKeyAndVersion))
					{
						processKeyAndVersion = currentProcessKeyAndVersion;
						processKeyAndVersionResources = new ArrayList<>();
						resources.put(processKeyAndVersion, processKeyAndVersionResources);
					}

					String resourceType = result.getString(2);
					UUID resourceId = result.getObject(3, UUID.class);
					String url = result.getString(4);
					String version = result.getString(5);
					String name = result.getString(6);

					processKeyAndVersionResources
							.add(new ResourceInfo(resourceType, url, version, name).setResourceId(resourceId));
				}

				return resources;
			}
		}
	}

	@Override
	public void addResources(Collection<? extends ProcessesResource> newResources) throws SQLException
	{
		Objects.requireNonNull(newResources, "newResources");

		if (newResources.isEmpty())
			return;

		try (Connection connection = dataSource.getConnection())
		{
			connection.setReadOnly(false);
			connection.setAutoCommit(false);

			try (PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO process_plugin_resources (process_key_and_version, resource_type, resource_id, url, version) VALUES (?, ?, ?, ?, ?) "
							+ "ON CONFLICT (process_key_and_version, resource_type, url, version) "
							+ "WHERE resource_type <> 'NamingSystem'" + " DO UPDATE SET resource_id = ?"))
			{
				for (ProcessesResource resource : newResources)
				{
					if ("NamingSystem".equals(resource.getResourceInfo().getResourceType()))
						continue;

					for (ProcessKeyAndVersion process : resource.getProcesses())
					{
						ResourceInfo resourceInfo = resource.getResourceInfo();

						statement.setString(1, process.toString());
						statement.setString(2, resourceInfo.getResourceType());
						statement.setObject(3, uuidToPgObject(resourceInfo.getResourceId()));
						statement.setString(4, resourceInfo.getUrl());
						statement.setString(5, resourceInfo.getVersion());

						statement.setObject(6, uuidToPgObject(resourceInfo.getResourceId()));

						statement.addBatch();
						logger.trace("Executing query '{}'", statement);
					}
				}

				statement.executeBatch();
			}
			catch (SQLException e)
			{
				connection.rollback();
				throw e;
			}

			try (PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO process_plugin_resources (process_key_and_version, resource_type, resource_id, name) VALUES (?, 'NamingSystem', ?, ?) "
							+ "ON CONFLICT (process_key_and_version, resource_type, name) "
							+ "WHERE resource_type = 'NamingSystem'" + " DO UPDATE SET resource_id = ?"))
			{
				for (ProcessesResource resource : newResources)
				{
					if (!"NamingSystem".equals(resource.getResourceInfo().getResourceType()))
						continue;

					for (ProcessKeyAndVersion process : resource.getProcesses())
					{
						ResourceInfo resourceInfo = resource.getResourceInfo();

						statement.setString(1, process.toString());
						statement.setObject(2, uuidToPgObject(resourceInfo.getResourceId()));
						statement.setString(3, resourceInfo.getName());

						statement.setObject(4, uuidToPgObject(resourceInfo.getResourceId()));

						statement.addBatch();
						logger.trace("Executing query '{}'", statement);
					}
				}

				statement.executeBatch();
			}
			catch (SQLException e)
			{
				connection.rollback();
				throw e;
			}

			connection.commit();
		}
	}

	private PGobject uuidToPgObject(UUID uuid)
	{
		if (uuid == null)
			return null;

		try
		{
			PGobject o = new PGobject();
			o.setType("UUID");
			o.setValue(uuid.toString());
			return o;
		}
		catch (DataFormatException | SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
}
