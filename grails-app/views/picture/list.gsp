
<%@ page import="de.webmpuls.photo_album.Picture" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'picture.label', default: 'Picture')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'picture.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="baseName" title="${message(code: 'picture.baseName.label', default: 'Base Name')}" />
                        
                            <g:sortableColumn property="coverPicture" title="${message(code: 'picture.coverPicture.label', default: 'Cover Picture')}" />
                        
                            <g:sortableColumn property="caption" title="${message(code: 'picture.caption.label', default: 'Caption')}" />
                        
                            <g:sortableColumn property="dateCreated" title="${message(code: 'picture.dateCreated.label', default: 'Date Created')}" />
                        
                            <g:sortableColumn property="URL" title="${message(code: 'picture.URL.label', default: 'URL')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${pictureInstanceList}" status="i" var="pictureInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${pictureInstance.id}">${fieldValue(bean: pictureInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: pictureInstance, field: "baseName")}</td>
                        
                            <td><g:formatBoolean boolean="${pictureInstance.coverPicture}" /></td>
                        
                            <td>${fieldValue(bean: pictureInstance, field: "caption")}</td>
                        
                            <td><g:formatDate date="${pictureInstance.dateCreated}" /></td>
                        
                            <td>${fieldValue(bean: pictureInstance, field: "URL")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${pictureInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
