package jdrive.glib;

import jdrive.ulib.Util;

import com.google.api.client.auth.oauth2.Credential;

public class CredentialData {
	private final String accessToken;
	private final String refreshToken;
	private final long expireAtMs;

	public CredentialData(final Credential credential) {
		this.accessToken = credential.getAccessToken();
		this.refreshToken = credential.getRefreshToken();
		this.expireAtMs = credential.getExpirationTimeMilliseconds();
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public long getExpireAtMs() {
		return expireAtMs;
	}

	public boolean populate(final Credential credential) {
		if (credential == null) {
			return false;
		}
		if (!Util.isEmpty(accessToken)) {
			credential.setAccessToken(accessToken);
		}
		if (!Util.isEmpty(refreshToken)) {
			credential.setRefreshToken(refreshToken);
		}
		credential.setExpirationTimeMilliseconds(expireAtMs);
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[accessToken=").append(accessToken).append("]");
		sb.append("[refreshToken=").append(refreshToken).append("]");
		sb.append("[expireAtMs=").append(expireAtMs).append("]");
		return sb.toString();
	}

}
