package de.webmpuls.photo_album

import de.webmpuls.photo_album.util.MediaUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import java.text.SimpleDateFormat

class Picture {

	static transients = ['URL', 'thumbNailURL', 'bigURL', 'tempURL', 'permURL', 'exists']
	
	static belongsTo = 'album'

	Date dateCreated
	
	String baseName
	String caption
	boolean coverPicture = false
	Album album

	static mapping = {
		sort("dateCreated")
		order("desc")
		cache(true)
	}

    static constraints = {
		baseName()
		coverPicture()
		caption(nullable: true, blank: true)
		dateCreated(display: false)
		URL(display: false)
		thumbNailURL(display: false)
		bigURL(display: false)
		tempURL(display: false)
		permURL(display: false)
    }

	static namedQueries =
	{
		withAlbumName
		{
			String name ->

			Album tmpAlbum = Album.findByName(name)
			eq('album', tmpAlbum)
			cache(true)
       	}
	}

	public String getURL() {
		return "${MediaUtils.getBaseName(baseName)}${MediaUtils.NORMAL}${MediaUtils.getExtension(baseName)}"
	}

	public String getBigURL() {
		return "${MediaUtils.getBaseName(baseName)}${MediaUtils.BIG}${MediaUtils.getExtension(baseName)}"
	}

	public String getThumbNailURL() {
		return "${MediaUtils.getBaseName(baseName)}${MediaUtils.THUMBNAIL}${MediaUtils.getExtension(baseName)}"
	}

	public String getPermURL() {
		return "${MediaUtils.getBaseName(baseName)}${MediaUtils.PERM}${MediaUtils.getExtension(baseName)}"
	}

	public String getTempURL() {
		return "${MediaUtils.getBaseName(baseName)}${MediaUtils.TEMP}${MediaUtils.getExtension(baseName)}"
	}

	public boolean exists() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy")
		String albumDate = simpleDateFormat.format(album.dateCreated)

		String tmpFilePath = "${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${album.toString()}_${albumDate}${File.separator}"

		String tmpFilePathBig = "${tmpFilePath}${getBigURL()}"
		String tmpFilePathNormal = "${tmpFilePath}${getURL()}"
		String tmpFilePathThumbNail = "${tmpFilePath}${getThumbNailURL()}"
		boolean bigExists = ApplicationHolder.getApplication().getMainContext().getResource(tmpFilePathBig).getFile().exists()
		boolean normalExists = ApplicationHolder.getApplication().getMainContext().getResource(tmpFilePathNormal).getFile().exists()
		boolean thumbNailExists = ApplicationHolder.getApplication().getMainContext().getResource(tmpFilePathThumbNail).getFile().exists()
		boolean exists = bigExists && normalExists && thumbNailExists
		if(!exists) {
			if(log.debugEnabled) {
				log.debug("File '${tmpFilePathNormal}' does${exists ? '' : ' not'} exist")
			}
		}
		return exists
	}

	public String toString() {
		return baseName
	}
}