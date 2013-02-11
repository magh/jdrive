package jdrive.ulib;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FileMap<T> {

	private final static String ROOT = ".";

	private final Map<T, String> fileMap = new HashMap<T, String>();

	public boolean containsValue(final String path) {
		return fileMap.values().contains(path);
	}

	public T getByValue(final String path) {
		final Set<Entry<T, String>> entrySet = fileMap.entrySet();
		for (final Entry<T, String> entry : entrySet) {
			if (entry.getValue().equals(path)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public boolean contains(final T id) {
		return fileMap.containsKey(id);
	}

	public String put(final T id, final T parentId, final String fileName) {
		validateName(fileName);
		validateParentId(parentId);
		final String parentPath = fileMap.get(parentId);
		final String fullPath = parentPath + File.separator + fileName;
		fileMap.put(id, fullPath);
		return fullPath;
	}

	public String get(final T id) {
		return fileMap.get(id);
	}

	public String get(final T parentId, final String name) {
		validateName(name);
		return fileMap.get(parentId) + File.separator + name;
	}

	public void setRoot(final T id) {
		fileMap.put(id, ROOT);
	}

	private void validateName(final String name) {
		if ((name == null) || name.contains("/")) {
			throw new RuntimeException(
					String.format("Name '%s' is null or contains '%s'!", name,
							File.separator));
		}
	}

	private void validateParentId(final T parentId) {
		if (!fileMap.containsKey(parentId)) {
			throw new RuntimeException(String.format(
					"parentId '%s' doesn't exist!", parentId));
		}
	}

}
