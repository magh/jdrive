package jdrive.glib;

import static jdrive.ulib.Util.log;

import java.io.IOException;
import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

public class GoogleUtil {

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
	public static void authenticateDrive(final String clientId,
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
		log("response=" + response.toPrettyString());
		credential.setFromTokenResponse(response);
	}

	public interface AuthorizationCodeCallback {
		String getAuthorizationCode(String authorizationUrl);
	}

	public static GoogleCredential newGoogleCredential(final String clientId,
			final String clientSecret) {
		return new GoogleCredential.Builder().setTransport(httpTransport)
				.setJsonFactory(jsonFactory)
				.setClientSecrets(clientId, clientSecret).build();
	}

	public static String uploadFile(final GoogleCredential credential,
			final String fileName, final String title, final String description)
			throws IOException {
		return uploadFile(credential, fileName, title, description,
				"text/plain");
	}

	public static String uploadFile(final GoogleCredential credential,
			final String fileName, final String title,
			final String description, final String mimeType) throws IOException {
		// Create a new authorized API client
		final Drive service = new Drive.Builder(httpTransport, jsonFactory,
				credential).build();

		// Insert a file
		final File body = new File();
		body.setTitle(title);
		body.setDescription(description);
		body.setMimeType(mimeType);

		final java.io.File fileContent = new java.io.File(fileName);
		final FileContent mediaContent = new FileContent(mimeType, fileContent);

		final File file = service.files().insert(body, mediaContent).execute();
		log("File ID: " + file.getId());
		return file.getId();
	}

}
