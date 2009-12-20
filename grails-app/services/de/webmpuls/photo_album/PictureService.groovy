package de.webmpuls.photo_album

import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import org.springframework.web.multipart.MultipartFile
import de.webmpuls.photo_album.util.MediaUtils
import grails.util.GrailsUtil

class PictureService implements ApplicationContextAware {

	boolean transactional = true

	static scope = "session"

	ApplicationContext applicationContext

	private static final Integer THUMBNAIL_WIDTH = 200
	private static final Integer THUMBNAIL_HEIGHT = 250
	private static final Integer BIG = 500

    public boolean uploadPhotos(MultipartFile foto, String uploadFolder, String rotateString, String fileName, String albumId) {

		if(log.debugEnabled) {
				log.debug("Uploading picture.")
		}

		File tmpUploadFolder = null

		if (uploadFolder) {
			tmpUploadFolder = applicationContext.
					getResource(File.separator + MediaUtils.DEFAULT_UPLOADS_FOLDER + File.separator + MediaUtils.DEFAULT_FOLDER_IMAGE + uploadFolder).getFile()

			uploadFolder = getUploadPath(tmpUploadFolder).getAbsolutePath()
			if(log.debugEnabled) {
				log.debug(uploadFolder)
			}
		}

		Picture tmpPicture = new Picture(baseName: fileName, album: Album.get(albumId))

		String newFilePath = "${uploadFolder}${File.separator}${tmpPicture.getPermURL()}"

		File newFile = new File(newFilePath)

		foto.transferTo(newFile)
		Thread.sleep(2000)

		String rotateDegrees = rotateString

		boolean isOk = processImg(newFilePath, uploadFolder, tmpUploadFolder, tmpPicture, rotateDegrees)

		if (isOk && newFile.exists() && newFile.size() > 0) {
			Album tmpAlbum = Album.get(albumId)
			Picture newPicture = new Picture(baseName: fileName, album: tmpAlbum)
			Picture existingPicture = Picture.findByBaseNameAndAlbum(fileName, tmpAlbum)
			if (existingPicture) {
				newPicture = existingPicture
			}

			if (newPicture.save(flush: true)) {
				if(log.debugEnabled)
				{
					log.debug("Foto '${newPicture.getURL()}' erfolgreich gespeichert.")
				}
				return true
			}
			else {
				newPicture.errors.allErrors.each {
					if(log.errorEnabled) {
						log.error(it)
					}
				}
				return false
			}
		}
		else {
			return false
		}
	}

	public boolean resetPhoto(Picture picture) {

		if(log.debugEnabled) {
				log.debug("Reseting picture.")
		}

		boolean isOk = false
		if (picture) {
			Album tmpAlbum = picture.album

			String albumDate = MediaUtils.formatDateForDisk(tmpAlbum.dateCreated)

			File tmpUploadFolder = applicationContext.getResource("${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${tmpAlbum.toString()}_${albumDate}").getFile()
			String uploadFolder = getUploadPath(tmpUploadFolder).getAbsolutePath()
			if(log.debugEnabled) {
				log.debug(uploadFolder)
			}
			String sourceFilePath = "${uploadFolder}${File.separator}${picture.getPermURL()}"
			String targetFilePath = "${uploadFolder}${File.separator}${picture.getTempURL()}"

			MediaUtils.copyFile(sourceFilePath, targetFilePath)

			isOk = processImg(sourceFilePath, uploadFolder, tmpUploadFolder, picture)
		}
		return isOk
	}

