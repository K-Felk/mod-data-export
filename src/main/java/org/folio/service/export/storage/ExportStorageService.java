package org.folio.service.export.storage;

/**
 * File retrieval service. Provides methods for retrieving the exported files.
 */
public interface ExportStorageService {
  /**
   * Fetch the link to download a file for a given job by fileName
   * @param jobId: The job to which the files are associated
   * @param fileName: The name of the file to download
   * @param tenantId
   * @return A link using which the file can be downloaded
   */
  String getFileDownloadLink(String jobId, String exportFileId, String tenantId);

  void storeFile();


}
