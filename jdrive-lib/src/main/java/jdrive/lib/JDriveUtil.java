package jdrive.lib;

import static jdrive.ulib.Util.log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import jdrive.glib.ClientSecrets;
import jdrive.glib.DriveUtil;
import jdrive.glib.SingleCredentialStore;
import jdrive.ulib.Util;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class JDriveUtil {

	private final static String CLIENT_SECRETS_FILE = "/client_secrets.json";

	public static GoogleCredential authenticate(
			final SingleCredentialStore store,
			final DriveUtil.AuthorizationCodeCallback callback)
			throws IOException {
		final ClientSecrets secrets = ClientSecrets
				.loadFromClasspath(CLIENT_SECRETS_FILE);

		final GoogleCredential credential = DriveUtil.newGoogleCredential(
				secrets.getClientId(), secrets.getClientSecret());
		final boolean loaded = store.load(credential);
		if (!loaded || !credential.refreshToken()) {
			DriveUtil.authenticate(secrets.getClientId(),
					secrets.getClientSecret(), secrets.getRedirectUri(),
					credential, callback);
		}
		store.store(credential);
		return credential;
	}

	public static void sync(final Drive service) throws IOException {
		final DriveFileMap fileMap = new DriveFileMap();
		final FileList list = service.files().list().execute();
		final List<File> files = list.getItems();
		log("entries=" + files.size());
		mkdirs(files, fileMap);
		syncLatest(service, files, fileMap);
		uploadNewRecursive(service, fileMap, new java.io.File("."));
	}

	private static void uploadNewRecursive(final Drive service,
			final DriveFileMap fileMap, final java.io.File root)
			throws IOException {
		final java.io.File[] files = root.listFiles();
		for (final java.io.File file : files) {
			if (file.isDirectory()) {
				final String path = file.getPath();
				log("Directory: " + path);
				if (!fileMap.containsValue(path)) {
					log("mkdir " + path);
					final String parentId = fileMap
							.getByValue(file.getParent());
					final String id = DriveUtil.createFolder(service,
							file.getName(), parentId);
					fileMap.put(id, parentId, file.getName());
				}
				uploadNewRecursive(service, fileMap, file);
			} else if (!"./.jdrive".equals(file.getPath())) {
				final String path = file.getPath();
				log("File: " + path);
				if (!fileMap.containsValue(path)) {
					log("Upload " + path);
					final String parentId = fileMap
							.getByValue(file.getParent());
					final String id = DriveUtil.uploadFile(service, parentId,
							file);
					fileMap.put(id, parentId, file.getName());
				}
			}
		}
	}

	private static void syncLatest(final Drive service, final List<File> files,
			final DriveFileMap fileMap) throws IOException {
		for (final File file : files) {
			if (!file.getEditable()
					|| DriveUtil.MIME_FOLDER.equals(file.getMimeType())) {
				continue;
			}
			final String filePath = fileMap.put(file);
			final java.io.File local = new java.io.File(filePath);
			if (local.exists()) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(filePath);
					if (Util.md5(fis).equalsIgnoreCase(file.getMd5Checksum())) {
						log("File already exists: " + filePath);
						continue;
					}
				} finally {
					Util.close(fis);
				}
			}

			if (local.exists() && (local.lastModified() > getDate(file))) {
				log("Uploading " + filePath);
				final String mimeType = MimetypesFileTypeMap
						.getDefaultFileTypeMap().getContentType(local);
				DriveUtil.uploadFile(service, filePath, file.getTitle(),
						file.getDescription(), mimeType);
			} else {
				log("Download file? " + filePath);
				final String downloadUrl = file.getDownloadUrl();
				if (downloadUrl != null) {
					final InputStream is = DriveUtil
							.downloadFile(service, file);
					if (is != null) {
						log("Downloading: " + filePath);
						Util.download(is, filePath);
					}
				}
			}
		}
	}

	private static void mkdirs(final List<File> files,
			final DriveFileMap fileMap) {
		for (final File file : files) {
			if (!file.getEditable()
					|| !DriveUtil.MIME_FOLDER.equals(file.getMimeType())) {
				continue;
			}
			// log("file=" + file.toPrettyString());
			final String fullPath = fileMap.put(file);
			final java.io.File file2 = new java.io.File(fullPath);
			if (!file2.exists()) {
				log("mkdir " + fullPath);
				file2.mkdirs();
			}
		}
	}

	private static long getDate(final File file) {
		final DateTime modifiedDate = file.getModifiedDate();
		if (modifiedDate != null) {
			return modifiedDate.getValue();
		}
		return file.getCreatedDate().getValue();
	}

}
