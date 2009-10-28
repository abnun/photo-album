package de.webmpuls.photo_album

import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import de.webmpuls.photo_album.util.MediaUtils

class MediaTagLib
{
	static namespace = "wm_photo_album"

	/**
	 * Gibt den Pfad zu den Bilder zurück.
	 */
	def mediaPath =
	{
		attrs ->

		if(!attrs['albumId'])
		{
			throwTagError('Tag [mediaPath] missing required attribute [albumId].')
		}
		else
		{
			Album tmpAlbum = Album.get(attrs['albumId'])
			
			String albumDate = formatDate(date: tmpAlbum.dateCreated, format: 'ddMMyyyy')

			String tmpPath = "${request.getContextPath()}/${MediaUtils.DEFAULT_UPLOADS_FOLDER}/${MediaUtils.DEFAULT_FOLDER_IMAGE}/${MediaUtils.DEFAULT_FOLDER}_${tmpAlbum.toString()}_${albumDate}/"

			out << tmpPath
		}

		return out
	}

	/**
     * Tag to pull in the CSS
     */
    def resources = { attrs ->
        if ((attrs.override != 'true') && !Boolean.valueOf(attrs.override)) {
            out << "<link rel=\"stylesheet\" type=\"text/css\" href=\"${resource(dir:pluginContextPath+'/css', file:'style.css')}\"/>"
        }
    }

	/**
     * Tag to pull in the CSS for the highslide gallery
     */
    def gallery = { attrs ->
        if ((attrs.override != 'true') && !Boolean.valueOf(attrs.override)) {
            out << "<script type=\"text/javascript\" src=\"${resource(dir: pluginContextPath + '/highslide', file: 'highslide-with-gallery.js')}\"></script>"
            out << "<link rel=\"stylesheet\" type=\"text/css\" href=\"${resource(dir: pluginContextPath + '/highslide', file: 'highslide.css')}\" />"
            out << "<!--[if lt IE 7]>"
            out << "<link rel=\"stylesheet\" type=\"text/css\" href=\"${resource(dir: pluginContextPath + '/highslide', file: 'highslide-ie6.css')}\" />"
            out << "<![endif]-->"
            out << "<link rel=\"stylesheet\" type=\"text/css\" href=\"${resource(dir:pluginContextPath + '/js/highslide', file: 'gallery.js')}\"/>"
        }
    }

}
