package org.folio.service.export;

import org.apache.commons.lang3.tuple.Pair;
import org.folio.rest.jaxrs.model.FileDefinition;
import org.folio.service.manager.export.ExportPayload;

import java.util.List;

/**
 * Export service
 */
public interface ExportService {

  /**
   * Exports collection of srs records to the destination.
   * Performs converting from json to marc format.
   *
   * @param marcToExport   collection of srs records on export and count of failed records
   * @param exportPayload  export payload on export
   */
  void exportSrsRecord(Pair<List<String>, Integer> marcToExport, ExportPayload exportPayload);

  /**
   * Exports collection of marc records to the destination.
   *
   * @param inventoryRecords collection of marc inventory records
   * @param fileDefinition   definition of file on export
   */
  void exportInventoryRecords(List<String> inventoryRecords, FileDefinition fileDefinition, String tenantId);

  /**
   * Performs post export logic
   *
   * @param fileDefinition file definition
   * @param tenantId       tenant id
   */
  void postExport(FileDefinition fileDefinition, String tenantId);
}
