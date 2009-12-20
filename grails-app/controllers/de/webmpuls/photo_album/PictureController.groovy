package de.webmpuls.photo_album

import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse

class PictureController {

	PictureService pictureService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        //params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        [pictureInstanceList: Picture.list(params), pictureInstanceTotal: Picture.count()]
    }

    def create = {
        def pictureInstance = new Picture()
        pictureInstance.properties = params
        return [pictureInstance: pictureInstance]
    }

    def save = {
        def pictureInstance = new Picture(params)
        if (pictureInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'picture.label', default: 'Picture'), pictureInstance.id])}"
            redirect(action: "show", id: pictureInstance.id)
        }
        else {
            render(view: "create", model: [pictureInstance: pictureInstance])
        }
    }

    def show = {
        def pictureInstance = Picture.get(params.id)
        if (!pictureInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'picture.label', default: 'Picture'), params.id])}"
            redirect(action: "list")
        }
        else {
            [pictureInstance: pictureInstance]
        }
    }

    def edit = {
        def pictureInstance = Picture.get(params.id)
        if (!pictureInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'picture.label', default: 'Picture'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [pictureInstance: pictureInstance]
        }
    }

    def update = {
        def pictureInstance = Picture.get(params.id)
        if (pictureInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (pictureInstance.version > version) {
                    
                    pictureInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'picture.label', default: 'Picture')], "Another user has updated this Picture while you were editing")
                    render(view: "edit", model: [pictureInstance: pictureInstance])
                    return
                }
            }

			Album oldAlbum = pictureInstance.album
			String oldAlbumId = oldAlbum.id

            pictureInstance.properties = params
            if (!pictureInstance.hasErrors() && pictureInstance.save(flush: true)) {

				if (params.album.id != oldAlbumId) {
					if (!pictureService.movePictureToAlbumOnDisk(pictureInstance, oldAlbum, params.album.id)) {
						flash.message = "${message(code: 'default.updated.message', args: [message(code: 'picture.label', default: 'Picture'), pictureInstance.id])}"
						render(view: "edit", model: [pictureInstance: pictureInstance])
					}
				}

                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'picture.label', default: 'Picture'), pictureInstance.id])}"
                redirect(action: "show", id: pictureInstance.id)
            }
            else {
                render(view: "edit", model: [pictureInstance: pictureInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'picture.label', default: 'Picture'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def pictureInstance = Picture.get(params.id)
        if (pictureInstance) {
            try {
                pictureInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'picture.label', default: 'Picture'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'picture.label', default: 'Picture'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'picture.label', default: 'Picture'), params.id])}"
            redirect(action: "list")
        }
    }

	def uploadPhotos = {
		if(log.debugEnabled) {
			log.debug("params -> $params")
		}

		if (request instanceof MultipartHttpServletRequest) {
			MultipartFile foto = request.getFile('fotos')
			if (!foto.empty) {
				if(pictureService.uploadPhotos(foto, params.folder, params.rotate, params['Filename'], params.album.id)) {
					//response.sendError(200, 'Foto erfolgreich hochgeladen.');
					response.setStatus(HttpServletResponse.SC_OK)
					response.outputStream << "Foto erfolgreich hochgeladen."
					response.outputStream.flush()
					return false
				}
				else {
					//response.sendError(500, 'Foto konnte nicht geladen werden.');
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					response.outputStream << "Foto konnte nicht geladen werden."
					response.outputStream.flush()
					return false
				}
			}
			else {
				flash.message = 'file cannot be empty'
				redirect(controller: 'album', action: 'show', id: params.album.id)
				return false
			}
		}
		else {
			//response.sendError(500, 'Foto konnte nicht geladen werden.');
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
			response.outputStream << "Foto konnte nicht geladen werden."
			response.outputStream.flush()
			return false
		}
	}

	def alterPhoto = {
		if(log.debugEnabled) {
			log.debug("params -> $params")
		}

		boolean isOk = false

		if (params.id) {
			Picture tmpPicture = Picture.get(params.id)

			if(params.rotate)
			{
				isOk = pictureService.rotatePhoto(tmpPicture, params.rotate)
			}

			if(params.width && params.height && params.offsetHeight && params.offsetWidth)
			{
				boolean onlyCrop = params.onlyCrop ?: false

				if(onlyCrop)
				{
					isOk = pictureService.cropFoto(tmpPicture, params.width, params.height, params.offsetHeight, params.offsetWidth)
				}

				if (params.monochrome)
				{
					isOk = pictureService.monochromeFoto(tmpPicture, params.width, params.height, params.offsetHeight, params.offsetWidth, onlyCrop)
				}

				if (params.sepia)
				{
					isOk = pictureService.sepiaFoto(tmpPicture, params.width, params.height, params.offsetHeight, params.offsetWidth, onlyCrop)
				}

				if (params.negate)
				{
					isOk = pictureService.negateFoto(tmpPicture, params.width, params.height, params.offsetHeight, params.offsetWidth, onlyCrop)
				}

				if (params.reduceRedEyes)
				{
					isOk = pictureService.reduceRedEyesOnFoto(tmpPicture, params.width, params.height, params.offsetHeight, params.offsetWidth, onlyCrop)
				}
			}
			else
			{
				if (params.monochrome)
				{
					isOk = pictureService.monochromeFoto(tmpPicture, "", "", "", "", true)
				}

				if (params.sepia)
				{
					isOk = pictureService.sepiaFoto(tmpPicture, "", "", "", "", true)
				}

				if (params.negate)
				{
					isOk = pictureService.negateFoto(tmpPicture, "", "", "", "", true)
				}

				// doesn't make sense
				/*if (params.reduceRedEyes)
				{
					isOk = pictureService.reduceRedEyesOnFoto(tmpPicture, "", "", "", "", true)
				}*/
			}

			if (isOk) {
				flash.message = "Picture erfolgreich geändert."
				redirect(action: 'edit', id: tmpPicture.id)
			}
			else {
				flash.message = "Picture konnte nicht geändert werden."
				redirect(action: 'edit', id: params.id, params: [rotate: params.rotate])
			}
		}
		else {
			flash.message = "Es wurden keine Einstellungen zum Ändern getroffen."
			redirect(action: 'edit', id: params.id, params: [rotate: params.rotate])
		}
	}

	def resetPhoto = {
		if(log.debugEnabled) {
			log.debug("params -> $params")
		}

		boolean isOk = false

		if (params.id) {
			Picture tmpPicture = Picture.get(params.id)

			isOk = pictureService.resetPhoto(tmpPicture)

		if (isOk) {
				flash.message = "Picture erfolgreich zurückgesetzt."
				redirect(action: 'edit', id: tmpPicture.id)
			}
			else {
				flash.message = "Picture konnte nicht zurückgesetzt werden."
				redirect(action: 'edit', id: params.id, params: [rotate: params.rotate])
			}
		}
		else {
			flash.message = "Es wurde keine Aktion angefordert."
			redirect(action: 'edit', id: params.id, params: [rotate: params.rotate])
		}
	}
}