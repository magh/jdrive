package jdrive.lib;

import jdrive.ulib.FileMap;
import jdrive.ulib.Util;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

public class DriveFileMap extends FileMap<String> {

	public String put(final File file) {
		final ParentReference parent = file.getParents().get(0);
		if (parent.getIsRoot() && !contains(parent.getId())) {
			setRoot(parent.getId());
		}
		return put(file.getId(), parent.getId(), getFileName(file));
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

}
