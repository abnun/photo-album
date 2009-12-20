
<%@ page import="de.webmpuls.photo_album.util.MediaUtils; de.webmpuls.photo_album.Picture" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'picture.label', default: 'Picture')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
		<wm_photo_album:image_processing_resources />
		<jq:jquery>
			jQuery('#jcrop_target').Jcrop({
					//onChange: cropImage,
					boxWidth: 400,
					onSelect: cropImage
				});

			function cropImage(c)
			{
				jQuery('#offsetHeight').val(c.y);
				jQuery('#offsetWidth').val(c.x);
				jQuery('#width').val(c.w);
				jQuery('#height').val(c.h);
			};
		</jq:jquery>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${pictureInstance}">
            <div class="errors">
                <g:renderErrors bean="${pictureInstance}" as="list" />
            </div>
            </g:hasErrors>
			<br />
			<g:form controller="picture" action="alterPhoto" id="${pictureInstance.id}" method="post" name="AlterPhotoForm">
				<table>
					<tr>
						<td><img id="jcrop_target" src="${wm_photo_album.pathToImage(picture: pictureInstance)}"/></td>
						<td>
							X:&nbsp;<input type="text" name="offsetHeight" id="offsetHeight" value="" readonly="readonly">
							<br/>
							Y:&nbsp;<input type="text" name="offsetWidth" id="offsetWidth" value="" readonly="readonly">
							<br/>
							Höhe&nbsp;<input type="text" name="height" id="height" value="" readonly="readonly">
							<br/>
							Breite&nbsp;<input type="text" name="width" id="width" value="" readonly="readonly">
						</td>
					</tr>
				</table>
				<g:if test="${Boolean.valueOf(grailsApplication.config.photo_album.picture.show.all.options)}">
					<g:if test="${Boolean.valueOf(grailsApplication.config.photo_album.picture.show.option.rotate)}">
						<br/>
						<br/>
						Um&nbsp;<g:textField name="rotate" value="${params.rotate ?: ''}"/>° im Uhrzeigersinn drehen?
					</g:if>
					<g:if test="${Boolean.valueOf(grailsApplication.config.photo_album.picture.show.option.grayscale)}">
						<br/>
						<br/>
						Graustufen?&nbsp;<g:checkBox name="monochrome"/>
					</g:if>
					<g:if test="${Boolean.valueOf(grailsApplication.config.photo_album.picture.show.option.sepia)}">
						<br/>
						<br/>
						Sepia?&nbsp;<g:checkBox name="sepia"/>
					</g:if>
					<g:if test="${Boolean.valueOf(grailsApplication.config.photo_album.picture.show.option.negation)}">
						<br/>
						<br/>
						Negation?&nbsp;<g:checkBox name="negate"/>
					</g:if>
					<g:if test="${Boolean.valueOf(grailsApplication.config.photo_album.picture.show.option.reduce.red.eyes)}">
						<br/>
						<br/>
						Rote Augen reduzieren?&nbsp;<g:checkBox name="reduceRedEyes"/>
					</g:if>
					<g:if test="${Boolean.valueOf(grailsApplication.config.photo_album.picture.show.option.reset)}">
						<br/>
						<br/>
						Ausschneiden?&nbsp;<g:checkBox name="onlyCrop"/>
					</g:if>
					<g:if test="${Boolean.valueOf(grailsApplication.config.photo_album.picture.show.option.crop)}">
						<br/>
						<br/>
						<g:link action="resetPhoto" id="${pictureInstance.id}">Foto zurücksetzen</g:link>
					</g:if>
				</g:if>
				<br/>
				<br/>
				<g:submitButton name="alterSubmit" value="${g.message(code: 'picture.alter.submit', default: 'Anwenden')}"/>
			</g:form>
			<br />
			<br />
            <g:form method="post" >
                <g:hiddenField name="id" value="${pictureInstance?.id}" />
                <g:hiddenField name="version" value="${pictureInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="baseName"><g:message code="picture.baseName.label" default="Base Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: pictureInstance, field: 'baseName', 'errors')}">
                                    <g:textField name="baseName" value="${pictureInstance?.baseName}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="coverPicture"><g:message code="picture.coverPicture.label" default="Cover Picture" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: pictureInstance, field: 'coverPicture', 'errors')}">
                                    <g:checkBox name="coverPicture" value="${pictureInstance?.coverPicture}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="caption"><g:message code="picture.caption.label" default="Caption" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: pictureInstance, field: 'caption', 'errors')}">
                                    <g:textField name="caption" value="${pictureInstance?.caption}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="album"><g:message code="picture.album.label" default="Album" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: pictureInstance, field: 'album', 'errors')}">
                                    <g:select name="album.id" from="${de.webmpuls.photo_album.Album.list()}" optionKey="id" value="${pictureInstance?.album?.id}"  />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                    <span class="button"><input type="button" value="Zurück zum Album" onclick="location.href = '${createLink(controller: 'album', action: 'show', id: pictureInstance?.album?.id)}';">
                </div>
            </g:form>
        </div>
    </body>
</html>
