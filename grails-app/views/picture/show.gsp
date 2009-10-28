
<%@ page import="de.webmpuls.photo_album.Picture" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'picture.label', default: 'Picture')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="picture.id.label" default="Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: pictureInstance, field: "id")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="picture.baseName.label" default="Base Name" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: pictureInstance, field: "baseName")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="picture.coverPicture.label" default="Cover Picture" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${pictureInstance?.coverPicture}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="picture.caption.label" default="Caption" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: pictureInstance, field: "caption")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="picture.dateCreated.label" default="Date Created" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${pictureInstance?.dateCreated}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="picture.URL.label" default="URL" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: pictureInstance, field: "URL")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="picture.thumbNailURL.label" default="Thumb Nail URL" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: pictureInstance, field: "thumbNailURL")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="picture.bigURL.label" default="Big URL" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: pictureInstance, field: "bigURL")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="picture.tempURL.label" default="Temp URL" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: pictureInstance, field: "tempURL")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="picture.album.label" default="Album" /></td>
                            
                            <td valign="top" class="value"><g:link controller="album" action="show" id="${pictureInstance?.album?.id}">${pictureInstance?.album?.encodeAsHTML()}</g:link></td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${pictureInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
