package jdrive.glib;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.gson.Gson;

public class ClientSecrets {

	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;

	public ClientSecrets(final String clientId, final String clientSecret,
			final String redirectUrl) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[clientId=").append(clientId).append("]");
		sb.append("[clientSecret=").append(clientSecret).append("]");
		sb.append("[redirectUri=").append(redirectUri).append("]");
		return sb.toString();
	}

	public static ClientSecrets loadFromClasspath(final String name)
			throws IOException {
		return load(new InputStreamReader(
				ClientSecrets.class.getResourceAsStream(name)));
	}

	public static ClientSecrets loadFromFile(final String fileName)
			throws IOException {
		return load(new FileReader(fileName));
	}

	private static ClientSecrets load(final Reader reader) throws IOException {
		final Gson gson = new Gson();
		final ClientSecrets secrets = gson
				.fromJson(reader, ClientSecrets.class);
		reader.close();
		return secrets;
	}

	public static void main(final String[] args) throws IOException {
		final String clientId = "YOUR CLIENT ID";
		final String clientSecret = "YOUR CLIENT SECRET";
		final String redirectUri = "YOUR REDIRECT URI";
		final ClientSecrets clientSecrets = new ClientSecrets(clientId,
				clientSecret, redirectUri);
		final Gson gson = new Gson();
		final String json = gson.toJson(clientSecrets);
		final FileOutputStream fos = new FileOutputStream(
				"../jdrive-lib/src/main/resources/client_secrets.json");
		fos.write(json.getBytes());
		fos.close();
	}

}
