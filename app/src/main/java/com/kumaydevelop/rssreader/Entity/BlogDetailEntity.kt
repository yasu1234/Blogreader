package com.kumaydevelop.rssreader.Entity

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
class BlogDetailEntity {

    @set:Element(name = "title", required = false)
    @get:Element(name = "title", required = false)
    var title: String? = null

    @set:Element(name = "link", required = false)
    @get:Element(name = "link", required = false)
    var link: String? = null

    @set:Element(name = "pubDate", required = false)
    @get:Element(name = "pubDate", required = false)
    var date: String? = null

    @set:Element(name = "description", required = false)
    @get:Element(name = "description", required = false)
    var description: String? = null
}