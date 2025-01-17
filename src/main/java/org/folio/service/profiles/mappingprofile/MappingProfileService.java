package org.folio.service.profiles.mappingprofile;

import io.vertx.core.Future;
import org.folio.rest.jaxrs.model.MappingProfile;
import org.folio.rest.jaxrs.model.MappingProfileCollection;
import org.folio.util.OkapiConnectionParams;

public interface MappingProfileService {

  /**
   * Returns {@link MappingProfileCollection} by the input query
   *
   * @param query  query string to filter entities
   * @param offset starting index in a list of results
   * @param limit  maximum number of results to return
   * @return future with {@link MappingProfileCollection}
   */
  Future<MappingProfileCollection> get(String query, int offset, int limit, String tenantId);

  /**
   * Saves {@link MappingProfile} to database
   *
   * @param mappingProfile {@link MappingProfile} to save
   * @param params         okapi headers and connection parameters
   * @return future with saved {@link MappingProfile}
   */
  Future<MappingProfile> save(MappingProfile mappingProfile, OkapiConnectionParams params);

  /**
   * Updates {@link MappingProfile}
   *
   * @param mappingProfile {@link MappingProfile} to update
   * @param params         okapi headers and connection parameters
   * @return future with {@link MappingProfile}
   */
  Future<MappingProfile> update(MappingProfile mappingProfile, OkapiConnectionParams params);

  /**
   * Delete {@link MappingProfile} by id
   *
   * @param mappingProfileId job id
   * @param tenantId         tenant id
   * @return future with {@link MappingProfile}
   */
  Future<Boolean> deleteById(String mappingProfileId, String tenantId);

  /**
   * Gets {@link MappingProfile}
   *
   * @param mappingProfileId job id
   * @param params           {@link OkapiConnectionParams} okapi headers and connection parameters
   * @return future with {@link MappingProfile}
   */
  Future<MappingProfile> getById(String mappingProfileId, OkapiConnectionParams params);

  /**
   * Gets default {@link MappingProfile}
   *
   * @param params {@link OkapiConnectionParams} okapi headers and connection parameters
   * @return future with default {@link MappingProfile}
   */
  Future<MappingProfile> getDefaultInstanceMappingProfile(OkapiConnectionParams params);

  /**
   * Gets default {@link MappingProfile}
   *
   * @param params {@link OkapiConnectionParams} okapi headers and connection parameters
   * @return future with default {@link MappingProfile}
   */
  Future<MappingProfile> getDefaultHoldingMappingProfile(OkapiConnectionParams params);

  /**
   * Gets default authority {@link MappingProfile}
   *
   * @param params {@link OkapiConnectionParams} okapi headers and connection parameters
   * @return future with default {@link MappingProfile}
   */
  Future<MappingProfile> getDefaultAuthorityMappingProfile(OkapiConnectionParams params);

  /**
   * Validates {@link MappingProfile}
   *
   * @param mappingProfile {@link MappingProfile} to validate
   * @param params         {@link OkapiConnectionParams} okapi headers and connection parameters
   * @return future with {@link Void}
   */
  Future<Void> validate(MappingProfile mappingProfile, OkapiConnectionParams params);


}
