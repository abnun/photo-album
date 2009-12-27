package de.webmpuls.photo_album

import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import de.webmpuls.photo_album.util.MediaUtils

class MediaTagLib {
	static namespace = "wm_photo_album"

	/**
	 * Gibt den Pfad zu den Bilder zurück.
	 */
	def mediaPath = {
		attrs ->

		if(!attrs['albumId']) {
			throwTagError('Tag [mediaPath] missing required attribute [albumId].')
		}
		else {
			Album tmpAlbum = Album.get(attrs['albumId'])
			
			String albumDate = formatDate(date: tmpAlbum.dateCreated, format: 'ddMMyyyy')

			String tmpPath = "${request.getContextPath()}/${MediaUtils.DEFAULT_UPLOADS_FOLDER}/${MediaUtils.DEFAULT_FOLDER_IMAGE}/${MediaUtils.DEFAULT_FOLDER}_${tmpAlbum.toString()}_${albumDate}/"

			out << tmpPath
		}

		return out
	}

	/**
	 * Gibt den Pfad zu den Bilder zurück.
	 */
	def pathToImage = {
		attrs ->

		Picture tmpPicture = attrs['picture']

		if(!tmpPicture) {
			throwTagError('Tag [pathToImage] missing required attribute [tmpPicture].')
		}

		StringBuilder imageSize = new StringBuilder()
		imageSize << mediaPath(albumId: tmpPicture.album.id)

		String tmpSize = attrs['size']
		if(tmpSize) {
			if(tmpSize  == MediaUtils.BIG) {
				imageSize << tmpPicture.getBigURL()
			}
			else if(tmpSize  == MediaUtils.THUMBNAIL) {
				imageSize << tmpPicture.getThumbNailURL()
			}
			else if(tmpSize  == MediaUtils.PERM) {
				imageSize << tmpPicture.getPermURL()
			}
			else {
				imageSize << tmpPicture.getURL()
			}
		}
		else {
			imageSize << tmpPicture.getURL()
		}

		out << imageSize.toString()

		return out
	}

	/**
     * Tag to pull in the basic CSS
     */
    def static_resources = {
		attrs ->
        if ((attrs.override != 'true') && !Boolean.valueOf(attrs.override)) {
            out << "<link rel=\"stylesheet\" type=\"text/css\" href=\"${resource(dir: '/css', file: 'style.css', plugin: 'photo-album')}\"/>\n"
			out << g.javascript(library: 'jquery')
        }
    }

	/**
     * Tag to pull in the JS and CSS for the highslide gallery
     */
    def gallery_resources = {
		attrs ->
        if ((attrs.override != 'true') && !Boolean.valueOf(attrs.override)) {
            out << "<script type=\"text/javascript\" src=\"${resource(dir: '/highslide', file: 'highslide-with-gallery.js', plugin: 'photo-album')}\"></script>\n"
            out << "<link rel=\"stylesheet\" type=\"text/css\" href=\"${resource(dir: '/highslide', file: 'highslide.css', plugin: 'photo-album')}\" />\n"
            out << "<!--[if lt IE 7]>\n"
            out << "<link rel=\"stylesheet\" type=\"text/css\" href=\"${resource(dir: '/highslide', file: 'highslide-ie6.css', plugin: 'photo-album')}\" />\n"
            out << "<![endif]-->\n"
			out << g.javascript() {
				"hs.graphicsDir = '${resource(dir: '/highslide/graphics/', plugin: 'photo-album')}';\n"
			}
			out << "<script type=\"text/javascript\" src=\"${resource(dir: '/js/highslide', file: 'gallery.js', plugin: 'photo-album')}\"></script>\n"
        }
    }

	/**
     * Tag to pull in the JS and CSS for the uploadify library
     */
    def uploadify_resources = {
		attrs ->
        if ((attrs.override != 'true') && !Boolean.valueOf(attrs.override)) {
            out << "<link rel=\"stylesheet\" type=\"text/css\" href=\"${resource(dir: '/css/uploadify', file: 'uploadify.css', plugin: 'photo-album')}\" />\n"
            out << "<script type=\"text/javascript\" src=\"${resource(dir: '/js/uploadify', file: 'swfobject.js', plugin: 'photo-album')}\"></script>\n"
            out << "<script type=\"text/javascript\" src=\"${resource(dir: '/js/uploadify', file: 'jquery.uploadify.v2.1.0.min.js', plugin: 'photo-album')}\"></script>\n"
        }
    }

	/**
     * Tag to pull in the JS for the jcrop library
     */
    def image_processing_resources = {
		attrs ->
        if ((attrs.override != 'true') && !Boolean.valueOf(attrs.override)) {
			out << "<link rel=\"stylesheet\" type=\"text/css\" href=\"${resource(dir: '/css/jcrop', file: 'jquery.Jcrop.css', plugin: 'photo-album')}\" />\n"
//            out << "<script type=\"text/javascript\" src=\"${resource(dir: '/js/jcrop', file: 'jquery.Jcrop.js')}\"></script>\n"
            out << "<script type=\"text/javascript\" src=\"${resource(dir: '/js/jcrop', file: 'jquery.Jcrop.min.js', plugin: 'photo-album')}\"></script>\n"
        }
    }

	/**
     * Tag to pull in the JS for the instant library
     */
    def image_instant_resources = {
		attrs ->
        if ((attrs.override != 'true') && !Boolean.valueOf(attrs.override)) {
            out << "<script type=\"text/javascript\" src=\"${resource(dir: '/js/instant', file: 'cvi_text_lib.js', plugin: 'photo-album')}\"></script>\n"
            out << "<script type=\"text/javascript\" src=\"${resource(dir: '/js/instant', file: 'instant.js', plugin: 'photo-album')}\"></script>\n"
        }
    }

	/**
     * Tag to pull in the JS for the reflex library
     */
    def image_reflex_resources = {
		attrs ->
        if ((attrs.override != 'true') && !Boolean.valueOf(attrs.override)) {
            out << "<script type=\"text/javascript\" src=\"${resource(dir: '/js/reflex', file: 'reflex.js', plugin: 'photo-album')}\"></script>\n"
        }
    }

	/**
     * Tag to pull in the JS for the glossy library
     */
    def image_glossy_resources = {
		attrs ->
        if ((attrs.override != 'true') && !Boolean.valueOf(attrs.override)) {
            out << "<script type=\"text/javascript\" src=\"${resource(dir: '/js/glossy', file: 'glossy.js', plugin: 'photo-album')}\"></script>\n"
        }
    }
}