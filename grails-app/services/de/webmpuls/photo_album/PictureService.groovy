package de.webmpuls.photo_album

import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import org.springframework.web.multipart.MultipartFile
import de.webmpuls.photo_album.util.MediaUtils
import grails.util.GrailsUtil

class PictureService implements ApplicationContextAware
{

    boolean transactional = true

	static scope = "session"

	ApplicationContext applicationContext

    public boolean uploadFotos(MultipartFile foto, String uploadFolder, String rotateString, String fileName, String albumId)
	{
		File tmpUploadFolder = null

		if (uploadFolder)
		{
			tmpUploadFolder = applicationContext.
					getResource(File.separator + MediaUtils.DEFAULT_UPLOADS_FOLDER + File.separator + MediaUtils.DEFAULT_FOLDER_IMAGE + uploadFolder).getFile()

			uploadFolder = getUploadPath(tmpUploadFolder).getAbsolutePath()
			if(log.debugEnabled)
			{
				log.debug(uploadFolder)
			}
		}

		Picture tmpPicture = new Picture(baseName: fileName, album: Album.get(albumId))

		String newFilePath = "${uploadFolder}${File.separator}${tmpPicture.getTempURL()}"

		File newFile = new File(newFilePath)

		foto.transferTo(newFile)
		Thread.sleep(2000)

		String rotateDegrees = rotateString

		boolean isOk = processImg(newFilePath, uploadFolder, tmpUploadFolder, tmpPicture, rotateDegrees)

		if (isOk && newFile.exists() && newFile.size() > 0)
		{
			Picture newPicture = new Picture(baseName: fileName, album: Album.get(albumId))
			Picture existingPicture = Picture.findByBaseName(fileName)
			if (existingPicture)
			{
				newPicture = existingPicture
			}

			if (newPicture.save(flush: true))
			{
				if(log.debugEnabled)
				{
					log.debug("Foto '${newPicture.getURL()}' erfolgreich gespeichert.")
				}
				return true
			}
			else
			{
				newPicture.errors.allErrors.each
				{
					if(log.errorEnabled)
					{
						log.error(it)
					}
				}
				return false
			}
		}
		else
		{
			return false
		}
	}

	public boolean rotateFoto(Picture picture, String rotateString)
	{
		boolean isOk = false
		if (picture && rotateString)
		{
			Album tmpAlbum = picture.album

			String albumDate = MediaUtils.formatDateForDisk(tmpAlbum.dateCreated)

			File tmpUploadFolder = applicationContext.getResource("${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${tmpAlbum.toString()}_${albumDate}").getFile()
			String uploadFolder = getUploadPath(tmpUploadFolder).getAbsolutePath()
			if(log.debugEnabled)
			{
				log.debug(uploadFolder)
			}
			String newFilePath = "${uploadFolder}${File.separator}${picture.getTempURL()}"
			String rotateDegrees = rotateString

			isOk = processImg(newFilePath, uploadFolder, tmpUploadFolder, picture, rotateDegrees)
		}
		return isOk
	}

	private boolean movePictureToAlbumOnDisk(Picture picture, String albumId)
	{
		boolean isOk = false

		Album tmpAlbum = Album.get(albumId)
		if (picture && tmpAlbum)
		{
			Album pictureAlbum = picture.album

			String albumDate = MediaUtils.formatDateForDisk(tmpAlbum.dateCreated)

			String pictureAlbumDate = MediaUtils.formatDateForDisk(pictureAlbum.dateCreated)
			File tmpSourceFolder = applicationContext.
					getResource("${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${pictureAlbum.toString()}_${pictureAlbumDate}").getFile()

			File tmpTargetFolder = applicationContext.
					getResource("${File.separator}${MediaUtils.DEFAULT_UPLOADS_FOLDER}${File.separator}${MediaUtils.DEFAULT_FOLDER_IMAGE}${File.separator}${MediaUtils.DEFAULT_FOLDER}_${tmpAlbum.toString()}_${albumDate}").getFile()

			String originalNormal = "${tmpSourceFolder.getAbsolutePath()}${File.separator}${picture.getURL()}"
			String originalBig = "${tmpSourceFolder.getAbsolutePath()}${File.separator}${picture.getBigURL()}"
			String originalThumbnail = "${tmpSourceFolder.getAbsolutePath()}${File.separator}${picture.getThumbNailURL()}"
			String originalTemp = "${tmpSourceFolder.getAbsolutePath()}${File.separator}${picture.getTempURL()}"

			String target = "${tmpTargetFolder.getAbsolutePath()}${File.separator}"

			try
			{
				MediaUtils.copyFile(originalNormal, target, true)
				isOk = true
			}
			catch (Exception e)
			{
				e.printStackTrace()
				isOk = false
			}

			try
			{
				MediaUtils.copyFile(originalBig, target, true)
				isOk = true
			}
			catch (Exception e)
			{
				e.printStackTrace()
				isOk = false
			}

			try
			{
				MediaUtils.copyFile(originalThumbnail, target, true)
				isOk = true
			}
			catch (Exception e)
			{
				e.printStackTrace()
				isOk = false
			}

			try
			{
				MediaUtils.copyFile(originalTemp, target, true)
				isOk = true
			}
			catch (Exception e)
			{
				e.printStackTrace()
				isOk = false
			}
		}
		return isOk
	}

