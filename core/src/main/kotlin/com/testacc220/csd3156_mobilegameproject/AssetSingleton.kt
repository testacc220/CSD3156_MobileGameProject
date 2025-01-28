package com.testacc220.csd3156_mobilegameproject

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader

object AssetSingleton {
    val assetManager: AssetManager = AssetManager(InternalFileHandleResolver()).apply {
        setLoader(
            com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator::class.java,
            FreeTypeFontGeneratorLoader(InternalFileHandleResolver())
        )
        setLoader(
            com.badlogic.gdx.graphics.g2d.BitmapFont::class.java,
            ".ttf",
            FreetypeFontLoader(InternalFileHandleResolver())
        )
    }
}
