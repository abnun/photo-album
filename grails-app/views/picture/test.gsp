<%@ page import="de.webmpuls.photo_album.Picture" contentType="text/html;charset=UTF-8" %>
<html>
  <head><title>Test page</title></head>
  <body>
  <g:if test="${name}">
	  ${Picture.withAlbumName(name).list()}
  </g:if>
  </body>
</html>