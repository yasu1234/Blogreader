package com.kumaydevelop.blogreader.Entity

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name= "rss", strict = false)
class BlogEntity {

    @set:Element(name = "title", required = true)
    @get:Element(name = "title", required = true)
    var title: String? = ""

    @set:Element(name = "lastBuildDate", required = false)
    @get:Element(name = "lastBuildDate", required = false)
    var lastBuildDate: String? = ""

    @set:ElementList(entry = "item", inline = true, required = false)
    @get:ElementList(entry = "item", inline = true, required = false)
    var articleEntities: List<BlogDetailEntity>? = null

    @set:Element(name = "channel", required = false)
    @get:Element(name = "channel", required = false)
    var channel: String? = ""
}