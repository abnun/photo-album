package de.webmpuls.photo_album

import de.webmpuls.photo_album.util.MediaUtils
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

class AlbumService implements ApplicationContextAware {
    boolean transactional = true

	static scope = "session"

	ApplicationContext applicationContext

	public boolean renameAlbumDirectory(Album tmpAlbum, String oldAlbumName) {
		boolean isOk = false

		if (tmpAlbum) {
			String albumDate = MediaUtils.formatDateForDisk(tmpAlbum.dateCreated)

			String tmpSourceFolderString = applicationContext.
					getResource("${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${oldAlbumName}_${albumDate}").getFile().getAbsolutePath()

			String tmpTargetFolderString = tmpSourceFolderString.replaceFirst(oldAlbumName, tmpAlbum.toString())

			try {
				MediaUtils.moveDir(tmpSourceFolderString, tmpTargetFolderString)
				isOk = true
			}
			catch (Exception e) {
				e.printStackTrace()
				isOk = false
			}
		}
		return isOk
	}
}