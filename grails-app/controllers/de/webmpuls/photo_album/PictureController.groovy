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

			def oldAlbumId = pictureInstance.album.id

            pictureInstance.properties = params
            if (!pictureInstance.hasErrors() && pictureInstance.save(flush: true)) {

				if (params.album.id != oldAlbumId)
				{
					if (!pictureService.movePictureToAlbumOnDisk(pictureInstance, params.album.id))
					{
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

	def uploadFotos =
	{
		if(log.debugEnabled)
		{
			log.debug("params -> $params")
		}

		if (request instanceof MultipartHttpServletRequest)
		{
			MultipartFile foto = request.getFile('fotos')
			if (!foto.empty)
			{
				if(pictureService.uploadFotos(foto, params.folder, params.rotate, params['Filename'], params.album.id))
				{
					//response.sendError(200, 'Foto erfolgreich hochgeladen.');
					response.setStatus(HttpServletResponse.SC_OK)
					response.outputStream << "Foto erfolgreich hochgeladen."
					response.outputStream.flush()
					return false
				}
				else
				{
					//response.sendError(500, 'Foto konnte nicht geladen werden.');
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					response.outputStream << "Foto konnte nicht geladen werden."
					response.outputStream.flush()
					return false
				}
			}
			else
			{
				flash.message = 'file cannot be empty'
				redirect(controller: 'album', action: 'show', id: params.album.id)
				return false
			}
		}
		else
		{
			//response.sendError(500, 'Foto konnte nicht geladen werden.');
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
			response.outputStream << "Foto konnte nicht geladen werden."
			response.outputStream.flush()
			return false
		}
	}

	def rotateFoto =
	{
		if(log.debugEnabled)
		{
			log.debug("params -> $params")
		}

		if (params.id)
		{
			Picture tmpPicture = Picture.get(params.id)

			boolean isOk = pictureService.rotateFoto(tmpPicture, params.rotate)

			if (isOk)
			{
				flash.message = "Picture erfolgreich gedreht."
				redirect(action: 'edit', id: tmpPicture.id)
			}
			else
			{
				flash.message = "Picture konnte nicht gedreht werden."
				redirect(action: 'edit', id: params.id, params: [rotate: params.rotate])
			}
		}
	}
}