	public boolean rotatePhoto(Picture picture, String rotateString) {

		if(log.debugEnabled) {
				log.debug("Rotating picture.")
		}

		boolean isOk = false
		if (picture && rotateString) {
			Album tmpAlbum = picture.album

			String albumDate = MediaUtils.formatDateForDisk(tmpAlbum.dateCreated)

			File tmpUploadFolder = applicationContext.getResource("${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${tmpAlbum.toString()}_${albumDate}").getFile()
			String uploadFolder = getUploadPath(tmpUploadFolder).getAbsolutePath()
			if(log.debugEnabled) {
				log.debug(uploadFolder)
			}
			String newFilePath = "${uploadFolder}${File.separator}${picture.getURL()}"
			String rotateDegrees = rotateString

			isOk = processImg(newFilePath, uploadFolder, tmpUploadFolder, picture, rotateDegrees)

			String tempFilePath = "${uploadFolder}${File.separator}${picture.getTempURL()}"
			MediaUtils.copyFile(newFilePath, tempFilePath)
		}
		return isOk
	}

	public boolean reduceRedEyesOnFoto(Picture picture, String width, String height, String offsetHeight, String offsetWidth, boolean completePicture = false) {

		if(log.debugEnabled) {
				log.debug("Reduce red eyes on picture.")
		}

		boolean isOk = false
		if (picture) {
			Album tmpAlbum = picture.album

			String albumDate = MediaUtils.formatDateForDisk(tmpAlbum.dateCreated)

			File tmpUploadFolder = applicationContext.getResource("${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${tmpAlbum.toString()}_${albumDate}").getFile()
			String uploadFolder = getUploadPath(tmpUploadFolder).getAbsolutePath()
			if(log.debugEnabled) {
				log.debug(uploadFolder)
			}
			String sourceFilePath = "${uploadFolder}${File.separator}${picture.getURL()}"
			String targetFilePath = "${uploadFolder}${File.separator}${picture.getTempURL()}"

			sourceFilePath = checkTempFile(sourceFilePath, targetFilePath)

			String cmdMono = ""

			String modulateOptions = "-channel red -modulate 100,20"

			if(completePicture)
			{
				cmdMono = createCmd(getOSSpecificString(sourceFilePath), "$modulateOptions", "", getOSSpecificString(targetFilePath))
			}
			else
			{
				cmdMono = createCmd(getOSSpecificString(sourceFilePath), "-region", createDimentions(Integer.parseInt(width), Integer.parseInt(height), Integer.parseInt(offsetHeight), Integer.parseInt(offsetWidth)) + " $modulateOptions +repage", getOSSpecificString(targetFilePath))
			}

			Process process = null

			try
			{
				process = cmdMono.execute()
				process.waitFor()
				isOk = true
			}
			catch (Exception e)
			{
				e.printStackTrace()
				isOk = false
			}

			isOk = processImg(targetFilePath, uploadFolder, tmpUploadFolder, picture)
		}
		return isOk
	}

	public boolean monochromeFoto(Picture picture, String width, String height, String offsetHeight, String offsetWidth, boolean completePicture = false) {

		if(log.debugEnabled) {
				log.debug("Making picture monochrome.")
		}

		boolean isOk = false
		if (picture) {
			Album tmpAlbum = picture.album

			String albumDate = MediaUtils.formatDateForDisk(tmpAlbum.dateCreated)

			File tmpUploadFolder = applicationContext.getResource("${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${tmpAlbum.toString()}_${albumDate}").getFile()
			String uploadFolder = getUploadPath(tmpUploadFolder).getAbsolutePath()
			if(log.debugEnabled) {
				log.debug(uploadFolder)
			}
			String sourceFilePath = "${uploadFolder}${File.separator}${picture.getURL()}"
			String targetFilePath = "${uploadFolder}${File.separator}${picture.getTempURL()}"

			sourceFilePath = checkTempFile(sourceFilePath, targetFilePath)

			String cmdMono = ""

			if(completePicture)
			{
				cmdMono = createCmd(getOSSpecificString(sourceFilePath), "-colorspace Gray", "", getOSSpecificString(targetFilePath))
			}
			else
			{
				cmdMono = createCmd(getOSSpecificString(sourceFilePath), "-region", createDimentions(Integer.parseInt(width), Integer.parseInt(height), Integer.parseInt(offsetHeight), Integer.parseInt(offsetWidth)) + " -colorspace Gray +repage", getOSSpecificString(targetFilePath))
			}

			Process process = null

			try
			{
				process = cmdMono.execute()
				process.waitFor()
				isOk = true
			}
			catch (Exception e)
			{
				e.printStackTrace()
				isOk = false
			}

			isOk = processImg(targetFilePath, uploadFolder, tmpUploadFolder, picture)
		}
		return isOk
	}

