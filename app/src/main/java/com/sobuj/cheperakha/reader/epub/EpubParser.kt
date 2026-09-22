package com.sobuj.cheperakha.reader.epub

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import javax.xml.parsers.SAXParserFactory

/**
 * Parses an EPUB file and extracts metadata, chapters, and resources.
 * Works with both EPUB 2.0 and EPUB 3.0 specifications.
 */
class EpubParser(private val context: Context) {

    private var epubZip: ZipFile? = null
    private var basePath = "book/"

    /**
     * Opens and parses an EPUB from assets.
     */
    fun openEpubFromAssets(assetPath: String) {
        basePath = assetPath.removeSuffix("/") + "/"
        val zipStream = context.assets.open(assetPath)
        val tempFile = context.cacheDir.resolve("temp_epub.zip")
        zipStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        epubZip = ZipFile(tempFile)
    }

    /**
     * Opens and parses an EPUB from a file path (e.g., Downloads).
     */
    fun openEpubFromFile(filePath: String) {
        epubZip = ZipFile(filePath)
    }

    /**
     * Reads metadata from the OPF file.
     */
    fun readMetadata(): BookMetadata {
        val opfEntry = findOpfFile() ?: throw IllegalStateException("No OPF file found in EPUB")
        val opfStream = epubZip!!.getInputStream(opfEntry)
        val opfContent = opfStream.bufferedReader().use { it.readText() }

        val parser = newPullParser()
        parser.setInput(opfContent.byteInputStream(), "UTF-8")

        var title = "Unknown"
        var author = "Unknown"
        var language = "en"
        var coverHref: String? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when {
                eventType == XmlPullParser.START_TAG && parser.name == "title" -> {
                    title = parser.nextText().trim()
                }
                eventType == XmlPullParser.START_TAG && parser.name == "creator" -> {
                    author = parser.nextText().trim()
                }
                eventType == XmlPullParser.START_TAG && parser.name == "language" -> {
                    language = parser.nextText().trim()
                }
                eventType == XmlPullParser.START_TAG && parser.name == "meta" &&
                    parser.getAttributeValue(null, "name") == "cover" -> {
                    coverHref = parser.getAttributeValue(null, "content")
                }
            }
            eventType = parser.next()
        }

