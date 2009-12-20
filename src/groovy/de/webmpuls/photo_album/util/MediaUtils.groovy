package de.webmpuls.photo_album.util

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import grails.util.GrailsUtil
import java.text.SimpleDateFormat

/**
 * User: markus.mueller (markus2.mueller@bechtle.com)
 * Date: 10.08.2009
 * Time: 12:26:36
 */
class MediaUtils {

	public static final String PERM = "-perm"
	public static final String THUMBNAIL = "-thumbnail"
	public static final String NORMAL = "-normal"
	public static final String BIG = "-big"
	public static final String TEMP = "-temp"
	public static final String CROP = "-crop"
	public static final String SUFFIX = ".jpg"

	public static final String DEFAULT_FOLDER = "album"
	public static final String DEFAULT_FOLDER_IMAGE = "Image"
	public static final String DEFAULT_UPLOADS_FOLDER = "uploads"

	private static Log log = LogFactory.getLog(MediaUtils.class);

	public static String getBaseName(String fileName) {
		String baseName = null
		if (fileName && fileName.indexOf(".") != -1) {
			baseName = fileName.substring(0, fileName.lastIndexOf("."))
		}

		if (log.debugEnabled) {
			log.debug("Basename is: '$baseName'")
		}

		return baseName;
	}

	public static String getExtension(String fileName) {
		String extension = SUFFIX
		if (fileName && fileName.indexOf(".") != -1) {
			extension = fileName.substring(fileName.lastIndexOf("."), fileName.size())
		}

		if (log.debugEnabled) {
			log.debug("File extension is: '$extension'")
		}

		return extension;
	}

	public static void printSysAndEnvVariables() {
		if(log.debugEnabled)
		{
			log.debug("OS -> ${System.getProperty("os.name")}")

			if (GrailsUtil.environment == "development" && !System.getProperty("os.name").contains("Mac")) {
				log.debug("PATH -> ${System.getenv().get("Path")}")
				log.debug("USER -> ${System.getenv().get("USERNAME")}")
			}
			else {
				log.debug("PATH -> ${System.getenv().get("PATH")}")
				log.debug("USER -> ${System.getenv().get("USER")}")
				log.debug("SHELL -> ${System.getenv().get("SHELL")}")
			}
		}
	}

	public static String formatDateForDisk(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy")

		return simpleDateFormat.format(date)
	}

	public static void copyFile(String sourceFile, String target, boolean isTargetDirectory = false) {
		File checkFile = new File(sourceFile)

		if(checkFile.exists()) {
			AntBuilder antBuilder = new AntBuilder()
			if(isTargetDirectory) {
				antBuilder.copy(file: sourceFile , toDir: target, overwrite: true, failonerror: true, verbose: true)
			}
			else {
				antBuilder.copy(file: sourceFile , toFile: target, overwrite: true, failonerror: true, verbose: true)
			}
		}
		else {
			log.error("Source file '$checkFile' does not exist, cannot copy file!")
		}
	}

	public static void moveDir(String sourceDir, String targetDir) {
		File checkDir = new File(sourceDir)

		if(checkDir.exists()) {
			AntBuilder antBuilder = new AntBuilder()
			antBuilder.move(file: sourceDir , toFile: targetDir, overwrite: true, failonerror: true, verbose: true)
		}
		else {
			log.error("Source dir '$checkDir' does not exist, cannot move!")
		}
	}
}