	public boolean sepiaFoto(Picture picture, String width, String height, String offsetHeight, String offsetWidth, boolean completePicture = false) {

		if(log.debugEnabled) {
				log.debug("Making picture sepia.")
		}

		boolean isOk = false
		if (picture) {
			Album tmpAlbum = picture.album

			String albumDate = MediaUtils.formatDateForDisk(tmpAlbum.dateCreated)

			File tmpUploadFolder = applicationContext.getResource("${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${tmpAlbum.toString()}_${albumDate}").getFile()
			String uploadFolder = getUploadPath(tmpUploadFolder).getAbsolutePath()
			if(log.debugEnabled) {
				log.debug(uploadFolder)
			}
			String sourceFilePath = "${uploadFolder}${File.separator}${picture.getURL()}"
			String targetFilePath = "${uploadFolder}${File.separator}${picture.getTempURL()}"

			sourceFilePath = checkTempFile(sourceFilePath, targetFilePath)

			String cmdSepia = ""

			if(completePicture)
			{
				cmdSepia = cmdSepia = createCmd(getOSSpecificString(sourceFilePath), "-sepia-tone 80%", "", getOSSpecificString(targetFilePath))
			}
			else
			{
				cmdSepia = createCmd(getOSSpecificString(sourceFilePath), "-region", createDimentions(Integer.parseInt(width), Integer.parseInt(height), Integer.parseInt(offsetHeight), Integer.parseInt(offsetWidth)) + " -sepia-tone 80% +repage", getOSSpecificString(targetFilePath))
			}

			Process process = null

			try
			{
				process = cmdSepia.execute()
				process.waitFor()
				isOk = true
			}
			catch (Exception e)
			{
				e.printStackTrace()
				isOk = false
			}

			isOk = processImg(targetFilePath, uploadFolder, tmpUploadFolder, picture)
		}
		return isOk
	}

	public boolean negateFoto(Picture picture, String width, String height, String offsetHeight, String offsetWidth, boolean completePicture = false) {

		if(log.debugEnabled) {
				log.debug("Negate picture.")
		}

		boolean isOk = false
		if (picture) {
			Album tmpAlbum = picture.album

			String albumDate = MediaUtils.formatDateForDisk(tmpAlbum.dateCreated)

			File tmpUploadFolder = applicationContext.getResource("${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${tmpAlbum.toString()}_${albumDate}").getFile()
			String uploadFolder = getUploadPath(tmpUploadFolder).getAbsolutePath()
			if(log.debugEnabled) {
				log.debug(uploadFolder)
			}
			String sourceFilePath = "${uploadFolder}${File.separator}${picture.getURL()}"
			String targetFilePath = "${uploadFolder}${File.separator}${picture.getTempURL()}"

			sourceFilePath = checkTempFile(sourceFilePath, targetFilePath)

			String cmdNegate = ""

			if(completePicture)
			{
				cmdNegate = cmdNegate = createCmd(getOSSpecificString(sourceFilePath), "-negate", "", getOSSpecificString(targetFilePath))
			}
			else
			{
				cmdNegate = createCmd(getOSSpecificString(sourceFilePath), "-region", createDimentions(Integer.parseInt(width), Integer.parseInt(height), Integer.parseInt(offsetHeight), Integer.parseInt(offsetWidth)) + " -negate +repage", getOSSpecificString(targetFilePath))
			}

			Process process = null

			try
			{
				process = cmdNegate.execute()
				process.waitFor()
				isOk = true
			}
			catch (Exception e)
			{
				e.printStackTrace()
				isOk = false
			}

			isOk = processImg(targetFilePath, uploadFolder, tmpUploadFolder, picture)
		}
		return isOk
	}

