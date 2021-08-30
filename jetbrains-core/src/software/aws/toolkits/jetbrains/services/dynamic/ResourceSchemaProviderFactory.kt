// Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.services.dynamic

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import com.jetbrains.jsonSchema.extension.SchemaType
import com.jetbrains.jsonSchema.impl.JsonSchemaVersion
import software.aws.toolkits.jetbrains.core.getResourceNow

class ResourceSchemaProviderFactory : JsonSchemaProviderFactory {
    override fun getProviders(project: Project): List<JsonSchemaFileProvider> {
        val schemaProviders = mutableListOf<JsonSchemaFileProvider>()
        DynamicResourceSchemaMapping.getInstance().getCurrentlyActiveResourceTypes().forEach {
            val schemaFile = object : JsonSchemaFileProvider {
                override fun isAvailable(file: VirtualFile): Boolean =
                    file is DynamicResourceVirtualFile && file.getResourceIdentifier().resourceType == it

                override fun getName(): String = "$it schema"

                override fun getSchemaFile(): VirtualFile? {
                    val schema = project.getResourceNow(DynamicResources.getResourceSchema(project, it))
                    return LocalFileSystem.getInstance().findFileByNioFile(schema.toPath())
                }

                override fun getSchemaVersion(): JsonSchemaVersion = JsonSchemaVersion.SCHEMA_7

                override fun getSchemaType(): SchemaType = SchemaType.userSchema
            }
            schemaProviders.add(schemaFile)
        }
        return schemaProviders
    }
}
