package jdrive.cli;

import static jdrive.ulib.Util.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jdrive.glib.DriveUtil;
import jdrive.glib.FileSingleCredentialStore;
import jdrive.lib.JDriveUtil;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.drive.Drive;

public class JDriveCli {

	public void run() throws IOException {
		final FileSingleCredentialStore store = new FileSingleCredentialStore(
				".jdrive");
		final GoogleCredential credential = JDriveUtil.authenticate(store, callback);

		final Drive service = DriveUtil.getDriveService(credential);

		JDriveUtil.sync(service);
	}

	private final DriveUtil.AuthorizationCodeCallback callback = new DriveUtil.AuthorizationCodeCallback() {
		@Override
		public String getAuthorizationCode(final String authorizationUrl) {
			log("Please open the following URL in your browser then type the authorization code:");
			log("  " + authorizationUrl);
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String authorizationCode = null;
			try {
				authorizationCode = br.readLine();
				log("authorizationCode=" + authorizationCode);
			} catch (final IOException e) {
				log("ERROR " + e.getMessage());
			}
			return authorizationCode;
		}
	};

	public static void main(final String[] args) throws IOException {
		final JDriveCli cli = new JDriveCli();
		cli.run();
	}

}
