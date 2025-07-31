package com.chidozie.core.renderEngine

import net.adventuregame.models.RawModel
import net.adventuregame.models.TexturedModel
import com.chidozie.core.textures.ModelTexture
import org.lwjgl.assimp.*
import org.slf4j.LoggerFactory
import java.nio.IntBuffer

object AssimpModelLoader {
    private val log = LoggerFactory.getLogger("AssimpModelLoader")

    fun loadModel(path: String, loader: Loader): List<TexturedModel> {
        val scene = Assimp.aiImportFile(
            path,
            Assimp.aiProcess_Triangulate or
                    Assimp.aiProcess_GenSmoothNormals or
                    Assimp.aiProcess_FlipUVs or
                    Assimp.aiProcess_JoinIdenticalVertices
        ) ?: throw RuntimeException("Assimp failed to load model at path: $path")

        val models = mutableListOf<TexturedModel>()
        val meshCount = scene.mNumMeshes()
        val meshBuffer = scene.mMeshes() ?: return models

        for (i in 0 until meshCount) {
            val mesh = AIMesh.create(meshBuffer[i])
            val rawModel = convertToRawModel(mesh, loader)
            val texture = extractTexture(scene, mesh, loader, path)
            models.add(TexturedModel(rawModel, texture))
        }

        Assimp.aiReleaseImport(scene)
        return models
    }

    private fun convertToRawModel(mesh: AIMesh, loader: Loader): RawModel {
        val positions = FloatArray(mesh.mNumVertices() * 3)
        val texCoords = FloatArray(mesh.mNumVertices() * 2)
        val normals = FloatArray(mesh.mNumVertices() * 3)
        val indices = mutableListOf<Int>()

        for (v in 0 until mesh.mNumVertices()) {
            val pos = mesh.mVertices()[v]
            val normal = mesh.mNormals()?.get(v)
            val texCoord = mesh.mTextureCoords(0)?.get(v)

            positions[v * 3] = pos.x()
            positions[v * 3 + 1] = pos.y()
            positions[v * 3 + 2] = pos.z()

            normals[v * 3] = normal!!.x()
            normals[v * 3 + 1] = normal.y()
            normals[v * 3 + 2] = normal.z()

            if (texCoord != null) {
                texCoords[v * 2] = texCoord.x()
                texCoords[v * 2 + 1] = texCoord.y()
            }
        }

        for (f in 0 until mesh.mNumFaces()) {
            val face = mesh.mFaces()[f]
            for (j in 0 until face.mNumIndices()) {
                indices.add(face.mIndices()[j])
            }
        }

        val rawModel = loader.loadToVAO(positions, texCoords, normals, indices.toIntArray())
        return rawModel
    }

    private fun extractTexture(scene: AIScene, mesh: AIMesh, loader: Loader, modelPath: String): ModelTexture {
        val matIndex = mesh.mMaterialIndex()
        val materials = scene.mMaterials() ?: return ModelTexture(0)
        val material = AIMaterial.create(materials[matIndex])

        val pathBuf = AIString.calloc()
        Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_DIFFUSE, 0, pathBuf, null as IntBuffer, null, null, null, null, null)

        val texturePath = pathBuf.dataString()
        pathBuf.free()

        if (texturePath.isBlank()) {
            log.warn("No diffuse texture found for mesh.")
            return ModelTexture(0) // Or fallback texture
        }

        // Extract base directory from model path
        val baseDir = modelPath.substringBeforeLast('/', "")
        val resolvedTexturePath = if (texturePath.startsWith("*")) {
            log.warn("Embedded textures are not yet supported: $texturePath")
            return ModelTexture(0)
        } else {
            "$baseDir/$texturePath"
        }

        val cleanedPath = resolvedTexturePath
            .replace("file://", "")
            .replace("\\", "/") // normalize Windows paths

        val textureId = try {
            loader.loadGameTexture(cleanedPath.removePrefix("res/").removeSuffix(".png"))
        } catch (e: Exception) {
            log.error("Failed to load texture at path: $cleanedPath", e)
            0
        }

        return ModelTexture(textureId)
    }
}