	public boolean cropFoto(Picture picture, String width, String height, String offsetHeight, String offsetWidth) {

		if(log.debugEnabled) {
				log.debug("Cropping picture.")
		}

		boolean isOk = false
		if (picture && width && height) {
			Album tmpAlbum = picture.album

			String albumDate = MediaUtils.formatDateForDisk(tmpAlbum.dateCreated)

			File tmpUploadFolder = applicationContext.getResource("${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${tmpAlbum.toString()}_${albumDate}").getFile()
			String uploadFolder = getUploadPath(tmpUploadFolder).getAbsolutePath()
			if(log.debugEnabled) {
				log.debug(uploadFolder)
			}
			String sourceFilePath = "${uploadFolder}${File.separator}${picture.getURL()}"
			String targetFilePath = "${uploadFolder}${File.separator}${picture.getTempURL()}"

			sourceFilePath = checkTempFile(sourceFilePath, targetFilePath)

			String cmdCrop = createCmd(getOSSpecificString(sourceFilePath), MediaUtils.CROP, createDimentions(Integer.parseInt(width), Integer.parseInt(height), Integer.parseInt(offsetHeight), Integer.parseInt(offsetWidth)) + " +repage", getOSSpecificString(targetFilePath))

			if(log.debugEnabled) {
				log.debug(cmdCrop)
			}

			Process process = null

			try
			{
				process = cmdCrop.execute()
				process.waitFor()
				isOk = true
			}
			catch (Exception e)
			{
				e.printStackTrace()
				isOk = false
			}

			isOk = processImg(targetFilePath, uploadFolder, tmpUploadFolder, picture)
		}
		return isOk
	}

	public boolean movePictureToAlbumOnDisk(Picture picture, Album oldAlbum, String albumId) {

		if(log.debugEnabled) {
				log.debug("Moving picture.")
		}

		boolean isOk = false
		Album tmpAlbum = Album.get(albumId)
		if (picture && tmpAlbum) {
			Album pictureAlbum = oldAlbum

			String albumDate = MediaUtils.formatDateForDisk(tmpAlbum.dateCreated)

			String pictureAlbumDate = MediaUtils.formatDateForDisk(pictureAlbum.dateCreated)
			File tmpSourceFolder = applicationContext.
					getResource("${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${pictureAlbum.toString()}_${pictureAlbumDate}").getFile()

			File tmpTargetFolder = applicationContext.
					getResource("${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${tmpAlbum.toString()}_${albumDate}").getFile()

			String originalNormal = "${tmpSourceFolder.getAbsolutePath()}${File.separator}${picture.getURL()}"
			String originalBig = "${tmpSourceFolder.getAbsolutePath()}${File.separator}${picture.getBigURL()}"
			String originalThumbnail = "${tmpSourceFolder.getAbsolutePath()}${File.separator}${picture.getThumbNailURL()}"
			String originalTemp = "${tmpSourceFolder.getAbsolutePath()}${File.separator}${picture.getPermURL()}"

			String target = "${tmpTargetFolder.getAbsolutePath()}${File.separator}"

			try {
				MediaUtils.copyFile(originalNormal, target, true)
				isOk = true
			}
			catch (Exception e) {
				e.printStackTrace()
				isOk = false
			}

			try {
				MediaUtils.copyFile(originalBig, target, true)
				isOk = true
			}
			catch (Exception e) {
				e.printStackTrace()
				isOk = false
			}

			try {
				MediaUtils.copyFile(originalThumbnail, target, true)
				isOk = true
			}
			catch (Exception e) {
				e.printStackTrace()
				isOk = false
			}

			try {
				MediaUtils.copyFile(originalTemp, target, true)
				isOk = true
			}
			catch (Exception e) {
				e.printStackTrace()
				isOk = false
			}
		}
		return isOk
	}

