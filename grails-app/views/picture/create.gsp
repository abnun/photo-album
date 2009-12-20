<%@ page import="de.webmpuls.photo_album.util.MediaUtils; de.webmpuls.photo_album.Album; de.webmpuls.photo_album.Picture" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
		<wm_photo_album:uploadify_resources />

		<%
		    Album tmpAlbum = Album.get(params.album?.id)

			String albumDate = ""
			if(tmpAlbum) {
				albumDate = formatDate(date: tmpAlbum.dateCreated, format: 'ddMMyyyy')
			}

		%>
		<jq:jquery>

			$('#albumFotos').uploadify({
				'uploader'  		: '${resource(dir: '/js/uploadify', file: 'uploadify.swf')}',
				'script'    		: '${createLink(controller: 'picture', action: 'uploadPhotos')}',
				'cancelImg' 		: '${resource(dir: '/images', file: 'cancel.png')}',
				'auto'      		: false,
				'fileDataName'		: 'fotos',
				'multi'				: true,
				'method'			: 'POST',
				'buttonText'		: 'Bilder waehlen',
				'fileDesc'			: 'Erlaubte Datei-Typen',
				'fileExt'			: '*.jpg;*.gif;*.JPG;*.jpeg;*.JPEG;*.GIF;*.png;*.PNG;*.avi;*.AVI',
				'folder'    		: '/${MediaUtils.DEFAULT_FOLDER}_${tmpAlbum.toString()}_${albumDate}',
				%{--'onComplete'		: function (evt, queueID, fileObj, response, data) { alert("Response: "+response);},--}%
				'onAllComplete'	: function(event, uploadObj) { alert(uploadObj.filesUploaded + ' Bild(er) hochgeladen. Anzahl der Fehler: ' + uploadObj.errors);},
				'onError'			: function(event, ID, fileObj, errorObj) { alert("Fehler: "+errorObj.info);}
			});

			$('#startUpload').click(function(){
			   	var queryString = { 'album.id': '${params.album?.id}', 'rotate': $('#rotate').val() };
   				$('#albumFotos').uploadifySettings('scriptData', queryString);
        		$('#albumFotos').uploadifyUpload();
			 });

		</jq:jquery>

	<title>Bilder hochladen</title>
    </head>
    <body>
        <div class="body">
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${pictureInstance}">
            <div class="errors">
                <g:renderErrors bean="${pictureInstance}" as="list" />
            </div>
            </g:hasErrors>

			Hier kannst du ein oder mehrere Bilder zum Album '${Album.get(params.album?.id)?.name}' hochladen.
			<br />
			<br />
			<input type="file" name="albumFotos" id="albumFotos" />
			<br />
			Um <g:textField name="rotate" />° im Uhrzeigersinn drehen?
			<br />
			<br />
			<a id="startUpload" href="javascript:void(0);">Upload starten</a> | <a href="javascript:$('#albumFotos').uploadifyClearQueue();">Queue löschen</a>
			<br />
			<br />
			<g:link controller="album" action="show" id="${params.album?.id}"><- Zurück zum Album</g:link>
        </div>
    </body>
</html>