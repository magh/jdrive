package jdrive.lib;

import static jdrive.ulib.Util.log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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

	private static void mkdirs(final List<File> files,
			final DriveFileMap fileMap) {
		log("mkdirs");
		for (final File file : files) {
			if (!file.getEditable()
					|| !DriveUtil.MIME_FOLDER.equals(file.getMimeType())) {
				continue;
			}
			// log("file=" + file.toPrettyString());
			final String fullPath = fileMap.put(file, files);
			final java.io.File file2 = new java.io.File(fullPath);
			if (!file2.exists()) {
				log("mkdir " + fullPath);
				file2.mkdirs();
			}
		}
	}

	private static void syncLatest(final Drive service, final List<File> files,
			final DriveFileMap fileMap) throws IOException {
		log("syncLatest");
		for (final File file : files) {
			if (!file.getEditable()
					|| DriveUtil.MIME_FOLDER.equals(file.getMimeType())) {
				continue;
			}
			final String filePath = fileMap.put(file, files);
			final java.io.File local = new java.io.File(filePath);

			final boolean localExists = local.exists();
			final boolean remoteTrashed = Util.valueOf(file
					.getExplicitlyTrashed());
			if (!localExists) {
				if (remoteTrashed) {
					log("Remote trashed (nop): " + filePath);
				} else {
					log("Download remote (!local): " + filePath);
					download(service, filePath, file);
				}
			} else {
				final boolean equals = equals(filePath, file);
				final boolean localNewer = local.lastModified() > getDate(file);
				if (remoteTrashed) {
					if (equals) {
						log("Deleting local (trashed): " + filePath);
						local.delete();
					} else if (localNewer) {
						log("Uploading local (newer, trashed): " + filePath);
						overwrite(service, filePath, file);
					} else {
						log("Deleting local (trashed): " + filePath);
						local.delete();
					}
				} else {
					if (equals) {
						log("equals (nop): " + filePath);
					} else if (localNewer) {
						log("Uploading local (newer): " + filePath);
						overwrite(service, filePath, file);
					} else {
						log("Downloading remote: " + filePath);
						download(service, filePath, file);
					}
				}
			}
		}
	}

	private static void download(final Drive service, final String filePath,
			final File file) throws IOException {
		final String downloadUrl = file.getDownloadUrl();
		if (downloadUrl != null) {
			final InputStream is = DriveUtil.downloadFile(service, file);
			if (is != null) {
				log("Downloading: " + filePath);
				Util.download(is, filePath);
			}
		}
	}

	private static void uploadNewRecursive(final Drive service,
			final DriveFileMap fileMap, final java.io.File root)
			throws IOException {
		log("uploadNewRecursive");
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

	private static void overwrite(final Drive service, final String filePath,
			final File remote) throws IOException {
		DriveUtil.delete(service, remote.getId());
		DriveUtil.uploadFile(service, filePath, remote);
	}

	private static boolean equals(final String local, final File remote) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(local);
			if (Util.md5(fis).equalsIgnoreCase(remote.getMd5Checksum())
					&& !Util.valueOf(remote.getExplicitlyTrashed())) {
				return true;
			}
		} catch (final FileNotFoundException e) {
		} finally {
			Util.close(fis);
		}
		return false;
	}

	private static long getDate(final File file) {
		final DateTime modifiedDate = file.getModifiedDate();
		if (modifiedDate != null) {
			return modifiedDate.getValue();
		}
		return file.getCreatedDate().getValue();
	}

}
