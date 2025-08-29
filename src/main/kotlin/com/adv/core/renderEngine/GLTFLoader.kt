package com.adv.core.renderEngine

import com.adv.core.textures.ModelTexture
import net.adventuregame.models.TexturedModel
import org.lwjgl.assimp.AIMaterial
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.AIString
import org.lwjgl.assimp.AITexture
import org.lwjgl.assimp.Assimp
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryUtil.*
import java.io.File

object GLTFLoader {
    fun loadGLB(path: String, loader: Loader): List<TexturedModel> {
        // 1. Call Assimp to import
        val flags = Assimp.aiProcess_Triangulate or Assimp.aiProcess_JoinIdenticalVertices or
                Assimp.aiProcess_FixInfacingNormals or Assimp.aiProcess_CalcTangentSpace or
                Assimp.aiProcess_PreTransformVertices
        val url = this::class.java.getResource("/assets/adventuregame/models/$path.glb")
        requireNotNull(url) { "GLB not found in classpath: $path" }
        val file = File(url.toURI())
        val scene = Assimp.aiImportFile(file.absolutePath, flags)
        require(scene != null) { "Failed to load GLB: $path" }

        // 2. Load textures (materials)
        val materials = mutableListOf<ModelTexture>()
        for (i in 0 until scene.mNumMaterials()) {
            val aiMat = AIMaterial.create(scene.mMaterials()!!.get(i))
            // e.g. get diffuse texture path
            val aiPath = AIString.calloc()
            val mapping = memAllocInt(1)
            val uvIndex = memAllocInt(1)
            val blend = memAllocFloat(1)
            val op = memAllocInt(1)
            val mapMode = memAllocInt(1)
            val flags = memAllocInt(1)

            Assimp.aiGetMaterialTexture(
                aiMat,
                Assimp.aiTextureType_DIFFUSE,
                0,
                aiPath,
                mapping,
                uvIndex,
                blend,
                op,
                mapMode,
                flags
            )

            // You can now read values from these buffers, then:

            memFree(mapping)
            memFree(uvIndex)
            memFree(blend)
            memFree(op)
            memFree(mapMode)
            memFree(flags)
            aiPath.free() // AIString uses .free()
            val tex = if (!aiPath.dataString().isNullOrEmpty()) {
                // Load texture via STB (like your loadFontTextureAtlas or loadGameTexture)
                val path = aiPath.dataString()
                val texId: Int
                if (path.startsWith("*")) {
                    val texIndex = path.substring(1).toInt()
                    val aiTexPtr = scene.mTextures()?.get(texIndex) ?: throw RuntimeException("Embedded texture *$texIndex not found")
                    val aiTex = AITexture.create(aiTexPtr)

                    val isCompressed = aiTex.mHeight() == 0
                    if (isCompressed) {
                        // Compressed texture like PNG or JPG
                        val widthInBytes = aiTex.mWidth()
                        val dataPtr = aiTex.pcData() // returns a ByteBuffer for compressed
                        val data = memByteBuffer(dataPtr.address(), widthInBytes)

                        val x = IntArray(1)
                        val y = IntArray(1)
                        val comp = IntArray(1)
                        val image = STBImage.stbi_load_from_memory(data, x, y, comp, 4)
                            ?: throw RuntimeException("Failed to load embedded texture: ${STBImage.stbi_failure_reason()}")

                        texId = loader.createOpenGLTexture(image, x[0], y[0])
                        STBImage.stbi_image_free(image)
                    } else {
                        // Uncompressed raw texture (RGBA32 format)
                        val width = aiTex.mWidth()
                        val height = aiTex.mHeight()
                        val pixelData = aiTex.pcData() // returns IntBuffer

                        val buffer = memAlloc(width * height * 4)
                        for (i in 0 until width * height) {
                            val pixel = aiTex.pcData()[i]  // AITexel
                            buffer.put(pixel.r()) //R
                            buffer.put(pixel.g()) //G
                            buffer.put(pixel.b()) //B
                            buffer.put(pixel.a()) //A
                        }
                        buffer.flip()

                        texId = loader.createOpenGLTexture(buffer, width, height)
                        memFree(buffer)
                    }
                } else {
                    // Load from disk normally
                    texId = loader.loadGameTexture(path)
                }
                ModelTexture(texId)
            } else {
                ModelTexture(0) // or some default
            }
            materials.add(tex)
        }

        // 3. For each mesh, extract vertex data and create RawModel
        val models = mutableListOf<TexturedModel>()
        val meshes = scene.mMeshes()
        for (mi in 0 until scene.mNumMeshes()) {
            val aiMesh = AIMesh.create(meshes!!.get(mi))
            // Extract positions, normals, texCoords, indices from aiMesh
            val positions = extractVertices(aiMesh)
            val normals   = extractNormals(aiMesh)
            val uvs       = extractTexCoords(aiMesh)
            val indices   = extractIndices(aiMesh)
            // Use existing Loader to load into VAO
            val rawModel = loader.loadToVAO(positions, uvs, normals, indices)
            // Determine material for this mesh
            val matIndex = aiMesh.mMaterialIndex()
            val matTex = if (matIndex >= 0 && matIndex < materials.size) materials[matIndex] else ModelTexture(0)
            // Create textured model
            models.add(TexturedModel(rawModel, matTex))
        }
        return models
    }

    fun extractVertices(mesh: AIMesh): FloatArray {
        val buffer = mesh.mVertices()
        val vertices = FloatArray(mesh.mNumVertices() * 3)
        for (i in 0 until mesh.mNumVertices()) {
            val vec = buffer[i]
            vertices[i * 3]     = vec.x()
            vertices[i * 3 + 1] = vec.y()
            vertices[i * 3 + 2] = vec.z()
        }
        return vertices
    }

    fun extractNormals(mesh: AIMesh): FloatArray {
        val buffer = mesh.mNormals()
        val normals = FloatArray(mesh.mNumVertices() * 3)
        for (i in 0 until mesh.mNumVertices()) {
            val vec = buffer?.get(i)
            normals[i * 3]     = vec!!.x()
            normals[i * 3 + 1] = vec.y()
            normals[i * 3 + 2] = vec.z()
        }
        return normals
    }

    fun extractTexCoords(mesh: AIMesh): FloatArray {
        val buffer = mesh.mTextureCoords(0) ?: return FloatArray(mesh.mNumVertices() * 2)
        val texCoords = FloatArray(mesh.mNumVertices() * 2)
        for (i in 0 until mesh.mNumVertices()) {
            val vec = buffer[i]
            texCoords[i * 2]     = vec.x()
            texCoords[i * 2 + 1] = vec.y()
        }
        return texCoords
    }

    fun extractIndices(mesh: AIMesh): IntArray {
        val faceCount = mesh.mNumFaces()
        val faces = mesh.mFaces()
        val indices = mutableListOf<Int>()
        for (i in 0 until faceCount) {
            val face = faces[i]
            for (j in 0 until face.mNumIndices()) {
                indices.add(face.mIndices().get(j))
            }
        }
        return indices.toIntArray()
    }
}
