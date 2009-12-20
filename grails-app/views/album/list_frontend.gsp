<%@ page import="de.webmpuls.photo_album.Picture; de.webmpuls.photo_album.Album" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
		<wm_photo_album:image_instant_resources />
        <title>Fotoalben anzeigen</title>
    </head>
    <body>
		<div class="nav">
			<span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
		</div>
        <div class="body">
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
				<g:if test="${albumInstanceList}">
					<%
					    boolean showInfo = false
					    albumInstanceList.findAll {
							Album albumInstance ->
							if(albumInstance.visible) {
								showInfo = true
							}
						}
					%>
					<g:if test="${showInfo}">
						<p>
							Um ein Album anzusehen, einfach auf das jeweilige Bild klicken.
						</p>
					</g:if>
					<g:else>
						Es sind noch keine Fotoalben vorhanden ...
					</g:else>
					<table>
						<g:each in="${albumInstanceList}" status="i" var="albumInstance">
								<g:if test="${i % 3 == 0}">
									<tr>
								</g:if>
										<td>
											<div class="thumbwrapper" style="${i % 3 != 0 ? 'margin-left: 80px;' : ''}">
												<g:link action="show" id="${albumInstance.id}">
													<!--<p class="meta">
														<b>${fieldValue(bean: albumInstance, field: 'name')}</b>
													</p>-->
													<%
														List albumPictures = albumInstance.pictures?.asList()

														Picture coverPicture = null
														if(albumPictures && !albumPictures.isEmpty()) {
															coverPicture = albumPictures[0]
														}

														ArrayList coverPictures = new ArrayList()
														albumPictures.findAll {
															if(it.coverPicture == true) {
																coverPictures.add(it)
															}
														}

														if(!coverPictures.isEmpty()) {
															coverPicture = coverPictures[0]
														}

													%>
													<g:if test="${albumPictures && !albumPictures.isEmpty() && coverPicture.exists()}">
														<img class="instant ishadow50 itiltleft historical nocorner itxtalt itxtcol666666" title="${fieldValue(bean: albumInstance, field: 'name')}" src="${wm_photo_album.mediaPath(albumId: albumInstance.id)}${coverPicture.getThumbNailURL()}" alt="${fieldValue(bean: albumInstance, field: 'name')}"/>
													</g:if>
													<g:else>
														<img class="instant ishadow50 itiltleft historical nocorner itxtalt itxtcol666666" title="${fieldValue(bean: albumInstance, field: 'name')}" src="${resource(dir: '/images', file: 'nopicavailable.gif')}" alt="${fieldValue(bean: albumInstance, field: 'name')}"/>
													</g:else>
												</g:link>

												<g:form name="DeleteAlbumForm_${albumInstance.id}" method="post" controller="album" action="delete" id="${albumInstance.id}"></g:form>
												<p class="meta" style="text-align: center;">
													<img src="${resource(dir: '/images/skin', file: 'database_delete.png')}" alt="Album löschen" />
													<a href="javascript: void(0);" onclick="if(confirm('Wirklich löschen?')) { document.forms['DeleteAlbumForm_${albumInstance.id}'].submit();}">
														<span style="text-align: left;">löschen</span>
													</a>
													<img src="${resource(dir: '/images/skin', file: 'database_edit.png')}" alt="Album ändern" />
													<g:link controller="album" action="edit" id="${albumInstance.id}">
														<span style="text-align: right;">ändern</span>
													</g:link>
												</p>

											</div>
								<g:if test="${i > 1 && ((i + 1) % 3) == 0}">
									</tr>
								</g:if>
						</g:each>
					</table>
					<br />
					<br />
					<img src="${resource(dir: '/images/skin', file: 'database_add.png')}" alt="Album erstellen" />
					<g:link controller="album" action="create">
						Neues Album anlegen
					</g:link>
				</g:if>
				<g:else>
					Es sind noch keine Fotoalben vorhanden ...
					<br />
					<br />
					<img src="${resource(dir: '/images/skin', file: 'database_add.png')}" alt="Album erstellen" />
					<g:link controller="album" action="create">
						Neues Album anlegen
					</g:link>
				</g:else>
            </div>
        </div>
    </body>
</html>
