<%@ page import="de.webmpuls.photo_album.Album" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
		<wm_photo_album:gallery_resources />
		<wm_photo_album:image_instant_resources />
		<wm_photo_album:image_reflex_resources />
        <title>Album '${albumInstance.name}'</title>
    </head>
    <body>
		<div class="nav">
			<span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
		</div>
        <div class="body">
			<div class="post">

				<h1 class="title">${albumInstance.name}</h1>
				<g:if test="${albumInstance.description}">
					<p class="meta">${albumInstance.description}</p>
				</g:if>

			</div>

            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>

            <div class="dialog">
				<%
				    def albumPicturesList = albumInstance.pictures
				%>
				<g:if test="${albumPicturesList && !albumPicturesList.isEmpty()}">
					<p>
						Um die Bilder zu vergrößern einfach drauf klicken. <g:if test="${albumPicturesList.size() > 1}">Nach dem Vergrößern besteht zusätzlich die Möglichkeit eine Art Dia-Show zu starten (dazu muss man den Knopf "Abspielen" klicken).</g:if>
					</p>
					<table>
						<g:each in="${albumPicturesList}" status="i" var="pictureInstance">
							<g:if test="${pictureInstance.exists()}">
								<g:if test="${i % 3 == 0}">
									<tr>
								</g:if>
								<td>
									<div class="highslide-gallery" style="margin: auto">
										<br />
										<jq:jquery>
											jQuery.preloadImages("${wm_photo_album.mediaPath(albumId: albumInstance.id)}${pictureInstance.getThumbNailURL()}", "${wm_photo_album.mediaPath(albumId: albumInstance.id)}${pictureInstance.getBigURL()}");
										</jq:jquery>
										<a class='highslide' href='${wm_photo_album.mediaPath(albumId: albumInstance.id)}${pictureInstance.getBigURL()}' onclick="return hs.expand(this, { captionText: '${pictureInstance.caption}' })">
											<img class="instant ishadow50 itiltleft historical nocorner itxtalt itxtcol666666" src='${wm_photo_album.mediaPath(albumId: albumInstance.id)}${pictureInstance.getThumbNailURL()}' alt='${pictureInstance.caption}'/>
										</a>
										<g:form name="DeletePictureForm_${pictureInstance.id}" method="post" url="${createLink(controller: 'picture', action: 'delete', params: [id: pictureInstance.id, albumId: albumInstance.id])}"></g:form>
										<p class="meta" style="text-align: center;">
											<img src="${resource(dir: '/images/skin', file: 'database_delete.png')}" alt="Bild löschen"/>
											<a href="javascript: void(0);" onclick="if(confirm('Wirklich löschen?')) { document.forms['DeletePictureForm_${pictureInstance.id}'].submit();}">
												<span style="text-align: left;">löschen</span>
											</a>
											<img src="${resource(dir: '/images/skin', file: 'database_edit.png')}" alt="Bild ändern" />
											<g:link controller="picture" action="edit" params="[id: pictureInstance.id, albumId: albumInstance.id]">
												ändern
											</g:link>
										</p>
									</div>
								</td>
								<g:if test="${i > 1 && i % 3 == 0}">
								</g:if>
							</g:if>
						</g:each>
						</tr>
					</table>
					<br />
					<br />
					<img src="${resource(dir: '/images/skin', file: 'database_add.png')}" alt="Bilder hochladen" />
					<g:link controller="picture" action="create" params="['album.id': albumInstance.id]">
						Weitere Bilder hochladen
					</g:link>
				</g:if>
				<g:else>
					<br />
					Es sind noch keine Bilder in diesem Album vorhanden ...
					<br />
					<br />
					<img src="${resource(dir: '/images/skin', file: 'database_add.png')}" alt="Bilder hochladen" />
					<g:link controller="picture" action="create" params="['album.id': albumInstance.id]">
						Neue Bilder hochladen
					</g:link>
				</g:else>
            </div>
        </div>
    </body>
</html>