	private boolean processImg(String fileName, String uploadFolder, File tmpUploadFolder, Picture targetFile, String rotateDegrees = null) {

		if(log.debugEnabled) {
				log.debug("Processing picture.")
		}

		final String original = fileName

		MediaUtils.printSysAndEnvVariables()

		boolean isOk = false

		final String uploadPathBig = "${getUploadPath(tmpUploadFolder).getAbsolutePath()}${File.separator}${targetFile.getBigURL()}"
		final String uploadPathNormal = "${getUploadPath(tmpUploadFolder).getAbsolutePath()}${File.separator}${targetFile.getURL()}"
		final String uploadPathThumbNail = "${getUploadPath(tmpUploadFolder).getAbsolutePath()}${File.separator}${targetFile.getThumbNailURL()}"

		Process process = null

		File originalFile = new File(original)
		if (rotateDegrees && originalFile.exists()) {
			String cmdRotate = createCmd(getOSSpecificString(original), "-rotate ${rotateDegrees}", "", getOSSpecificString(uploadPathNormal))

			if(log.debugEnabled) {
				log.debug(cmdRotate)
			}
			try {
				process = cmdRotate.execute()
				//println process.in.text
				process.waitFor()
				Thread.sleep(2000)
				isOk = true
			}
			catch (Exception e) {
				e.printStackTrace()
				isOk = false
			}
		}
		else {
			try {
				MediaUtils.copyFile(original, uploadPathNormal)
				isOk = true
			}
			catch (Exception e) {
				e.printStackTrace()
				isOk = false
			}
		}

		String cmdThumbNail = createCmd(getOSSpecificString(uploadPathNormal), MediaUtils.THUMBNAIL, createDimentions(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT), getOSSpecificString(uploadPathThumbNail))
		try {
			process = cmdThumbNail.execute()
			process.waitFor()
			isOk = true
		}
		catch (Exception e) {
			e.printStackTrace()
			isOk = false
		}

		String cmdBig = createCmd(getOSSpecificString(uploadPathNormal), MediaUtils.THUMBNAIL, createDimentions(BIG, 0), getOSSpecificString(uploadPathBig))
		try {
			Process processMain = cmdBig.execute()
			processMain.waitFor()
			isOk = true
		}
		catch (Exception e) {
			e.printStackTrace()
			isOk = false
		}

		//             def waterMarkCmd = ["cmd /c composite -compose atop watermark.png", 'imgs/main_' + fileName, 'imgs/wm_' + fileName]
		//             waterMarkCmd.join(" ").execute()
		//		}
		return isOk
	}

	private String createCmd(String inpath, String action, String options, String outpath) {
		def cmd = ['cmd', '/c', 'convert', inpath, action, options, outpath]

		// If Not Windows or Mac-Development
		if (GrailsUtil.environment == "production" || System.getProperty("os.name").contains("Mac")) {
			cmd = ['convert', inpath, action, options, outpath]
		}

		String execString = cmd.join(" ")
		if(log.debugEnabled) {
			log.debug("execString -> $execString")
		}
		return execString;
	}

	private String createDimentions(int width, int height, int offsetWidth = 0, int offsetHeight = 0) {
		StringBuffer sb = new StringBuffer();
		if (width != null && width > 0) {
			sb.append(width);
		}

		sb.append('x');
		if (height != null && height > 0) {
			sb.append(height)
		}

		if(offsetHeight != null) {
			sb.append("+$offsetHeight")
		}

		if(offsetWidth != null) {
			sb.append("+$offsetWidth")
		}

		return sb.toString()
	}

	private File getUploadPath(File tmpUploadFolder) {
		if (!tmpUploadFolder.exists()) {
			tmpUploadFolder.mkdir()
		}

		return tmpUploadFolder
	}

	private String getOSSpecificString(String sourceString) {
		if (GrailsUtil.environment == "development" && !System.getProperty("os.name").contains("Mac")) {
			sourceString = "\"${sourceString}\""
		}
		return sourceString
	}

	private String checkTempFile(String sourceFilePath, String targetFilePath)
	{
		if(new File(targetFilePath).exists())
		{
			sourceFilePath = targetFilePath
			if(log.debugEnabled) {
				log.debug("Temp file found.")
			}
		}
		return sourceFilePath
	}
}