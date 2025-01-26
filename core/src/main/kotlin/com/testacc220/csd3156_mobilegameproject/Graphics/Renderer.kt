import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.XmlReader

data class SubTexture(
    val name: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

class TextureAtlasLoader(atlasPath: String, xmlPath: String) {
    private val texture: Texture = Texture(Gdx.files.internal(atlasPath))
    private val regions: MutableMap<String, TextureRegion> = mutableMapOf()

    init {
        loadAtlas(xmlPath)
    }

    private fun loadAtlas(xmlPath: String) {
        try {
            val xml = XmlReader()
            val file = Gdx.files.internal(xmlPath)
            val root = xml.parse(file)

            for (subTexture in root.getChildrenByName("SubTexture")) {
                val name = subTexture.getAttribute("name")
                val x = subTexture.getIntAttribute("x")
                val y = subTexture.getIntAttribute("y")
                val width = subTexture.getIntAttribute("width")
                val height = subTexture.getIntAttribute("height")

                val region = TextureRegion(texture, x, y, width, height)
                regions[name] = region
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getRegion(name: String): TextureRegion? = regions[name]

    fun getAllRegions(): Map<String, TextureRegion> = regions

    fun dispose() {
        texture.dispose()
    }
}