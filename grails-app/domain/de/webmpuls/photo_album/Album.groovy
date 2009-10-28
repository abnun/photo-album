package de.webmpuls.photo_album

class Album
{
	static hasMany = [pictures: Picture]

	Date dateCreated

	String name
	String description
	boolean visible = true

	static mapping =
	{
		sort("dateCreated")
		order("desc")
		cache(true)
		pictures(sort: 'dateCreated', order: 'desc')
	}

    static constraints =
	{
		name(blank: false, unique: true, validator: { String v ->
			boolean isValid = (v ==~ /[\w\-]+/)
			println("validation for '$v' evaluates to $isValid")
			return isValid
		})
		visible()
		description(widget: 'textarea')
		dateCreated(display: false)
    }

	public String toString()
	{
		String result = name.replaceAll(' ', '_')
		return result
	}
}