	private boolean processImg(String fileName, String uploadFolder, File tmpUploadFolder, Picture targetFile, String rotateDegrees)
	{
		final String original = fileName

		MediaUtils.printSysAndEnvVariables()

		boolean isOk = false

		final String uploadPathBig = "${getUploadPath(tmpUploadFolder).getAbsolutePath()}${File.separator}${targetFile.getBigURL()}"
		final String uploadPathNormal = "${getUploadPath(tmpUploadFolder).getAbsolutePath()}${File.separator}${targetFile.getURL()}"
		final String uploadPathThumbNail = "${getUploadPath(tmpUploadFolder).getAbsolutePath()}${File.separator}${targetFile.getThumbNailURL()}"

		Process process = null

		File originalFile = new File(original)
		if (rotateDegrees && originalFile.exists())
		{
			String cmdRotate = "convert -rotate ${rotateDegrees} ${getOSSpecificString(original)} ${getOSSpecificString(uploadPathNormal)}"

			if(log.debugEnabled)
			{
				log.debug(cmdRotate)
			}
			try
			{
				process = cmdRotate.execute()
				//println process.in.text
				process.waitFor()
				Thread.sleep(2000)
				isOk = true
			}
			catch (Exception e)
			{
				e.printStackTrace()
				isOk = false
			}
		}
		else
		{
			try
			{
				MediaUtils.copyFile(original, uploadPathNormal)
				isOk = true
			}
			catch (Exception e)
			{
				e.printStackTrace()
				isOk = false
			}
		}

		String cmdThumbNail = createCmd(getOSSpecificString(uploadPathNormal), MediaUtils.THUMBNAIL, createDimentions(150, 140), getOSSpecificString(uploadPathThumbNail))
		try
		{
			process = cmdThumbNail.execute()
			process.waitFor()
			isOk = true
		}
		catch (Exception e)
		{
			e.printStackTrace()
			isOk = false
		}

		String cmdBig = createCmd(getOSSpecificString(uploadPathNormal), MediaUtils.THUMBNAIL, createDimentions(400, 0), getOSSpecificString(uploadPathBig))
		try
		{
			Process processMain = cmdBig.execute()
			processMain.waitFor()
			isOk = true
		}
		catch (Exception e)
		{
			e.printStackTrace()
			isOk = false
		}

		//             def waterMarkCmd = ["cmd /c composite -compose atop watermark.png", 'imgs/main_' + fileName, 'imgs/wm_' + fileName]
		//             waterMarkCmd.join(" ").execute()
		//		}
		return isOk
	}

	private String createCmd(String inpath, String action, String options, String outpath)
	{
		def cmd = ['cmd', '/c', 'convert', inpath, action, options, outpath]

		// If Not Windows or Mac-Development
		if (GrailsUtil.environment == "production" || System.getProperty("os.name").contains("Mac"))
		{
			cmd = ['convert', inpath, action, options, outpath]
		}

		String execString = cmd.join(" ")
		if(log.debugEnabled)
		{
			log.debug("execString -> $execString")
		}
		return execString;
	}

	private String createDimentions(int width, int height)
	{
		StringBuffer sb = new StringBuffer();
		if (width > 0)
		{
			sb.append(width);
		}

		sb.append('x');
		if (height > 0)
		{
			sb.append(height)
		}

		return sb.toString()
	}

	private File getUploadPath(File tmpUploadFolder)
	{
		if (!tmpUploadFolder.exists())
		{
			tmpUploadFolder.mkdir()
		}

		return tmpUploadFolder
	}

	private String getOSSpecificString(String sourceString)
	{
		if (GrailsUtil.environment == "development" && !System.getProperty("os.name").contains("Mac"))
		{
			sourceString = "\"${sourceString}\""
		}
		return sourceString
	}
}
