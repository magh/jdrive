package jdrive.lib;

import java.util.List;

import jdrive.glib.DriveUtil;
import jdrive.ulib.FileMap;
import jdrive.ulib.Util;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

public class DriveFileMap extends FileMap<String> {

	public String put(final File file, final List<File> files) {
		final ParentReference parent = DriveUtil.getParent(file);
		final String parentId = parent.getId();
		if (parent.getIsRoot() && !contains(parentId)) {
			setRoot(parentId);
		}
		if (!contains(parentId)) {
			final File parentFile = findFile(parentId, files);
			put(parentFile, files);
		}
		return put(file.getId(), parentId, getFileName(file));
	}

	private String getFileName(final File file) {
		final String fileName;
		if (!Util.isEmpty(file.getOriginalFilename())) {
			fileName = file.getOriginalFilename();
		} else {
			fileName = file.getTitle();
		}
		return fileName;
	}

	private static File findFile(final String fileId, final List<File> files) {
		for (final File file : files) {
			if (file.getId().equals(fileId)) {
				return file;
			}
		}
		return null;
	}

}
