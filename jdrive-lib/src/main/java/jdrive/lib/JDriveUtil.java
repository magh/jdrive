package jdrive.lib;

import static jdrive.ulib.Util.log;

import java.io.IOException;

import jdrive.glib.ClientSecrets;
import jdrive.glib.CredentialData;
import jdrive.glib.GoogleUtil;
import jdrive.glib.SingleCredentialStore;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

public class JDriveUtil {

	private final static String CLIENT_SECRETS_FILE = "/client_secrets.json";

	public static GoogleCredential authenticateDrive(final SingleCredentialStore store,
			final GoogleUtil.AuthorizationCodeCallback callback)
			throws IOException {
		final ClientSecrets secrets = ClientSecrets
				.loadFromClasspath(CLIENT_SECRETS_FILE);

		final GoogleCredential credential = GoogleUtil.newGoogleCredential(
				secrets.getClientId(), secrets.getClientSecret());
		final boolean loaded = store.load(credential);
		log("loaded=" + loaded + " credentials="
				+ new CredentialData(credential));

		if (!loaded || !credential.refreshToken()) {
			GoogleUtil.authenticateDrive(secrets.getClientId(),
					secrets.getClientSecret(), secrets.getRedirectUri(),
					credential, callback);
		}

		log("Persisting credential=" + new CredentialData(credential));
		store.store(credential);

		return credential;
	}

}
