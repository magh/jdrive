package jdrive.glib;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.activation.MimetypesFileTypeMap;

import jdrive.ulib.Util;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

public class DriveUtil {

	public static final String MIME_FOLDER = "application/vnd.google-apps.folder";

	private static final HttpTransport httpTransport = new NetHttpTransport();
	private static final JsonFactory jsonFactory = new JacksonFactory();

	/**
	 * @param clientId
	 * @param clientSecret
	 * @param redirectUri
	 * @param credential
	 * @param callback
	 * @throws IOException
	 */
	public static void authenticate(final String clientId,
			final String clientSecret, final String redirectUri,
			final GoogleCredential credential,
			final AuthorizationCodeCallback callback) throws IOException {

		final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, jsonFactory, clientId, clientSecret,
				Arrays.asList(DriveScopes.DRIVE)).setAccessType("offline")
				.setApprovalPrompt("force").build();
		final String authorizationUrl = flow.newAuthorizationUrl()
				.setRedirectUri(redirectUri).build();
		final String authorizationCode = callback
				.getAuthorizationCode(authorizationUrl);
		final GoogleTokenResponse response = flow
				.newTokenRequest(authorizationCode).setRedirectUri(redirectUri)
				.execute();
		credential.setFromTokenResponse(response);
	}

	public interface AuthorizationCodeCallback {
		String getAuthorizationCode(String authorizationUrl);
	}

	public static Drive getDriveService(final GoogleCredential credential) {
		// Create a new authorized API client
		return new Drive.Builder(httpTransport, jsonFactory, credential)
				.build();
	}

	public static GoogleCredential newGoogleCredential(final String clientId,
			final String clientSecret) {
		return new GoogleCredential.Builder().setTransport(httpTransport)
				.setJsonFactory(jsonFactory)
				.setClientSecrets(clientId, clientSecret).build();
	}

	public static String uploadFile(final Drive service, final String parentId,
			final java.io.File file)
			throws IOException {
		final String mimeType = MimetypesFileTypeMap.getDefaultFileTypeMap()
				.getContentType(file);
		return uploadFile(service, parentId, file.getPath(), file.getName(),
				"", mimeType);
	}

	public static String uploadFile(final Drive service, final String parentId,
			final String fileName,
			final String title, final String description) throws IOException {
		return uploadFile(service, parentId, fileName, title, description,
				"application/octet-stream");
	}

	public static String uploadFile(final Drive service, final String parentId,
			final String fileName,
			final String title, final String description, final String mimeType)
			throws IOException {

		// Insert a file
		final File body = new File();
		body.setTitle(title);
		body.setDescription(description);
		body.setMimeType(mimeType);
		final ParentReference parentRef = new ParentReference();
		parentRef.setId(parentId);
		body.setParents(Arrays.asList(parentRef));

		final java.io.File fileContent = new java.io.File(fileName);
		final FileContent mediaContent = new FileContent(mimeType, fileContent);

		final File file = service.files().insert(body, mediaContent).execute();
		return file.getId();
	}

	/**
	 * Download a file's content.
	 *
	 * @param service
	 *            Drive API service instance.
	 * @param file
	 *            Drive File instance.
	 * @return InputStream containing the file's content if successful,
	 *         {@code null} otherwise.
	 */
	public static InputStream downloadFile(final Drive service, final File file) {
		if (Util.isEmpty(file.getDownloadUrl())) {
			// The file doesn't have any content stored on Drive.
			return null;
		}
		try {
			final HttpResponse resp = service.getRequestFactory()
					.buildGetRequest(new GenericUrl(file.getDownloadUrl()))
					.execute();
			return resp.getContent();
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String createFolder(final Drive service,
			final String folderName, final String parentId) throws IOException {
		final File body = new File();
		body.setTitle(folderName);
		final ParentReference parentRef = new ParentReference();
		parentRef.setId(parentId);
		body.setParents(Arrays.asList(parentRef));
		body.setMimeType(MIME_FOLDER);
		final File file = service.files().insert(body).execute();
		return file.getId();
	}

}