        return BookMetadata(title, author, language, coverHref)
    }

    /**
     * Reads the table of contents from NCX or nav element.
     */
    fun readTableOfContents(): List<TocEntry> {
        val entries = mutableListOf<TocEntry>()

        // Try NCX first (EPUB 2.0)
        val ncxEntry = epubZip!!.entries().asSequence()
            .filter { it.name.endsWith(".ncx") }
            .firstOrNull()

        if (ncxEntry != null) {
            val ncxStream = epubZip!!.getInputStream(ncxEntry)
            val ncxContent = ncxStream.bufferedReader().use { it.readText() }
            entries.addAll(parseNcxToc(ncxContent))
            return entries
        }

        // Try EPUB 3.0 nav element
        val htmlFiles = epubZip!!.entries().asSequence()
            .filter { it.name.endsWith(".xhtml") || it.name.endsWith(".html") }
            .toList()

        for (htmlFile in htmlFiles) {
            val htmlStream = epubZip!!.getInputStream(htmlFile)
            val htmlContent = htmlStream.bufferedReader().use { it.readText() }
            if (htmlContent.contains("nav") && htmlContent.contains("toc")) {
                entries.addAll(parseHtmlNavToc(htmlContent, htmlFile.name))
                break
            }
        }

        // Fallback: use spine order
        if (entries.isEmpty()) {
            entries.addAll(parseSpineOrder())
        }

        return entries
    }

    /**
     * Gets all chapter HTML content indexed by their href.
     */
    fun getChapterContents(): Map<String, String> {
        val contents = mutableMapOf<String, String>()
        
        epubZip!!.entries().asSequence()
            .filter { it.name.endsWith(".xhtml") || it.name.endsWith(".html") }
            .forEach { entry ->
                val stream = epubZip!!.getInputStream(entry)
                contents[entry.name] = stream.bufferedReader().use { it.readText() }
            }
        
        return contents
    }

    /**
     * Gets the cover image as a Bitmap.
     */
    fun getCoverImage(): Bitmap? {
        val coverEntry = epubZip!!.entries().asSequence()
            .firstOrNull { 
                val name = it.name.lowercase()
                name.contains("cover") && (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) 
            }

        return coverEntry?.let { entry ->
            val stream = epubZip!!.getInputStream(entry)
            BitmapFactory.decodeStream(stream)
        }
    }

    /**
     * Gets CSS styles from the EPUB.
     */
    fun getStylesheets(): Map<String, String> {
        val css = mutableMapOf<String, String>()
        
        epubZip!!.entries().asSequence()
            .filter { it.name.endsWith(".css") }
            .forEach { entry ->
                val stream = epubZip!!.getInputStream(entry)
                css[entry.name] = stream.bufferedReader().use { it.readText() }
            }
        
        return css
    }

    /**
     * Finds the OPF file in the EPUB.
     */
    private fun findOpfFile(): ZipEntry? {
        // Try container.xml first
        val containerEntry = epubZip!!.getEntry("META-INF/container.xml")
        if (containerEntry != null) {
            val containerStream = epubZip!!.getInputStream(containerEntry)
            val containerContent = containerStream.bufferedReader().use { it.readText() }
            
            val parser = newPullParser()
            parser.setInput(containerContent.byteInputStream(), "UTF-8")
            
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "rootfile") {
                    val fullPath = parser.getAttributeValue(null, "full-path")
                    return epubZip!!.getEntry(fullPath)
                }
                eventType = parser.next()
            }
        }
        
        // Fallback: search for .opf file
        return epubZip!!.entries().asSequence()
            .firstOrNull { it.name.endsWith(".opf") }
    }

    /**
     * Parses NCX navigation markup.
     */
    private fun parseNcxToc(content: String): List<TocEntry> {
        val entries = mutableListOf<TocEntry>()
        val parser = newPullParser()
        parser.setInput(content.byteInputStream(), "UTF-8")

        var currentTitle = ""
        var currentHref = ""
        var inNavLabel = false
        var depth = 0

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when {
                eventType == XmlPullParser.START_TAG && parser.name == "navPoint" -> {
                    depth++
                    currentTitle = ""
                    currentHref = ""
                }
                eventType == XmlPullParser.START_TAG && parser.name == "navLabel" -> {
                    inNavLabel = true
                }
                eventType == XmlPullParser.START_TAG && parser.name == "content" -> {
                    currentHref = parser.getAttributeValue(null, "src")
                }
                eventType == XmlPullParser.TEXT && inNavLabel -> {
                    currentTitle += parser.text
                }
                eventType == XmlPullParser.END_TAG && parser.name == "navPoint" -> {
                    if (currentTitle.isNotEmpty()) {
                        entries.add(TocEntry(currentTitle.trim(), currentHref.trim(), depth))
                    }
                    inNavLabel = false
                }
            }
            eventType = parser.next()
        }
        return entries
    }

    /**
     * Parses HTML nav element for TOC.
     */
    private fun parseHtmlNavToc(content: String, htmlFile: String): List<TocEntry> {
        val entries = mutableListOf<TocEntry>()
        
        // Simple regex-based parsing for nav elements
        val navPattern = Regex("""<li[^>]*>\s*<a[^>]*href="([^"]+)"[^>]*>(.*?)</a>\s*</li>""", 
            RegexOption.DOT_MATCHES_ALL)
        
        navPattern.findAll(content).forEach { match ->
            val href = match.groupValues[1]
            val title = match.groupValues[2].replace("<[^>]+>".toRegex(), "").trim()
            if (title.isNotEmpty() && href.isNotEmpty()) {
                entries.add(TocEntry(title, href, 0))
            }
        }
        
        return entries
    }

    /**
     * Fallback: parse spine order from OPF.
     */
    private fun parseSpineOrder(): List<TocEntry> {
        val entries = mutableListOf<TocEntry>()
        val opfEntry = findOpfFile() ?: return entries
        
        val opfStream = epubZip!!.getInputStream(opfEntry)
        val opfContent = opfStream.bufferedReader().use { it.readText() }
        
        val parser = newPullParser()
        parser.setInput(opfContent.byteInputStream(), "UTF-8")
        
        var inSpine = false
        var currentItemHref: String? = null
        
        val manifestItems = mutableMapOf<String, String>()
        
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when {
                eventType == XmlPullParser.START_TAG && parser.name == "manifest" -> inSpine = false
                eventType == XmlPullParser.START_TAG && parser.name == "spine" -> inSpine = true
                eventType == XmlPullParser.START_TAG && parser.name == "item" && !inSpine -> {
                    val id = parser.getAttributeValue(null, "id")
                    val href = parser.getAttributeValue(null, "href")
                    if (id != null && href != null) {
                        manifestItems[id] = href
                    }
                }
                eventType == XmlPullParser.START_TAG && parser.name == "itemref" && inSpine -> {
                    val itemRefId = parser.getAttributeValue(null, "idref")
                    currentItemHref = itemRefId?.let { manifestItems[it] }
                }
                eventType == XmlPullParser.END_TAG && parser.name == "itemref" && inSpine -> {
                    if (currentItemHref != null) {
                        entries.add(TocEntry("Chapter ${entries.size + 1}", currentItemHref!!, 0))
                    }
                }
            }
            eventType = parser.next()
        }
        
        return entries
    }

    /**
     * Searches text content for a query.
     */
    fun searchContent(query: String): List<SearchResult> {
        val results = mutableListOf<SearchResult>()
        val contents = getChapterContents()
        val lowerQuery = query.lowercase()
        
        contents.forEach { (filename, html) ->
            val textOnly = html.replace("<[^>]+>".toRegex(), " ")
                .replace("\\s+".toRegex(), " ").trim()
            
            if (textOnly.lowercase().contains(lowerQuery)) {
                val index = textOnly.lowercase().indexOf(lowerQuery)
                val snippetStart = maxOf(0, index - 50)
                val snippetEnd = minOf(textOnly.length, index + query.length + 50)
                val snippet = textOnly.substring(snippetStart, snippetEnd)
                
                results.add(SearchResult(
                    filename = filename,
                    snippet = "...$snippet...",
                    context = textOnly
                ))
            }
        }
        
        return results
    }

    /**
     * Closes the EPUB file handle.
     */
    fun close() {
        epubZip?.close()
        epubZip = null
    }

    private fun newPullParser(): XmlPullParser {
        return XmlPullParserFactory.newInstance().also { it.isNamespaceAware = true }.newPullParser()
    }

    data class TocEntry(val title: String, val href: String, val depth: Int)
    data class SearchResult(val filename: String, val snippet: String, val context: String)
}

/**
 * EPUB metadata extracted from the package file.
 */
data class BookMetadata(
    val title: String,
    val author: String,
    val language: String,
    val coverHref: String? = null
)
