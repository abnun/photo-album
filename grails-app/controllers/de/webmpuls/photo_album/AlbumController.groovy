package de.webmpuls.photo_album

import de.webmpuls.photo_album.util.MediaUtils

class AlbumController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	AlbumService albumService

    def index = {
        redirect(action: "list_frontend", params: params)
    }

    def list = {
        //params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        [albumInstanceList: Album.list(params), albumInstanceTotal: Album.count()]
    }

    def create = {
        def albumInstance = new Album()
        albumInstance.properties = params
        return [albumInstance: albumInstance]
    }

    def save = {
        def albumInstance = new Album(params)
        if (albumInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'album.label', default: 'Album'), albumInstance.id])}"
            redirect(action: "show", id: albumInstance.id)
        }
        else {
            render(view: "create", model: [albumInstance: albumInstance])
        }
    }

    def show = {
        def albumInstance = Album.get(params.id)
        if (!albumInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'album.label', default: 'Album'), params.id])}"
            redirect(action: "list")
        }
        else {
            [albumInstance: albumInstance]
        }
    }

    def edit = {
        def albumInstance = Album.get(params.id)
        if (!albumInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'album.label', default: 'Album'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [albumInstance: albumInstance]
        }
    }

    def update = {
        def albumInstance = Album.get(params.id)
        if (albumInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (albumInstance.version > version) {
                    
                    albumInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'album.label', default: 'Album')], "Another user has updated this Album while you were editing")
                    render(view: "edit", model: [albumInstance: albumInstance])
                    return
                }
            }

			String oldAlbumName = albumInstance.toString()

			albumInstance.properties = params
            if (!albumInstance.hasErrors() && albumInstance.save(flush: true)) {

				if (params.name != oldAlbumName) {
					println("Trying to rename album directory")
					if (!albumService.renameAlbumDirectory(albumInstance, oldAlbumName)) {
						flash.message = "${message(code: 'default.updated.message', args: [message(code: 'album.label', default: 'Album'), albumInstance.id])}"
						render(view: "edit", model: [albumInstance: albumInstance])
					}
				}

                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'album.label', default: 'Album'), albumInstance.id])}"
                redirect(action: "show", id: albumInstance.id)
            }
            else {
                render(view: "edit", model: [albumInstance: albumInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'album.label', default: 'Album'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def albumInstance = Album.get(params.id)
        if (albumInstance) {
            try {
                albumInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'album.label', default: 'Album'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'album.label', default: 'Album'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'album.label', default: 'Album'), params.id])}"
            redirect(action: "list")
        }
    }

	def list_frontend = {
        //params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        [albumInstanceList: Album.list(params), albumInstanceTotal: Album.count()]
    }
}
