class UrlMappings {
    static mappings = {
      "/$controller/$action?/$id?"{
	      constraints {
			 // apply constraints here
		  }
	  }
      "/"(controller: "album", action: 'list_frontend')
	  "500"(view: '/error')
	}
}
